package com.moreo.shorlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moreo.shorlink.admin.dao.entity.GroupDO;
import com.moreo.shorlink.admin.dto.req.GroupUpdateDTO;
import com.moreo.shorlink.admin.dto.resp.GroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {

    /**
     * 新增短链接分组
     * @param name 分组名称
     */
    void saveGroup(String name);

    /**
     * 查询用户的短链接分组
     */
    List<GroupRespDTO> listGroup();

    /**
     * 修改短链接分组
     */
    void updateGroup(GroupUpdateDTO requestParam);
}
