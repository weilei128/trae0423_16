package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("return_order")
public class ReturnOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String returnNo;
    private String originalOutNo;
    private Long stockOutId;
    private String customerName;
    private String customerPhone;
    private String returnReason;
    private Integer totalQty;
    private Integer inspectedQty;
    private Integer status;
}
