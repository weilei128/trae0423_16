package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stock_in_detail")
public class StockInDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long stockInId;
    private String inNo;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Integer planQty;
    private Integer inspectedQty;
    private Integer qualifiedQty;
    private Integer unqualifiedQty;
    private Integer shelvedQty;
    private Long locationId;
    private String locationCode;
    private String batchNo;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private Integer status;
}
