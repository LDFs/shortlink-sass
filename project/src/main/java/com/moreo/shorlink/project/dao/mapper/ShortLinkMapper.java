package com.moreo.shorlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moreo.shorlink.project.dao.entity.ShortLinkDO;
import com.moreo.shorlink.project.dto.req.ShortLinkPageReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * 短链接持久层
 */
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {

    // 直接拿的仓库代码，后期才使用
    IPage<ShortLinkDO> pageLink(ShortLinkPageReqDTO pageParam);

    /**
     * 短链接访问统计自增
     */
    @Insert("""
            UPDATE t_link
                    SET
                        total_pv = total_pv + #{totalPv},
                        total_uv = total_uv + #{totalUv},
                        total_uip = total_uip + #{totalUip}
                    WHERE
                        gid = #{gid}
                        AND full_short_url = #{fullShortUrl}
            """)
    void incrementStats(@Param("gid") String gid,
                        @Param("fullShortUrl") String fullShortUrl,
                        @Param("totalPv") Integer totalPv,
                        @Param("totalUv") Integer totalUv,
                        @Param("totalUip") Integer totalUip);

}
