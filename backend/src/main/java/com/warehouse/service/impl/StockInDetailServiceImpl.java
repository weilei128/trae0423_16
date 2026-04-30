package com.warehouse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.entity.StockInDetail;
import com.warehouse.mapper.StockInDetailMapper;
import com.warehouse.service.StockInDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockInDetailServiceImpl extends ServiceImpl<StockInDetailMapper, StockInDetail> implements StockInDetailService {

    @Override
    public List<StockInDetail> getByStockInId(Long stockInId) {
        return this.lambdaQuery()
                .eq(StockInDetail::getStockInId, stockInId)
                .list();
    }
}
