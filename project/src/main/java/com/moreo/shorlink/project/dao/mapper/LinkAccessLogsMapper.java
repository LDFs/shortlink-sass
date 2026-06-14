package com.moreo.shorlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moreo.shorlink.project.dao.entity.LinkAccessLogsDO;
import com.moreo.shorlink.project.dao.entity.LinkAccessStatsDO;
import com.moreo.shorlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/**
 * 访问日志监控持久层
 */
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {

    /**
     * 根据短链接获取指定日期内的 pv uv uip 数量
     */
    @Select("""
            SELECT
                COUNT(tlal.user) as pv,
                COUNT(DISTINCT tlal.user) as uv,
                COUNT(DISTINCT tlal.ip) as uip
            FROM
                t_link tl INNER JOIN
                t_link_access_logs tlal ON tl.full_short_url = tlal.full_short_url
            WHERE
                tlal.full_short_url = #{param.fullShortUrl}
                AND tl.gid = #{param.gid}
                AND tl.del_flag = '0'
                AND tl.enable_status = #{param.enableStatus}
                AND tlal.create_time BETWEEN #{param.startDate} AND #{param.endDate}
            GROUP BY
                tlal.full_short_url, tl.gid;
            """)
    LinkAccessStatsDO findPvUvUidStatsByShortLink(@Param("param")ShortLinkStatsReqDTO param);

    @Select("""
            SELECT
                tlal.ip,
                COUNT(tlal.ip) as count
            FROM
                t_link tl INNER JOIN
                t_link_access_logs tlal ON tl.full_short_url = tlal.full_short_url
            WHERE
                tlal.full_short_url = #{param.fullShortUrl}
                AND tl.gid = #{param.gid}
                AND tl.del_flag = '0'
                AND tl.enable_status = #{param.enableStatus}
                AND tlal.create_time BETWEEN #{param.startDate} AND #{param.endDate}
            GROUP BY
                tlal.full_short_url, tl.gid, tlal.ip
            ORDER BY
                count DESC
            LIMIT 5;
            """)
    List<HashMap<String, Object>> listTopIpByShortLink(@Param("param") ShortLinkStatsReqDTO param);
}
