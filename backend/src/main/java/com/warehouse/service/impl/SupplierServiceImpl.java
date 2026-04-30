package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Supplier;
import com.warehouse.mapper.SupplierMapper;
import com.warehouse.service.SupplierService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    @Override
    public Page<Supplier> queryPage(PageQuery pageQuery, Supplier supplier) {
        Page<Supplier> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(supplier.getSupplierCode())) {
            wrapper.like(Supplier::getSupplierCode, supplier.getSupplierCode());
        }
        if (StringUtils.hasText(supplier.getSupplierName())) {
            wrapper.like(Supplier::getSupplierName, supplier.getSupplierName());
        }
        if (StringUtils.hasText(supplier.getContactPerson())) {
            wrapper.like(Supplier::getContactPerson, supplier.getContactPerson());
        }
        if (supplier.getStatus() != null) {
            wrapper.eq(Supplier::getStatus, supplier.getStatus());
        }
        
        wrapper.orderByDesc(Supplier::getCreateTime);
        return this.page(page, wrapper);
    }
}
