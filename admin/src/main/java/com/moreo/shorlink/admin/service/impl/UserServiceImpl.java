package com.moreo.shorlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moreo.shorlink.admin.common.convention.exception.ClientException;
import com.moreo.shorlink.admin.common.enums.UserErrorCodeEnum;
import com.moreo.shorlink.admin.dao.entity.UserDO;
import com.moreo.shorlink.admin.dao.mapper.UserMapper;
import com.moreo.shorlink.admin.dto.req.UserLoginReqDTO;
import com.moreo.shorlink.admin.dto.req.UserRegisterReqDTO;
import com.moreo.shorlink.admin.dto.req.UserUpdateReqDTO;
import com.moreo.shorlink.admin.dto.resp.UserActualRespDTO;
import com.moreo.shorlink.admin.dto.resp.UserLoginRespDTO;
import com.moreo.shorlink.admin.dto.resp.UserRespDTO;
import com.moreo.shorlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.moreo.shorlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.moreo.shorlink.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;
import static com.moreo.shorlink.admin.common.enums.UserErrorCodeEnum.USER_NAME_EXIST;
import static com.moreo.shorlink.admin.common.enums.UserErrorCodeEnum.USER_SAVE_ERROR;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate  stringRedisTemplate;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if(userDO == null) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        UserRespDTO userRespDTO = new UserRespDTO();
        BeanUtils.copyProperties(userDO,userRespDTO);
        return userRespDTO;
    }

    @Override
    public UserActualRespDTO getUserActualByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if(userDO == null) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        UserActualRespDTO userRespDTO = new UserActualRespDTO();
        BeanUtils.copyProperties(userDO,userRespDTO);
        return userRespDTO;
    }

    @Override
    public Boolean hasUsername(String username) {
        // 使用接口查询数据库中用户名是否已存在
//        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
//                .eq(UserDO::getUsername, username);
//        UserDO userDO = baseMapper.selectOne(queryWrapper);
//        return Objects.nonNull(userDO);

        // 使用布隆过滤器来查询用户名是否已存在。在注册时使用
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if(hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST);
        }

        // 使用分布式锁，来防止在短时间内多个同一用户名的注册
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        if(!lock.tryLock()) {
            throw new ClientException(USER_NAME_EXIST);
        }
        try {
            int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
            if(inserted <= 0) {
                throw new ClientException(USER_SAVE_ERROR);
            }

            userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
        }catch (DuplicateKeyException e) {
            throw new ClientException(USER_SAVE_ERROR);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        // TODO 验证修改的用户是否为当前登陆用户
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), updateWrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if(userDO == null) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        // 检查用户是否已经登陆
        Map<Object, Object> hasLoginMap = stringRedisTemplate.opsForHash().entries(USER_LOGIN_KEY+requestParam.getUsername());
        if(CollUtil.isNotEmpty(hasLoginMap)) {
            stringRedisTemplate.expire(USER_LOGIN_KEY+requestParam.getUsername(), 30L, TimeUnit.MINUTES);
            String token = hasLoginMap
                    .keySet()
                    .stream()
                    .findFirst()
                    .map(Object::toString).orElseThrow(() -> new ClientException(UserErrorCodeEnum.USER_NULL));
            return new UserLoginRespDTO(token);
        }
        String uuid = UUID.randomUUID().toString();
        // 存储到 redis 缓存中
        stringRedisTemplate.opsForHash().put(USER_LOGIN_KEY+requestParam.getUsername(), uuid, JSON.toJSONString(userDO));
        // 设置token的过期时间
        stringRedisTemplate.expire(USER_LOGIN_KEY+requestParam.getUsername(), 30L, TimeUnit.MINUTES);
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get(USER_LOGIN_KEY+username, token) != null;
    }

    @Override
    public void logout(String username, String token) {
        if(checkLogin(username, token)) {
            stringRedisTemplate.opsForHash().delete(USER_LOGIN_KEY+username, token);
            return;
        }
        throw new ClientException("用户Token不存在或用户未登录");
    }
}
