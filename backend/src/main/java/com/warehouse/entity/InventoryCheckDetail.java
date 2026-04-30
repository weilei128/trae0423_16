package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inventory_check_detail")
public class InventoryCheckDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long inventoryCheckId;
    private String checkNo;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Long locationId;
    private String locationCode;
    private Integer systemStock;
    private Integer actualStock;
    private Integer diffQty;
    private String batchNo;
    private Integer isAbnormal;
    private String handleRemark;
    private Integer status;
}
