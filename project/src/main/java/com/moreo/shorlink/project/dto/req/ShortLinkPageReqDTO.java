package com.moreo.shorlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moreo.shorlink.project.dao.entity.ShortLinkDO;
import lombok.Data;

/**
 * 分页查询短链接请求参数
 */
@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {

    private String gid;

    /**
     * 排序标识
     */
    private String orderTag;
}
