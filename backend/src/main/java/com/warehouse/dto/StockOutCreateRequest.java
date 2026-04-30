package com.warehouse.dto;

import com.warehouse.entity.StockOut;
import com.warehouse.entity.StockOutDetail;
import lombok.Data;

import java.util.List;

@Data
public class StockOutCreateRequest {

    private StockOut stockOut;
    private List<StockOutDetail> details;
}
