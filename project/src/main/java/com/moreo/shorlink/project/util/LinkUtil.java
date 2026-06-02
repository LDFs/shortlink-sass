package com.moreo.shorlink.project.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

import static com.moreo.shorlink.project.common.constant.ShortLinkConstant.DEFAULT_CACHE_VALID_TIME;

public class LinkUtil {

    /**
     * 获取短链接缓存有效期时间
     *
     * @param validDate 有效期时间。如果validDate有值，就取 从现在到这个日期 的毫秒数；否则返回一个默认的有效期长度的毫秒数
     * @return 有限期时间戳
     */
    public static long getLinkCacheValidTime(Date validDate) {
        return Optional.ofNullable(validDate)
                .map(each -> DateUtil.between(new Date(), each, DateUnit.MS))
                .orElse(DEFAULT_CACHE_VALID_TIME);
    }
}
