package com.moreo.shorlink.admin.controller;

import com.moreo.shorlink.admin.common.convention.result.Result;
import com.moreo.shorlink.admin.common.convention.result.Results;
import com.moreo.shorlink.admin.dto.req.UserRegisterReqDTO;
import com.moreo.shorlink.admin.dto.resp.UserActualRespDTO;
import com.moreo.shorlink.admin.dto.resp.UserRespDTO;
import com.moreo.shorlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    // @RequiredArgsConstructor 会自动为所有 final 字段生成构造方法，Spring 通过构造器完成注入。
    private final UserService userService;

    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
//        UserRespDTO userRespDTO = userService.getUserByUsername(username);
//        if(userRespDTO == null){
////            return Results.failure(UserErrorCodeEnum.USER_NULL.code(), UserErrorCodeEnum.USER_NULL.message());
//            // 枚举值的类型 就是枚举实现的接口的类型
//            return Results.failure(UserErrorCodeEnum.USER_NULL);
//        }else {
//            return Results.success(userRespDTO);
//        }

        return Results.success(userService.getUserByUsername(username));

    }


    @GetMapping("/api/shortlink/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getUserActualByUsername(@PathVariable("username") String username) {
        return Results.success(userService.getUserActualByUsername(username));
    }

    @GetMapping("/api/shortlink/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }

    @PostMapping("/api/shortlink/v1/user/save")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }
}
