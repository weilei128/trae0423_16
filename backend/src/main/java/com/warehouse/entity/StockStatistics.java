package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stock_statistics")
public class StockStatistics extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private LocalDate statDate;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Integer beginningStock;
    private Integer inQty;
    private Integer outQty;
    private Integer endingStock;
}
