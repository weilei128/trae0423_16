package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("supplier")
public class Supplier extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String supplierCode;
    private String supplierName;
    private String contactPerson;
    private String contactPhone;
    private String address;
    private Integer status;
}
