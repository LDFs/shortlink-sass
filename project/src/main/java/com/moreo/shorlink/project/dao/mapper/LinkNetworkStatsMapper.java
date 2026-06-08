package com.moreo.shorlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moreo.shorlink.project.dao.entity.LinkNetworkStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * 访问网络监控持久层
 */
public interface LinkNetworkStatsMapper extends BaseMapper<LinkNetworkStatsDO> {

    @Insert("INSERT INTO "+
            "t_link_network_stats (full_short_url, date, cnt, network, create_time, update_time, del_flag)" +
            "VALUES(#{linkNetworkStats.fullShortUrl}, #{linkNetworkStats.date}, #{linkNetworkStats.cnt}, #{linkNetworkStats.network}, NOW(), NOW(), 0)" +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{linkNetworkStats.cnt};" )
    void shorLinkNetworkState(@Param("linkNetworkStats") LinkNetworkStatsDO linkNetworkStats);
}
