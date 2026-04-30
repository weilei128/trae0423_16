package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("waybill_trace")
public class WaybillTrace extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long waybillId;
    private String waybillNo;
    private Integer status;
    private String location;
    private String description;
    private String operatorName;
    private LocalDateTime operateTime;
}
