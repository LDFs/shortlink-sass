package com.moreo.shorlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moreo.shorlink.admin.dao.entity.GroupDO;
import com.moreo.shorlink.admin.dto.req.GroupOrderDTO;
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
     * 新增短链接分组
     * @param userName 用户名
     * @param name 分组名称
     */
    void saveGroup(String userName, String name);

    /**
     * 查询用户的短链接分组
     */
    List<GroupRespDTO> listGroup();

    /**
     * 修改短链接分组
     */
    void updateGroup(GroupUpdateDTO requestParam);

    /**
     * 删除短链接分组
     */
    void deleteGroup(String gid);

    /**
     * 排序短链接分组
     */
    void orderGroup(List<GroupOrderDTO> requestParam);
}
