package com.moreo.shorlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moreo.shorlink.admin.dao.entity.UserDO;
import com.moreo.shorlink.admin.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

}
