package com.warehouse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.StockIn;
import com.warehouse.entity.StockInDetail;

import java.util.List;

public interface StockInService extends IService<StockIn> {

    Page<StockIn> queryPage(PageQuery pageQuery, StockIn stockIn);

    StockIn getWithDetails(Long id);

    boolean createStockIn(StockIn stockIn, List<StockInDetail> details);

    boolean inspect(Long id, List<StockInDetail> details);

    boolean shelve(Long id, List<StockInDetail> details);

    boolean cancel(Long id);
}
