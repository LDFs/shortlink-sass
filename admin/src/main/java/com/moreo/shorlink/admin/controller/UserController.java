package com.moreo.shorlink.admin.controller;

import com.moreo.shorlink.admin.common.convention.result.Result;
import com.moreo.shorlink.admin.common.enums.UserErrorCodeEnum;
import com.moreo.shorlink.admin.dto.resp.UserRespDTO;
import com.moreo.shorlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    // @RequiredArgsConstructor 会自动为所有 final 字段生成构造方法，Spring 通过构造器完成注入。
    private final UserService userService;

    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        UserRespDTO userRespDTO = userService.getUserByUsername(username);
        if(userRespDTO == null){
            return new Result<UserRespDTO>().setCode(UserErrorCodeEnum.USER_NULL.code()).setMessage(UserErrorCodeEnum.USER_NULL.message());
        }else {
            return new Result<UserRespDTO>().setCode("0").setData(userRespDTO);
        }

    }
}
