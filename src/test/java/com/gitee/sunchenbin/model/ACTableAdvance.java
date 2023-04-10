package com.gitee.sunchenbin.model;

import cn.bootx.mybatis.table.modify.annotation.*;
import cn.bootx.mybatis.table.modify.impl.mysql.annotation.MySqlIndex;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 全部采用actable自有的注解
 */
@DbTable(value = "t_actable_advance", comment = "actable进阶配置")
public class ACTableAdvance {

    @IsKey
    private Long id;

    @DbColumn
    @MySqlIndex(value = "idx_name_shop", columns = { "name", "shop" })
    private String name;

    @DbColumn(name = "create_time",  comment = "创建时间")
    private Date createTime;

    @DbColumn
    private Boolean isTrue;

    @DbColumn
    private Integer age;

    @DbColumn(length = 10, decimalLength = 4)
    private BigDecimal price;

    @DbColumn
    private String identitycard;

    @DbColumn
    private String shop;

}
