package com.moreo.shorlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.moreo.shorlink.project.dao.entity.ShortLinkDO;
import com.moreo.shorlink.project.dto.req.ShortLinkCreateReqDTO;
import com.moreo.shorlink.project.dto.req.ShortLinkPageReqDTO;
import com.moreo.shorlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建短链接
     * @param shortLinkCreateReqDTO 请求参数
     * @return 返回值
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO shortLinkCreateReqDTO);

    /**
     * 分页查询短链接
     */
    IPage<ShortLinkPageRespDTO> pagerShortLink(ShortLinkPageReqDTO shortLinkPageReqDTO);

    /**
     * 查询短链接分组内数量
     *
     * @param requestParam 查询短链接分组内数量请求参数
     * @return 查询短链接分组内数量响应
     */
    List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam);

    /**
     * 更新短链接信息
     * @param requestParam 请求参数
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);
}
