package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("picking_task")
public class PickingTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String taskNo;
    private Long stockOutId;
    private String outNo;
    private Long pickerId;
    private String pickerName;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Long locationId;
    private String locationCode;
    private Integer planQty;
    private Integer actualQty;
    private Integer status;
}
