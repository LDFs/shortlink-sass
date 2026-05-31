package com.moreo.shorlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moreo.shorlink.project.common.convention.result.Result;
import com.moreo.shorlink.project.common.convention.result.Results;
import com.moreo.shorlink.project.dto.req.ShortLinkCreateReqDTO;
import com.moreo.shorlink.project.dto.req.ShortLinkPageReqDTO;
import com.moreo.shorlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkPageRespDTO;
import com.moreo.shorlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    @PostMapping("/api/shortlink/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    @GetMapping("/api/shortlink/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageLink(ShortLinkPageReqDTO requestParam) {
        return Results.success(shortLinkService.pagerShortLink(requestParam));
    }

    @GetMapping("/api/shortlink/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupCount(@RequestParam("gid") List<String> requestParam) {
        return Results.success(shortLinkService.listGroupShortLinkCount(requestParam));
    }

    @PostMapping("/api/shortlink/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }
}
