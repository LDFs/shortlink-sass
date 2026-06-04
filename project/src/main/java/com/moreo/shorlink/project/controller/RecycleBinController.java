package com.moreo.shorlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moreo.shorlink.project.common.convention.result.Result;
import com.moreo.shorlink.project.common.convention.result.Results;
import com.moreo.shorlink.project.dto.req.RecycleBinDeleteReqDTO;
import com.moreo.shorlink.project.dto.req.RecycleBinPageReqDTO;
import com.moreo.shorlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.moreo.shorlink.project.dto.req.RecycleBinSaveReqDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkPageRespDTO;
import com.moreo.shorlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站管理控制层
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService  recycleBinService;

    /**
     * 保存至回收站
     */
    @PostMapping("/api/shortlink/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     */
    @GetMapping("/api/shortlink/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageLink(RecycleBinPageReqDTO requestParam) {
        return Results.success(recycleBinService.pagerShortLink(requestParam));
    }

    /**
     * 恢复回收站
     */
    @PostMapping("/api/shortlink/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam){
        recycleBinService.recoverRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 删除回收站
     */
    @PostMapping("/api/shortlink/v1/recycle-bin/delete")
    public Result<Void> deleteRecycleBin(@RequestBody RecycleBinDeleteReqDTO requestParam){
        recycleBinService.deleteRecycleBin(requestParam);
        return Results.success();
    }
}
