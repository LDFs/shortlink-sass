package com.moreo.shorlink.admin.controller;

import com.moreo.shorlink.admin.common.convention.result.Result;
import com.moreo.shorlink.admin.remote.ShortLinkRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OriginalUrlTitleController {

    ShortLinkRemoteService  shortLinkRemoteService = new ShortLinkRemoteService() {};

    /**
     * 根据URL获取对应网站的标题
     */
    @GetMapping("/api/shortlink/admin/v1/url-title")
    public Result<String> getUrlTitle(String url) {
        return shortLinkRemoteService.getTitleByUrl(url);
    }
}
