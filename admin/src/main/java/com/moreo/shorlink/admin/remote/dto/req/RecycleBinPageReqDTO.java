package com.moreo.shorlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 短链接回收站分页查询请求对象
 */
@Data
public class RecycleBinPageReqDTO extends Page {

    /**
     * 当前用户所有的分组标识列表
     */
    private List<String> gidList;
}
