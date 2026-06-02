package com.moreo.shorlink.project.service;

import com.moreo.shorlink.project.dto.req.RecycleBinSaveReqDTO;

/**
 * 回收站管理接口层
 */
public interface RecycleBinService {

    /**
     * 保存短链接至回收站
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);
}
