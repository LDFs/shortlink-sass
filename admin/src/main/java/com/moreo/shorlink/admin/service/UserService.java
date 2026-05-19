package com.moreo.shorlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moreo.shorlink.admin.dao.entity.UserDO;
import com.moreo.shorlink.admin.dto.req.UserLoginReqDTO;
import com.moreo.shorlink.admin.dto.req.UserRegisterReqDTO;
import com.moreo.shorlink.admin.dto.req.UserUpdateReqDTO;
import com.moreo.shorlink.admin.dto.resp.UserActualRespDTO;
import com.moreo.shorlink.admin.dto.resp.UserLoginRespDTO;
import com.moreo.shorlink.admin.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 获取用户的无脱敏的真实信息
     * @param username 用户名
     * @return  用户返回实体
     */
    UserActualRespDTO getUserActualByUsername(String username);

    /**
     * 查询用户名是否存在
     * @param username 用户名
     * @return 是否存在，存在返回 True, 不存在返回 False
     */
    Boolean hasUsername(String username);

    /**
     * 注册用户
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 更改用户
     */
    void update(UserUpdateReqDTO requestParam);

    /**
     * 用户登陆
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 检查用户是否登陆
     */
    Boolean checkLogin(String username, String token);

    /**
     * 用户退出登陆
     */
    void logout(String username, String token);
}
