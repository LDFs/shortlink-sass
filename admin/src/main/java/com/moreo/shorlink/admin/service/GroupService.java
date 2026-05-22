package com.moreo.shorlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moreo.shorlink.admin.dao.entity.GroupDO;

public interface GroupService extends IService<GroupDO> {

    /**
     * 新增短链接分组
     * @param name 分组名称
     */
    void saveGroup(String name);
}
