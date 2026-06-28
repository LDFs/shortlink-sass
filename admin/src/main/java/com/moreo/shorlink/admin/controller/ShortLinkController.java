package com.moreo.shorlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moreo.shorlink.admin.common.convention.result.Result;
import com.moreo.shorlink.admin.common.convention.result.Results;
import com.moreo.shorlink.admin.remote.ShortLinkRemoteService;
import com.moreo.shorlink.admin.remote.dto.req.ShortLinkBatchCreateReqDTO;
import com.moreo.shorlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.moreo.shorlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.moreo.shorlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.moreo.shorlink.admin.remote.dto.resp.ShortLinkBaseInfoRespDTO;
import com.moreo.shorlink.admin.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import com.moreo.shorlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.moreo.shorlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.moreo.shorlink.admin.util.EasyExcelWebUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShortLinkController {

    ShortLinkRemoteService shortLinkremoteService = new ShortLinkRemoteService() {
    };

    @PostMapping("/api/shortlink/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkremoteService.createShortLink(requestParam);
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/shortlink/admin/v1/page")
    public Result<Page<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return shortLinkremoteService.pageShortLink(requestParam);
    }

    @PostMapping("/api/shortlink/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody  ShortLinkUpdateReqDTO requestParam) {
        shortLinkremoteService.updateShortLink(requestParam);
        return Results.success();
    }

    @PostMapping("/api/shortlink/v1/create/batch")
    public void createShortLinkBatch(@RequestBody ShortLinkBatchCreateReqDTO requestParam, HttpServletResponse response) {
        Result<ShortLinkBatchCreateRespDTO> shortLinkBatchCreateRespDTOResult = shortLinkremoteService.batchCreateShortLink(requestParam);
        if(shortLinkBatchCreateRespDTOResult.isSuccess()){
            List<ShortLinkBaseInfoRespDTO> baseLinkInfos = shortLinkBatchCreateRespDTOResult.getData().getBaseLinkInfos();
            EasyExcelWebUtil.write(response, "批量创建短链接-Saas短链接系统", ShortLinkBaseInfoRespDTO.class, baseLinkInfos);
        }
    }
}
