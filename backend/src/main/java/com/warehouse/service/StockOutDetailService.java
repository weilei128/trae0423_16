package com.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.entity.StockOutDetail;

import java.util.List;

public interface StockOutDetailService extends IService<StockOutDetail> {

    List<StockOutDetail> getByStockOutId(Long stockOutId);
}
