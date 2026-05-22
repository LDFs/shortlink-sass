package com.moreo.shorlink.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moreo.shorlink.admin.dao.entity.GroupDO;
import com.moreo.shorlink.admin.dao.mapper.GroupMapper;
import com.moreo.shorlink.admin.service.GroupService;
import com.moreo.shorlink.admin.util.RandomGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {



    @Override
    public void saveGroup(String groupName) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, "")
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
                        .username("")
                        .name(groupName)
                        .build();
                baseMapper.insert(groupDO);
                break;
            }
            retryCount++;
        }
    }

    private String saveGroupUniqueReturnGid() {
        String gid = RandomGenerator.generateRandom();
        return gid;
    }
}
