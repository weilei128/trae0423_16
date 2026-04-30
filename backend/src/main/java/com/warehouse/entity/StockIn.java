package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stock_in")
public class StockIn extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String inNo;
    private String inType;
    private String refNo;
    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private String warehouseCode;
    private String warehouseName;
    private Integer totalQty;
    private Integer inspectedQty;
    private Integer shelvedQty;
    private Integer status;
}
