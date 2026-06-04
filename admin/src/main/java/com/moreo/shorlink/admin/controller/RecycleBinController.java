package com.moreo.shorlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moreo.shorlink.admin.common.convention.result.Result;
import com.moreo.shorlink.admin.common.convention.result.Results;
import com.moreo.shorlink.admin.remote.ShortLinkRemoteService;
import com.moreo.shorlink.admin.remote.dto.req.RecycleBinPageReqDTO;
import com.moreo.shorlink.admin.remote.dto.req.RecycleBinRecoverReqDTO;
import com.moreo.shorlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.moreo.shorlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.moreo.shorlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {};
    private final RecycleBinService recycleBinService;

    @PostMapping("/api/shortlink/admin/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        shortLinkRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/shortlink/admin/v1/recycle-bin/page")
    public Result<Page<ShortLinkPageRespDTO>> pageShortLink(RecycleBinPageReqDTO requestParam) {
        return recycleBinService.pageRecycleBin(requestParam);
    }

    @PostMapping("/api/shortlink/admin/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        shortLinkRemoteService.recoverRecycleBin(requestParam);
        return Results.success();
    }
}
