package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("return_order_detail")
public class ReturnOrderDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long returnOrderId;
    private String returnNo;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Integer returnQty;
    private Integer inspectedQty;
    private Integer qualifiedQty;
    private Integer unqualifiedQty;
    private String qcResult;
    private String qcRemark;
    private Integer status;
}
