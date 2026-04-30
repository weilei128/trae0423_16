package com.warehouse.dto;

import com.warehouse.entity.StockIn;
import com.warehouse.entity.StockInDetail;
import lombok.Data;

import java.util.List;

@Data
public class StockInCreateRequest {

    private StockIn stockIn;
    private List<StockInDetail> details;
}
