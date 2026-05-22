package com.moreo.shorlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moreo.shorlink.admin.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接分组实体
 */
@Data
@TableName("t_group")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDO extends BaseDO {

    private Long id;

    private String gid;

    private String name;

    private String username;

}
