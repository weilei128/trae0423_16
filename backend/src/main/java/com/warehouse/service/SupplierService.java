package com.warehouse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Supplier;

public interface SupplierService extends IService<Supplier> {

    Page<Supplier> queryPage(PageQuery pageQuery, Supplier supplier);
}
