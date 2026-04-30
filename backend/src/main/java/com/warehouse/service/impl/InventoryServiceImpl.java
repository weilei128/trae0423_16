package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.common.BusinessException;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Inventory;
import com.warehouse.mapper.InventoryMapper;
import com.warehouse.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    @Override
    public Page<Inventory> queryPage(PageQuery pageQuery, Inventory inventory) {
        Page<Inventory> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        
        if (inventory.getSkuId() != null) {
            wrapper.eq(Inventory::getSkuId, inventory.getSkuId());
        }
        if (StringUtils.hasText(inventory.getSkuCode())) {
            wrapper.like(Inventory::getSkuCode, inventory.getSkuCode());
        }
        if (StringUtils.hasText(inventory.getSkuName())) {
            wrapper.like(Inventory::getSkuName, inventory.getSkuName());
        }
        if (inventory.getLocationId() != null) {
            wrapper.eq(Inventory::getLocationId, inventory.getLocationId());
        }
        if (StringUtils.hasText(inventory.getLocationCode())) {
            wrapper.eq(Inventory::getLocationCode, inventory.getLocationCode());
        }
        
        wrapper.orderByDesc(Inventory::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long skuId, Long locationId, String batchNo, Integer quantity) {
        log.info("开始扣减库存: skuId={}, locationId={}, batchNo={}, quantity={}", skuId, locationId, batchNo, quantity);
        
        if (quantity <= 0) {
            throw new BusinessException("扣减数量必须大于0");
        }
        
        Inventory inventory = baseMapper.selectBySkuAndLocationWithLock(skuId, locationId, batchNo);
        
        if (inventory == null) {
            throw new BusinessException("库存记录不存在");
        }
        
        if (inventory.getAvailableStock() < quantity) {
            throw new BusinessException("库存不足，可用库存: " + inventory.getAvailableStock() + ", 需要: " + quantity);
        }
        
        inventory.setStock(inventory.getStock() - quantity);
        inventory.setLockedStock(inventory.getLockedStock() - quantity);
        inventory.setAvailableStock(inventory.getAvailableStock() - quantity);
        
        this.updateById(inventory);
        
        log.info("库存扣减完成: skuId={}, 扣减后库存={}", skuId, inventory.getStock());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStock(Long skuId, Long locationId, String batchNo, Integer quantity) {
        log.info("开始增加库存: skuId={}, locationId={}, batchNo={}, quantity={}", skuId, locationId, batchNo, quantity);
        
        if (quantity <= 0) {
            throw new BusinessException("增加数量必须大于0");
        }
        
        Inventory inventory = baseMapper.selectBySkuAndLocationWithLock(skuId, locationId, batchNo);
        
        if (inventory == null) {
            throw new BusinessException("库存记录不存在，请先创建库存记录");
        }
        
        inventory.setStock(inventory.getStock() + quantity);
        inventory.setAvailableStock(inventory.getAvailableStock() + quantity);
        
        this.updateById(inventory);
        
        log.info("库存增加完成: skuId={}, 增加后库存={}", skuId, inventory.getStock());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockStock(Long skuId, Long locationId, String batchNo, Integer quantity) {
        log.info("开始锁定库存: skuId={}, locationId={}, batchNo={}, quantity={}", skuId, locationId, batchNo, quantity);
        
        if (quantity <= 0) {
            throw new BusinessException("锁定数量必须大于0");
        }
        
        Inventory inventory = baseMapper.selectBySkuAndLocationWithLock(skuId, locationId, batchNo);
        
        if (inventory == null) {
            throw new BusinessException("库存记录不存在");
        }
        
        if (inventory.getAvailableStock() < quantity) {
            throw new BusinessException("可用库存不足，可用库存: " + inventory.getAvailableStock() + ", 需要锁定: " + quantity);
        }
        
        inventory.setLockedStock(inventory.getLockedStock() + quantity);
        inventory.setAvailableStock(inventory.getAvailableStock() - quantity);
        
        this.updateById(inventory);
        
        log.info("库存锁定完成: skuId={}, 锁定数量={}, 可用库存={}", skuId, quantity, inventory.getAvailableStock());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockStock(Long skuId, Long locationId, String batchNo, Integer quantity) {
        log.info("开始解锁库存: skuId={}, locationId={}, batchNo={}, quantity={}", skuId, locationId, batchNo, quantity);
        
        if (quantity <= 0) {
            throw new BusinessException("解锁数量必须大于0");
        }
        
        Inventory inventory = baseMapper.selectBySkuAndLocationWithLock(skuId, locationId, batchNo);
        
        if (inventory == null) {
            throw new BusinessException("库存记录不存在");
        }
        
        if (inventory.getLockedStock() < quantity) {
            throw new BusinessException("锁定库存不足，锁定库存: " + inventory.getLockedStock() + ", 需要解锁: " + quantity);
        }
        
        inventory.setLockedStock(inventory.getLockedStock() - quantity);
        inventory.setAvailableStock(inventory.getAvailableStock() + quantity);
        
        this.updateById(inventory);
        
        log.info("库存解锁完成: skuId={}, 解锁数量={}, 可用库存={}", skuId, quantity, inventory.getAvailableStock());
    }
}
