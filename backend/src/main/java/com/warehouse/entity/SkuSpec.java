package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sku_spec")
public class SkuSpec extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long skuId;
    private String skuCode;
    private String specKey;
    private String specValue;
}
