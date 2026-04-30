package com.warehouse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Inventory;

public interface InventoryService extends IService<Inventory> {

    Page<Inventory> queryPage(PageQuery pageQuery, Inventory inventory);

    void deductStock(Long skuId, Long locationId, String batchNo, Integer quantity);

    void addStock(Long skuId, Long locationId, String batchNo, Integer quantity);

    void lockStock(Long skuId, Long locationId, String batchNo, Integer quantity);

    void unlockStock(Long skuId, Long locationId, String batchNo, Integer quantity);
}
