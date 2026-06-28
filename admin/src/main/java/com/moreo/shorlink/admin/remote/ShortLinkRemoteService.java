package com.moreo.shorlink.admin.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moreo.shorlink.admin.common.convention.result.Result;
import com.moreo.shorlink.admin.remote.dto.req.*;
import com.moreo.shorlink.admin.remote.dto.resp.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接中台远程调用服务
 */
public interface ShortLinkRemoteService {

    /**
     * 创建短链接
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {});
    }

    /**
     * 分页查询短链接
     */
    default Result<Page<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> map = new HashMap<>();
        map.put("gid",  requestParam.getGid());
        map.put("current",  requestParam.getCurrent());
        map.put("size",  requestParam.getSize());
        map.put("orderTag", requestParam.getOrderTag());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/page", map);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {});
    }


    /**
     * 查询短链接分组内数量
     */
    default Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(List<String> requestParam) {
        Map<String, Object> map = new HashMap<>();
        map.put("gid",  requestParam);
        String result = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/count", map);
        return JSON.parseObject(result, new TypeReference<>() {});
    }


    default void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/update", JSON.toJSONString(requestParam));
    }

    default Result<String> getTitleByUrl(String url) {
        String result = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/url-title?url="+url);
        return JSON.parseObject(result, new TypeReference<>() {});
    }

    default void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/recycle-bin/save", JSON.toJSONString(requestParam));
    }

    /**
     * 分页查询回收站短链接
     */
    default Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(RecycleBinPageReqDTO requestParam) {
        Map<String, Object> map = new HashMap<>();
        map.put("gidList",  requestParam.getGidList());
        map.put("current",  requestParam.getCurrent());
        map.put("size",  requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/recycle-bin/page", map);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {});
    }

    default void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/recycle-bin/recover", JSON.toJSONString(requestParam));
    }

    default void deleteRecycleBin(RecycleBinDeleteReqDTO requestParam) {
        HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/recycle-bin/delete", JSON.toJSONString(requestParam));
    }

    default Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        Map<String, Object> map = new HashMap<>();
        map.put("gid",  requestParam.getGid());
        map.put("fullShortUrl",  requestParam.getFullShortUrl());
        map.put("startDate",  requestParam.getStartDate());
        map.put("endDate",  requestParam.getEndDate());
        map.put("enableStatus",   requestParam.getEnableStatus());
        String result = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/stats", map);
        return JSON.parseObject(result, new TypeReference<>() {});
    }

    /**
     * 分页查询短链接监控日志记录
     */
    default Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkAccessRecordReqDTO requestParam) {
        Map<String, Object> map = new HashMap<>();
        map.put("fullShortUrl",  requestParam.getFullShortUrl());
        map.put("startDate",  requestParam.getStartDate());
        map.put("endDate",  requestParam.getEndDate());
        map.put("enableStatus",   requestParam.getEnableStatus());
        map.put("gid",  requestParam.getGid());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/stats/access-record", map);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {});
    }

    /**
     * 批量创建短链接
     */
    default Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam){
        Map<String, Object> map = new HashMap<>();
        map.put("createType",  requestParam.getCreatedType());
        map.put("originUrls",  requestParam.getOriginUrls());
        map.put("describes",   requestParam.getDescribes());
        map.put("validDateType",   requestParam.getValidDateType());
        map.put("validDate",   requestParam.getValidDate());
        map.put("gid",  requestParam.getGid());
        String resultPageStr = HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/create/batch", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultPageStr, new TypeReference<>() {});
    }

}
