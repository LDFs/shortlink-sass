package com.moreo.shorlink.admin.remote.dto.req;

import lombok.Data;

/**
 * 短链接回收站恢复请求
 */
@Data
public class RecycleBinRecoverReqDTO {

    /**
     * 分组标识
     */
    String gid;

    /**
     * 完整短链接
     */
    String fullShortUrl;
}
