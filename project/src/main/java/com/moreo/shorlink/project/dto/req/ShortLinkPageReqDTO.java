package com.moreo.shorlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moreo.shorlink.project.dao.entity.ShortLinkDO;
import lombok.Data;

@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {

    private String gid;
}
