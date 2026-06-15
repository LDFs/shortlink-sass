package com.moreo.shorlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moreo.shorlink.project.dao.entity.LinkAccessLogsDO;
import com.moreo.shorlink.project.dao.entity.LinkAccessStatsDO;
import com.moreo.shorlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取用户信息是否为新老访客
     * 判断方法：用户最早访问这个短链接的时间在查询范围内 - 新访客
     * 使用 script -> 为了在 MyBatis 的 @Select 注解中支持复杂的动态 SQL（如 <foreach>）
     * 动态SQL：foreach, 生成类似 IN ('user1', 'user2', 'user3') 的条件，防止 SQL 注入。
     */
    @Select("""
            <script>
            SELECT
                tlal.user,
                CASE
                    WHEN MIN(tlal.create_time) BETWEEN #{startDate} AND #{endDate} THEN '新访客'
                    ELSE '老访客'
                END as uvType
            FROM
                t_link tl INNER JOIN
                t_link_access_logs tlal ON tl.full_short_url = tlal.full_short_url
            WHERE
                tlal.full_short_url = #{fullShortUrl}
                AND tl.gid = #{gid}
                AND tl.del_flag = '0'
                AND tl.enable_status = #{enableStatus}
                AND tlal.user IN
                <foreach
                    item='item' index='index' collection='userAccessLogsList' open='(' separator=',' close=')'> #{item}
                </foreach>
            GROUP BY
                tlal.user;
            </script>
            """)
    List<Map<String, Object>> selectUvTypeByUsers(
            @Param("gid") String gid,
            @Param("fullShortUrl") String fullShortUrl,
            @Param("enableStatus") Integer enableStatus,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("userAccessLogsList") List<String> userAccessLogsList
    );
}
