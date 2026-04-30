package com.warehouse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.StockOut;
import com.warehouse.entity.StockOutDetail;

import java.util.List;

public interface StockOutService extends IService<StockOut> {

    Page<StockOut> queryPage(PageQuery pageQuery, StockOut stockOut);

    StockOut getWithDetails(Long id);

    boolean createStockOut(StockOut stockOut, List<StockOutDetail> details);

    boolean pick(Long id, List<StockOutDetail> details);

    boolean check(Long id, List<StockOutDetail> details);

    boolean cancel(Long id);
}
