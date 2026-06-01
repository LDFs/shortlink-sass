package com.moreo.shorlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moreo.shorlink.project.common.convention.exception.ClientException;
import com.moreo.shorlink.project.common.convention.exception.ServiceException;
import com.moreo.shorlink.project.common.enums.ShortLinkValidType;
import com.moreo.shorlink.project.dao.entity.ShortLinkDO;
import com.moreo.shorlink.project.dao.entity.ShortLinkGotoDO;
import com.moreo.shorlink.project.dao.mapper.ShortLinkGotoMapper;
import com.moreo.shorlink.project.dao.mapper.ShortLinkMapper;
import com.moreo.shorlink.project.dto.req.ShortLinkCreateReqDTO;
import com.moreo.shorlink.project.dto.req.ShortLinkPageReqDTO;
import com.moreo.shorlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkPageRespDTO;
import com.moreo.shorlink.project.service.ShortLinkService;
import com.moreo.shorlink.project.util.HashUtil;
import com.moreo.shorlink.project.util.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.moreo.shorlink.project.common.constant.RedisKeyConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkCreateCache;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortLink = StrBuilder.create(requestParam.getDomain())
                .append("/")
                .append(shortLinkSuffix)
                .toString();
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(requestParam.getDomain())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .shortUri(shortLinkSuffix)
                .enableStatus(0)
                .fullShortUrl(fullShortLink)
                .build();
        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .gid(requestParam.getGid())
                .fullShortUrl(fullShortLink)
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(shortLinkGotoDO);
        }catch (DuplicateKeyException e) {
            // 由于一些原因，生成的短链接存在数据库中，但是没在布隆过滤器中
            if(!shortLinkCreateCache.contains(fullShortLink)) {
                shortLinkCreateCache.add(fullShortLink);
            }
            throw new ServiceException(String.format("短链接：%s 生成重复", fullShortLink));
        }
        shortLinkCreateCache.add(fullShortLink);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pagerShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0);
        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + each.getDomain());
            return result;
        });
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid, count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .eq("del_flag", 0)
                .groupBy("gid");
        List<Map<String, Object>> result = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(result, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        // 1. 查找数据库中的原始数据，主要根据 gid 和 fullShortUrl
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
        if(hasShortLinkDO == null) {
            throw new ClientException("短链接记录不存在");
        }
        // 创建新的短链接实体
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(hasShortLinkDO.getDomain())
                .fullShortUrl(hasShortLinkDO.getFullShortUrl())
                .shortUri(hasShortLinkDO.getShortUri())
                .createdType(hasShortLinkDO.getCreatedType())
                .clickNum(hasShortLinkDO.getClickNum())
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .build();
        if(Objects.equals(hasShortLinkDO.getGid(), requestParam.getGid())) {
            // 2. 如果没有修改短链接分组
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .set(Objects.equals(requestParam.getValidDateType(), ShortLinkValidType.PERMANENT.getType()), ShortLinkDO::getValidDate, null);

            baseMapper.update(shortLinkDO, updateWrapper);
        }else {
            // 3. 如果修改了分组
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getOriginUrl())
                    .eq(ShortLinkDO::getGid, hasShortLinkDO.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            baseMapper.delete(updateWrapper);
            baseMapper.insert(shortLinkDO);
        }
    }

    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) {
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        // 1. 从缓冲中拿原始链接记录
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
        try {
            // 2. 缓存中有，直接返回
            if(StringUtil.isNotBlank(originalLink)) {
                ((HttpServletResponse) response).sendRedirect(originalLink);
                return;
            }
        }catch (Exception e){
            throw new ClientException(e.getMessage());
        }
        // 使用布隆过滤器，判断记录是否在数据库中
        boolean contains = shortLinkCreateCache.contains(fullShortUrl);
        if(!contains) {
            return;
        }
        // 从缓存中查找这个记录是否是空值
        String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
        if(StringUtil.isNotBlank(gotoIsNullShortLink)) {
            return;
        }
        // 3. 加锁，防止缓存击穿
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            // 4. 为预防前面一个人已经把新的记录放到缓存中了，这里再从缓存中读一次
            originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            // 5. 有了，直接返回
            if(StringUtil.isNotBlank(originalLink)) {
                ((HttpServletResponse) response).sendRedirect(originalLink);
                return;
            }
            // 又从缓存中查找这个记录是否是空值
            gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
            if(StringUtil.isNotBlank(gotoIsNullShortLink)) {
                return;
            }
            // 6. 从数据库中查找。先从 goto 表中找对应的 gid，再从 link 表中找原始链接
            LambdaQueryWrapper<ShortLinkGotoDO> linkGotoDOLambdaQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoDOLambdaQueryWrapper);
            if(shortLinkGotoDO == null) {
                // 数据库中没有记录，就在缓存中保存 这个记录是控制，防止下次又要从数据库中查找它
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.MINUTES);
                throw new ClientException("短链接记录不存在");
            }
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
            if(hasShortLinkDO == null || (hasShortLinkDO.getValidDate() != null && hasShortLinkDO.getValidDate().before(new Date()))) {
                // 数据库中没有记录，就在缓存中保存 这个记录是控制，防止下次又要从数据库中查找它
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.MINUTES);
                throw new ClientException("短链接记录不存在");
            }
            // 7. 存进缓存中，返回跳转链接
            stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                    hasShortLinkDO.getOriginUrl(),
                    LinkUtil.getLinkCacheValidTime(hasShortLinkDO.getValidDate()),
                    TimeUnit.MILLISECONDS
                    );
            ((HttpServletResponse) response).sendRedirect(hasShortLinkDO.getOriginUrl());
        }catch (Exception e){
            throw new ClientException(e.getMessage());
        }finally {
            lock.unlock();
        }

    }

    String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        int generateCount = 0;
        String shortUri;
        while (true) {
            if(generateCount >= 10) {
                throw new ServiceException("短链接生成失败，请稍后再试");
            }
            String originUrl = requestParam.getOriginUrl();
            originUrl += UUID.randomUUID().toString().replaceAll("-", "");
            shortUri = HashUtil.hashToBase62(originUrl);

            if(!shortLinkCreateCache.contains(requestParam.getDomain() + "/" + shortUri)) {
                break;
            }
            generateCount++;
        }
        return shortUri;
    }
}
