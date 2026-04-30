package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inventory_check")
public class InventoryCheck extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String checkNo;
    private String warehouseCode;
    private String warehouseName;
    private String checkType;
    private LocalDate checkDate;
    private Long checkerId;
    private String checkerName;
    private Integer totalSkuCount;
    private Integer abnormalCount;
    private Integer status;
}
