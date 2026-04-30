package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("waybill")
public class Waybill extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String waybillNo;
    private Long stockOutId;
    private String outNo;
    private Long carrierId;
    private String carrierCode;
    private String carrierName;
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private BigDecimal totalWeight;
    private BigDecimal totalVolume;
    private BigDecimal freightAmount;
    private Integer status;
    private String currentLocation;
    private LocalDateTime estimatedArrivalTime;
    private LocalDateTime actualArrivalTime;
}
