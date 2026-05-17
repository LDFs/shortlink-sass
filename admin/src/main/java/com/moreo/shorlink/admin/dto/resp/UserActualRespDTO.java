package com.moreo.shorlink.admin.dto.resp;

import lombok.Data;

@Data
public class UserActualRespDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     * 脱敏手机信息 @JsonSerialize
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}
