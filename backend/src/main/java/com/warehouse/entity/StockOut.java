package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stock_out")
public class StockOut extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String outNo;
    private String outType;
    private String refNo;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String warehouseCode;
    private String warehouseName;
    private Integer totalQty;
    private Integer pickedQty;
    private Integer checkedQty;
    private Integer status;
}
