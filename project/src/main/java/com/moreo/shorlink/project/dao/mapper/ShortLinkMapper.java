package com.moreo.shorlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moreo.shorlink.project.dao.entity.ShortLinkDO;
import com.moreo.shorlink.project.dto.req.ShortLinkPageReqDTO;

/**
 * 短链接持久层
 */
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {

    IPage<ShortLinkDO> pageLink(ShortLinkPageReqDTO pageParam);
}
