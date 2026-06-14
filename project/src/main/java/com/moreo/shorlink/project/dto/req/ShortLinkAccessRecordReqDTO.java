package com.moreo.shorlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moreo.shorlink.project.dao.entity.LinkAccessLogsDO;
import lombok.Data;

/**
 * 访问日志监控实体
 */
@Data
public class ShortLinkAccessRecordReqDTO extends Page<LinkAccessLogsDO> {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 启用标识 0：启用 1：未启用
     */
    private Integer enableStatus;
}