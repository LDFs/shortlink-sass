package com.moreo.shorlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moreo.shorlink.project.dto.req.RecycleBinDeleteReqDTO;
import com.moreo.shorlink.project.dto.req.RecycleBinPageReqDTO;
import com.moreo.shorlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.moreo.shorlink.project.dto.req.RecycleBinSaveReqDTO;
import com.moreo.shorlink.project.dto.resp.ShortLinkPageRespDTO;

/**
 * 回收站管理接口层
 */
public interface RecycleBinService {

    /**
     * 保存短链接至回收站
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询回收站短链接
     */
    IPage<ShortLinkPageRespDTO> pagerShortLink(RecycleBinPageReqDTO requestParam);

    /**
     * 恢复回收站短链接
     */
    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    /**
     * 删除回收站短链接
     */
    void deleteRecycleBin(RecycleBinDeleteReqDTO requestParam);
}
