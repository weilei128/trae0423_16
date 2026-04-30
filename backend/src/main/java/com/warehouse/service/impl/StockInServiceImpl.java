package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.common.BusinessException;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Inventory;
import com.warehouse.entity.Sku;
import com.warehouse.entity.StockIn;
import com.warehouse.entity.StockInDetail;
import com.warehouse.mapper.InventoryMapper;
import com.warehouse.mapper.SkuMapper;
import com.warehouse.mapper.StockInMapper;
import com.warehouse.service.InventoryService;
import com.warehouse.service.StockInDetailService;
import com.warehouse.service.StockInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockInServiceImpl extends ServiceImpl<StockInMapper, StockIn> implements StockInService {

    private final StockInDetailService stockInDetailService;
    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;
    private final SkuMapper skuMapper;

    @Override
    public Page<StockIn> queryPage(PageQuery pageQuery, StockIn stockIn) {
        Page<StockIn> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<StockIn> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(stockIn.getInNo())) {
            wrapper.like(StockIn::getInNo, stockIn.getInNo());
        }
        if (StringUtils.hasText(stockIn.getInType())) {
            wrapper.eq(StockIn::getInType, stockIn.getInType());
        }
        if (stockIn.getSupplierId() != null) {
            wrapper.eq(StockIn::getSupplierId, stockIn.getSupplierId());
        }
        if (stockIn.getStatus() != null) {
            wrapper.eq(StockIn::getStatus, stockIn.getStatus());
        }
        
        wrapper.orderByDesc(StockIn::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public StockIn getWithDetails(Long id) {
        StockIn stockIn = this.getById(id);
        if (stockIn != null) {
            List<StockInDetail> details = stockInDetailService.getByStockInId(id);
        }
        return stockIn;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createStockIn(StockIn stockIn, List<StockInDetail> details) {
        log.info("创建入库单: inType={}", stockIn.getInType());
        
        String inNo = generateInNo(stockIn.getInType());
        stockIn.setInNo(inNo);
        stockIn.setStatus(0);
        stockIn.setTotalQty(details.stream().mapToInt(StockInDetail::getPlanQty).sum());
        stockIn.setInspectedQty(0);
        stockIn.setShelvedQty(0);
        
        boolean result = this.save(stockIn);
        
        if (result && details != null && !details.isEmpty()) {
            for (StockInDetail detail : details) {
                detail.setStockInId(stockIn.getId());
                detail.setInNo(inNo);
                detail.setStatus(0);
                
                Sku sku = skuMapper.selectById(detail.getSkuId());
                if (sku != null) {
                    detail.setSkuName(sku.getSkuName());
                }
            }
            stockInDetailService.saveBatch(details);
        }
        
        log.info("入库单创建成功: inNo={}", inNo);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean inspect(Long id, List<StockInDetail> details) {
        log.info("验货确认: stockInId={}", id);
        
        StockIn stockIn = this.getById(id);
        if (stockIn == null) {
            throw new BusinessException("入库单不存在");
        }
        if (stockIn.getStatus() != 0 && stockIn.getStatus() != 1) {
            throw new BusinessException("入库单状态不正确，无法验货");
        }
        
        stockIn.setStatus(1);
        
        int totalInspected = 0;
        int totalQualified = 0;
        
        for (StockInDetail detail : details) {
            StockInDetail existingDetail = stockInDetailService.getById(detail.getId());
            if (existingDetail != null) {
                existingDetail.setInspectedQty(detail.getInspectedQty());
                existingDetail.setQualifiedQty(detail.getQualifiedQty());
                existingDetail.setUnqualifiedQty(detail.getInspectedQty() - detail.getQualifiedQty());
                existingDetail.setStatus(1);
                stockInDetailService.updateById(existingDetail);
                
                totalInspected += detail.getInspectedQty();
                totalQualified += detail.getQualifiedQty();
            }
        }
        
        stockIn.setInspectedQty(totalInspected);
        stockIn.setStatus(2);
        
        boolean result = this.updateById(stockIn);
        log.info("验货完成: stockInId={}, inspectedQty={}, qualifiedQty={}", id, totalInspected, totalQualified);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean shelve(Long id, List<StockInDetail> details) {
        log.info("上架确认: stockInId={}", id);
        
        StockIn stockIn = this.getById(id);
        if (stockIn == null) {
            throw new BusinessException("入库单不存在");
        }
        if (stockIn.getStatus() != 2 && stockIn.getStatus() != 3) {
            throw new BusinessException("入库单状态不正确，无法上架");
        }
        
        stockIn.setStatus(3);
        
        int totalShelved = 0;
        
        for (StockInDetail detail : details) {
            StockInDetail existingDetail = stockInDetailService.getById(detail.getId());
            if (existingDetail != null) {
                existingDetail.setShelvedQty(detail.getShelvedQty());
                existingDetail.setLocationId(detail.getLocationId());
                existingDetail.setLocationCode(detail.getLocationCode());
                existingDetail.setBatchNo(detail.getBatchNo());
                existingDetail.setProductionDate(detail.getProductionDate());
                existingDetail.setExpiryDate(detail.getExpiryDate());
                existingDetail.setStatus(2);
                stockInDetailService.updateById(existingDetail);
                
                totalShelved += detail.getShelvedQty();
                
                addInventory(detail);
            }
        }
        
        stockIn.setShelvedQty(totalShelved);
        stockIn.setStatus(4);
        
        boolean result = this.updateById(stockIn);
        log.info("上架完成: stockInId={}, shelvedQty={}", id, totalShelved);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(Long id) {
        log.info("取消入库单: stockInId={}", id);
        
        StockIn stockIn = this.getById(id);
        if (stockIn == null) {
            throw new BusinessException("入库单不存在");
        }
        if (stockIn.getStatus() == 4) {
            throw new BusinessException("入库单已完成，无法取消");
        }
        
        stockIn.setStatus(5);
        boolean result = this.updateById(stockIn);
        
        log.info("入库单已取消: stockInId={}", id);
        return result;
    }

    private void addInventory(StockInDetail detail) {
        log.info("增加库存: skuId={}, locationId={}, qty={}", detail.getSkuId(), detail.getLocationId(), detail.getShelvedQty());
        
        Inventory inventory = inventoryMapper.selectBySkuAndLocationWithLock(
                detail.getSkuId(), detail.getLocationId(), detail.getBatchNo());
        
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setSkuId(detail.getSkuId());
            inventory.setSkuCode(detail.getSkuCode());
            inventory.setSkuName(detail.getSkuName());
            inventory.setLocationId(detail.getLocationId());
            inventory.setLocationCode(detail.getLocationCode());
            inventory.setStock(detail.getShelvedQty());
            inventory.setLockedStock(0);
            inventory.setAvailableStock(detail.getShelvedQty());
            inventory.setBatchNo(detail.getBatchNo());
            inventory.setProductionDate(detail.getProductionDate());
            inventory.setExpiryDate(detail.getExpiryDate());
            inventoryMapper.insert(inventory);
        } else {
            inventory.setStock(inventory.getStock() + detail.getShelvedQty());
            inventory.setAvailableStock(inventory.getAvailableStock() + detail.getShelvedQty());
            inventoryMapper.updateById(inventory);
        }
    }

    private String generateInNo(String inType) {
        String prefix = "PURCHASE".equals(inType) ? "PO" : "RO";
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long millis = System.currentTimeMillis() % 10000;
        return prefix + datePart + String.format("%04d", millis);
    }
}
