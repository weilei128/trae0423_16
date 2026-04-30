package com.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.entity.StockInDetail;

import java.util.List;

public interface StockInDetailService extends IService<StockInDetail> {

    List<StockInDetail> getByStockInId(Long stockInId);
}
