package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.entity.StockOutDetail;
import com.warehouse.mapper.StockOutDetailMapper;
import com.warehouse.service.StockOutDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockOutDetailServiceImpl extends ServiceImpl<StockOutDetailMapper, StockOutDetail> implements StockOutDetailService {

    @Override
    public List<StockOutDetail> getByStockOutId(Long stockOutId) {
        LambdaQueryWrapper<StockOutDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockOutDetail::getStockOutId, stockOutId);
        wrapper.orderByAsc(StockOutDetail::getId);
        return this.list(wrapper);
    }
}
