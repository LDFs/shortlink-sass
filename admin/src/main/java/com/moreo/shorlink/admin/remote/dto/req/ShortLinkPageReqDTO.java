package com.moreo.shorlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moreo.shorlink.admin.dao.entity.ShortLinkDO;
import lombok.Data;

@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {

    private String gid;
}
