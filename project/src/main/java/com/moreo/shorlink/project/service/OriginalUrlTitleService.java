package com.moreo.shorlink.project.service;

/**
 * URL 标题接口层
 */
public interface OriginalUrlTitleService {

    /**
     * 根据 URL 获取网站标题
     * @param url 目标网址
     * @return 标题
     */
    String getTitleByUrl(String url);
}
