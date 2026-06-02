package com.moreo.shorlink.project.controller;

import com.moreo.shorlink.project.common.convention.result.Result;
import com.moreo.shorlink.project.common.convention.result.Results;
import com.moreo.shorlink.project.service.OriginalUrlTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * URL 标题控制层
 */
@RestController
@RequiredArgsConstructor
public class OriginalUrlTitleController {

    private final OriginalUrlTitleService urlTitleService;

    /**
     * 根据 URL 获取对应网站的标题
     */
    @GetMapping("/api/shortlink/v1/url-title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) {
        return Results.success(urlTitleService.getTitleByUrl(url));
    }
}
