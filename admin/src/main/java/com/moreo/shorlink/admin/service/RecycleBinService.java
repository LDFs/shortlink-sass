package com.moreo.shorlink.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moreo.shorlink.admin.common.convention.result.Result;
import com.moreo.shorlink.admin.remote.dto.req.RecycleBinPageReqDTO;
import com.moreo.shorlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

public interface RecycleBinService {

    /**
     * 分页查询短链接回收站
     */
    Result<Page<ShortLinkPageRespDTO>> pageRecycleBin(RecycleBinPageReqDTO requestParam);
}
