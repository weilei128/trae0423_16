package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inventory")
public class Inventory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long skuId;
    private String skuCode;
    private String skuName;
    private Long locationId;
    private String locationCode;
    private Integer stock;
    private Integer lockedStock;
    private Integer availableStock;
    private String batchNo;
    private LocalDate productionDate;
    private LocalDate expiryDate;
}
