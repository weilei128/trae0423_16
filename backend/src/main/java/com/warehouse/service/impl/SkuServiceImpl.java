package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Sku;
import com.warehouse.mapper.SkuMapper;
import com.warehouse.service.SkuService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {

    @Override
    public Page<Sku> queryPage(PageQuery pageQuery, Sku sku) {
        Page<Sku> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(sku.getSkuCode())) {
            wrapper.like(Sku::getSkuCode, sku.getSkuCode());
        }
        if (StringUtils.hasText(sku.getSkuName())) {
            wrapper.like(Sku::getSkuName, sku.getSkuName());
        }
        if (StringUtils.hasText(sku.getCategory())) {
            wrapper.eq(Sku::getCategory, sku.getCategory());
        }
        if (sku.getSupplierId() != null) {
            wrapper.eq(Sku::getSupplierId, sku.getSupplierId());
        }
        if (sku.getStatus() != null) {
            wrapper.eq(Sku::getStatus, sku.getStatus());
        }
        
        wrapper.orderByDesc(Sku::getCreateTime);
        return this.page(page, wrapper);
    }
}
