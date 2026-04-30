package com.warehouse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Sku;

public interface SkuService extends IService<Sku> {

    Page<Sku> queryPage(PageQuery pageQuery, Sku sku);
}
