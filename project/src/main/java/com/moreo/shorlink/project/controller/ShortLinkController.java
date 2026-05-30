package com.moreo.shorlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moreo.shorlink.project.common.convention.result.Result;
import com.moreo.shorlink.project.common.convention.result.Results;
import com.moreo.shorlink.project.dto.req.ShortLinkCreateReqDTO;
import com.moreo.shorlink.project.dto.req.ShortLinkPageReqDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkPageRespDTO;
import com.moreo.shorlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
