package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("alert_threshold")
public class AlertThreshold extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long skuId;
    private String skuCode;
    private Integer minStock;
    private Integer maxStock;
}
