package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("carrier")
public class Carrier extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String carrierCode;
    private String carrierName;
    private String contactPerson;
    private String contactPhone;
    private String apiUrl;
    private String apiKey;
    private Integer status;
}
