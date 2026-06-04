package com.moreo.shorlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moreo.shorlink.project.dao.entity.ShortLinkDO;
import lombok.Data;

import java.util.List;

/**
 * 短链接回收站分页查询请求对象
 */
@Data
public class RecycleBinPageReqDTO extends Page<ShortLinkDO> {

    /**
     * 当前用户所有的分组标识列表
     */
    private List<String> gidList;
}
