package com.moreo.shorlink.admin.remote.dto.req;

import lombok.Data;

/**
 * 保存至回收站请求对象
 */
@Data
public class RecycleBinSaveReqDTO {

    /**
     * 分组标识
     */
    String gid;

    /**
     * 完整短链接
     */
    String fullShortUrl;
}
