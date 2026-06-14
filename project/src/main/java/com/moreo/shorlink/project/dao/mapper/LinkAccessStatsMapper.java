package com.moreo.shorlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moreo.shorlink.project.dao.entity.LinkAccessStatsDO;
import com.moreo.shorlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/**
 * 短链接基础访问监控持久层
 */
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    /**
     * 记录基础访问监控数据
     */
    @Insert("INSERT INTO " +
            "t_link_access_stats (full_short_url, date, pv, uv, uip, hour, weekday, create_time, update_time, del_flag) " +
            "VALUES( #{linkAccessStats.fullShortUrl}, #{linkAccessStats.date}, #{linkAccessStats.pv}, #{linkAccessStats.uv}, #{linkAccessStats.uip}, #{linkAccessStats.hour}, #{linkAccessStats.weekday}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE pv = pv +  #{linkAccessStats.pv}, uv = uv + #{linkAccessStats.uv}, uip = uip + #{linkAccessStats.uip};")
    void shortLinkStats(@Param("linkAccessStats") LinkAccessStatsDO linkAccessStatsDO);

    /**
     * 根据短链接，获取在指定时间段内 每一天的基础监控数据：pv uv uip 的统计数量
     */
    @Select("""
            SELECT
                tlas.date,
                SUM(tlas.pv) AS pv,
                SUM(tlas.uv) as uv,
                SUM(tlas.uip) as uip
            FROM
                t_link tl INNER JOIN
                t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url
            WHERE
                tlas.full_short_url = #{param.fullShortUrl}
                AND tl.gid = #{param.gid}
                AND tl.del_flag = '0'
                AND tl.enable_status = #{param.enableStatus}
                AND tlas.date BETWEEN #{param.startDate} and #{param.endDate}
            GROUP BY
                tlas.full_short_url, tl.gid, tlas.date;
            """)
    List<LinkAccessStatsDO> listStatsByShortLink(@Param("param") ShortLinkStatsReqDTO param);

    /**
     * 根据短链接 获取指定日期内 小时基础监控数据
     */
    @Select("""
            SELECT
                tlas.hour,
                SUM(tlas.pv) as pv
            FROM
                t_link tl INNER JOIN
                t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url
            WHERE
                tlas.full_short_url = #{param.fullShortUrl}
                AND tl.gid = #{param.gid}
                AND tl.del_flag = '0'
                AND tl.enable_status = #{param.enableStatus}
                AND tlas.date BETWEEN #{param.startDate} and #{param.endDate}
            GROUP BY
                tlas.full_short_url, tl.gid, tlas.hour;
            """)
    List<LinkAccessStatsDO> listHourStatsByShortLink(@Param("param")  ShortLinkStatsReqDTO param);

    /**
     * 根据短链接 获取在指定日期内的 每个weekday的监控数据
     */
    @Select("""
            SELECT
                tlas.weekday,
                SUM(tlas.pv) as pv
            FROM
                t_link tl INNER JOIN
                t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url
            WHERE
                tlas.full_short_url = #{param.fullShortUrl}
                AND tl.gid = #{param.gid}
                AND tl.del_flag = '0'
                AND tl.enable_status = #{param.enableStatus}
                AND tlas.date BETWEEN #{param.startDate} and #{param.endDate}
            GROUP BY
                tlas.full_short_url, tl.gid, tlas.weekday;
            """)
    List<LinkAccessStatsDO> listWeekdayStatsByShortLink(@Param("param")  ShortLinkStatsReqDTO param);

    /**
     * 根据短链接 获取在指定日期内的 访客类型数据
     */
    @Select("""
            SELECT
                SUM(old_user) AS oldUserCnt,
                SUM(new_user) AS newUserCnt
            FROM (
                SELECT
                    CASE WHEN COUNT(DISTINCT DATE(tlal.create_time)) > 1 THEN 1 ELSE 0 END AS old_user,
                    CASE WHEN COUNT(DISTINCT DATE(tlal.create_time)) = 1 AND MAX(tlal.create_time) >= #{param.startDate} AND MAX(tlal.create_time) <= #{param.endDate} THEN 1 ELSE 0 END as new_user
                FROM
                    t_link tl INNER JOIN
                    t_link_access_logs tlal ON tl.full_short_url = tlal.full_short_url
                WHERE
                    tlal.full_short_url = #{param.fullShortUrl}
                    AND tl.gid = #{param.gid}
                    AND tl.del_flag = '0'
                    AND tl.enable_status = #{param.enableStatus}
                    AND tlal.create_time BETWEEN #{param.startDate} and #{param.endDate}
                GROUP BY
                    tlal.user
            ) as user_counts;
            """)
    HashMap<String, Object>  findUvTypeCntByShortLink(@Param("param")  ShortLinkStatsReqDTO param);
}
