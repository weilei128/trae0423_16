package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stock_out_detail")
public class StockOutDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long stockOutId;
    private String outNo;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Integer planQty;
    private Integer pickedQty;
    private Integer checkedQty;
    private Long locationId;
    private String locationCode;
    private String batchNo;
    private Integer status;
}
