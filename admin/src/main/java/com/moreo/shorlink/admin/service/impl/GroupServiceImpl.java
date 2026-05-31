package com.moreo.shorlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moreo.shorlink.admin.common.biz.user.UserContext;
import com.moreo.shorlink.admin.common.convention.result.Result;
import com.moreo.shorlink.admin.dao.entity.GroupDO;
import com.moreo.shorlink.admin.dao.mapper.GroupMapper;
import com.moreo.shorlink.admin.dto.req.GroupOrderDTO;
import com.moreo.shorlink.admin.dto.req.GroupUpdateDTO;
import com.moreo.shorlink.admin.dto.resp.GroupRespDTO;
import com.moreo.shorlink.admin.remote.ShortLinkRemoteService;
import com.moreo.shorlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.moreo.shorlink.admin.service.GroupService;
import com.moreo.shorlink.admin.util.RandomGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    ShortLinkRemoteService shortLinkremoteService = new ShortLinkRemoteService() {
    };

    @Override
    public void saveGroup(String groupName) {
        saveGroup(UserContext.getUsername(), groupName);
    }

    @Override
    public void saveGroup(String userName, String groupName) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, userName)
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        int retryCount = 0;
        int maxRetries = 10;
        String gid = null;
        while (retryCount < maxRetries) {
            gid = saveGroupUniqueReturnGid();
            if(StrUtil.isNotEmpty(gid)){
                GroupDO groupDO = GroupDO.builder()
                        .gid(gid)
                        .sortOrder(0)
                        .username(userName)
                        .name(groupName)
                        .build();
                baseMapper.insert(groupDO);
                break;
            }
            retryCount++;
        }
    }

    @Override
    public List<GroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        // 1. 获取该用户的所有短链接分组
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);

        // 2. 将所有分组的 Gid 转为 List 传给 listGroupShortLinkCount 来获取所有分组内的短链接数量
        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkremoteService
                .listGroupShortLinkCount(groupDOList.stream().map(GroupDO::getGid).toList());

        // 3. 将分组内短链接数量放入结果List中
        List<GroupRespDTO> groupRespDTOList = BeanUtil.copyToList(groupDOList, GroupRespDTO.class);
        groupRespDTOList.forEach(each -> {
            Optional<ShortLinkGroupCountQueryRespDTO> first = listResult.getData().stream()
                    .filter(item -> Objects.equals(each.getGid(), item.getGid()))
                    .findFirst();
            first.ifPresent(item -> each.setShortLinkCount(first.get().getShortLinkCount()));
        });

        return groupRespDTOList;
    }

    @Override
    public void updateGroup(GroupUpdateDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getGid, requestParam.getGid());
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void deleteGroup(String gud) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getGid, gud);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void orderGroup(List<GroupOrderDTO> requestParam) {
        requestParam.forEach(item -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(item.getSortOrder())
                    .build();
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, item.getGid())
                    .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(groupDO, updateWrapper);
        });
    }

    private String saveGroupUniqueReturnGid() {
        String gid = RandomGenerator.generateRandom();
        return gid;
    }
}
