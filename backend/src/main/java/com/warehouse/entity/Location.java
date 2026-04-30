package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("location")
public class Location extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String locationCode;
    private String locationName;
    private String warehouseCode;
    private String warehouseName;
    private String area;
    private String row;
    private String column;
    private String layer;
    private Integer maxCapacity;
    private Integer usedCapacity;
    private Integer status;
}
