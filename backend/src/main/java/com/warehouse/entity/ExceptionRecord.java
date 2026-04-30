package com.warehouse.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exception_record")
public class ExceptionRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String exceptionNo;
    private String sourceType;
    private String sourceNo;
    private String exceptionType;
    private String description;
    private Long handlerId;
    private String handlerName;
    private String handleResult;
    private Integer status;
}
