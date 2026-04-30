package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sku")
public class Sku extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String skuCode;
    private String skuName;
    private String category;
    private String unit;
    private String specs;
    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private BigDecimal weight;
    private BigDecimal volume;
    private Integer status;
}
