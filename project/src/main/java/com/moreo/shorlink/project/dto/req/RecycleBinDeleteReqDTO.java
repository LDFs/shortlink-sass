package com.moreo.shorlink.project.dto.req;

import lombok.Data;

/**
 * 删除回收站中的短链接的请求对象
 */
@Data
public class RecycleBinDeleteReqDTO {

    /**
     * 分组标识
     */
    String gid;

    /**
     * 完整短链接
     */
    String fullShortUrl;
}
