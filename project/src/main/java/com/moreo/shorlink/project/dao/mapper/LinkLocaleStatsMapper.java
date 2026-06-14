package com.moreo.shorlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moreo.shorlink.project.dao.entity.LinkLocaleStatsDO;
import com.moreo.shorlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 地区访问统计持久层
 */
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {

    /**
     * 记录地区访问监控数据
     */
    @Insert("INSERT INTO " +
            "t_link_locale_stats (full_short_url, date, cnt, country, province, city, adcode, create_time, update_time, del_flag) " +
            "VALUES( #{linkLocaleStats.fullShortUrl}, #{linkLocaleStats.date}, #{linkLocaleStats.cnt}, #{linkLocaleStats.country}, #{linkLocaleStats.province}, #{linkLocaleStats.city}, #{linkLocaleStats.adcode}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkLocaleStats.cnt};")
    void shortLinkLocaleState(@Param("linkLocaleStats") LinkLocaleStatsDO linkLocaleStatsDO);

    /**
     * 根据短链接 获取具体时间段内 各个地区的访问数
     */
    @Select("""
            SELECT
                tlls.province, SUM(tlls.cnt) as cnt
            FROM
                t_link tl INNER JOIN
                t_link_locale_stats tlls ON tl.full_short_url = tlls.full_short_url
            WHERE
                tlls.full_short_url = #{param.fullShortUrl}
                AND tl.gid = #{param.gid}
                AND tl.del_flag = '0'
                AND tl.enable_status = #{param.enableStatus}
                AND tlls.date BETWEEN #{param.startDate} AND #{param.endDate}
            GROUP BY
                tlls.full_short_url, tl.gid, tlls.province;
            """)
    List<LinkLocaleStatsDO> listLocaleByShortLink(@Param("param")ShortLinkStatsReqDTO param);
}
