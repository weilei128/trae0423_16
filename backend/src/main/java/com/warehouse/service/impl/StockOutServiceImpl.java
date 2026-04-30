package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.common.BusinessException;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Inventory;
import com.warehouse.entity.Sku;
import com.warehouse.entity.StockOut;
import com.warehouse.entity.StockOutDetail;
import com.warehouse.mapper.InventoryMapper;
import com.warehouse.mapper.SkuMapper;
import com.warehouse.mapper.StockOutMapper;
import com.warehouse.service.InventoryService;
import com.warehouse.service.StockOutDetailService;
import com.warehouse.service.StockOutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockOutServiceImpl extends ServiceImpl<StockOutMapper, StockOut> implements StockOutService {

    private final StockOutDetailService stockOutDetailService;
    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;
    private final SkuMapper skuMapper;

    @Override
    public Page<StockOut> queryPage(PageQuery pageQuery, StockOut stockOut) {
        Page<StockOut> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<StockOut> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(stockOut.getOutNo())) {
            wrapper.like(StockOut::getOutNo, stockOut.getOutNo());
        }
        if (StringUtils.hasText(stockOut.getOutType())) {
            wrapper.eq(StockOut::getOutType, stockOut.getOutType());
        }
        if (stockOut.getStatus() != null) {
            wrapper.eq(StockOut::getStatus, stockOut.getStatus());
        }
        
        wrapper.orderByDesc(StockOut::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public StockOut getWithDetails(Long id) {
        StockOut stockOut = this.getById(id);
        if (stockOut != null) {
            List<StockOutDetail> details = stockOutDetailService.getByStockOutId(id);
        }
        return stockOut;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createStockOut(StockOut stockOut, List<StockOutDetail> details) {
        log.info("创建出库单: outType={}", stockOut.getOutType());
        
        String outNo = generateOutNo(stockOut.getOutType());
        stockOut.setOutNo(outNo);
        stockOut.setStatus(0);
        stockOut.setTotalQty(details.stream().mapToInt(StockOutDetail::getPlanQty).sum());
        stockOut.setPickedQty(0);
        stockOut.setCheckedQty(0);
        
        boolean result = this.save(stockOut);
        
        if (result && details != null && !details.isEmpty()) {
            for (StockOutDetail detail : details) {
                detail.setStockOutId(stockOut.getId());
                detail.setOutNo(outNo);
                detail.setStatus(0);
                
                Sku sku = skuMapper.selectById(detail.getSkuId());
                if (sku != null) {
                    detail.setSkuName(sku.getSkuName());
                    detail.setSkuCode(sku.getSkuCode());
                }
            }
            stockOutDetailService.saveBatch(details);
        }
        
        log.info("出库单创建成功: outNo={}", outNo);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pick(Long id, List<StockOutDetail> details) {
        log.info("拣货确认: stockOutId={}", id);
        
        StockOut stockOut = this.getById(id);
        if (stockOut == null) {
            throw new BusinessException("出库单不存在");
        }
        if (stockOut.getStatus() != 0 && stockOut.getStatus() != 1) {
            throw new BusinessException("出库单状态不正确，无法拣货");
        }
        
        stockOut.setStatus(1);
        
        int totalPicked = 0;
        
        for (StockOutDetail detail : details) {
            StockOutDetail existingDetail = stockOutDetailService.getById(detail.getId());
            if (existingDetail != null) {
                existingDetail.setPickedQty(detail.getPickedQty());
                existingDetail.setLocationId(detail.getLocationId());
                existingDetail.setLocationCode(detail.getLocationCode());
                existingDetail.setBatchNo(detail.getBatchNo());
                existingDetail.setStatus(1);
                stockOutDetailService.updateById(existingDetail);
                
                totalPicked += detail.getPickedQty();
            }
        }
        
        stockOut.setPickedQty(totalPicked);
        stockOut.setStatus(2);
        
        boolean result = this.updateById(stockOut);
        log.info("拣货完成: stockOutId={}, pickedQty={}", id, totalPicked);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean check(Long id, List<StockOutDetail> details) {
        log.info("复核确认: stockOutId={}", id);
        
        StockOut stockOut = this.getById(id);
        if (stockOut == null) {
            throw new BusinessException("出库单不存在");
        }
        if (stockOut.getStatus() != 2 && stockOut.getStatus() != 3) {
            throw new BusinessException("出库单状态不正确，无法复核");
        }
        
        stockOut.setStatus(3);
        
        int totalChecked = 0;
        
        for (StockOutDetail detail : details) {
            StockOutDetail existingDetail = stockOutDetailService.getById(detail.getId());
            if (existingDetail != null) {
                existingDetail.setCheckedQty(detail.getCheckedQty());
                existingDetail.setStatus(2);
                stockOutDetailService.updateById(existingDetail);
                
                totalChecked += detail.getCheckedQty();
                
                deductInventory(existingDetail);
            }
        }
        
        stockOut.setCheckedQty(totalChecked);
        stockOut.setStatus(4);
        
        boolean result = this.updateById(stockOut);
        log.info("复核完成: stockOutId={}, checkedQty={}", id, totalChecked);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(Long id) {
        log.info("取消出库单: stockOutId={}", id);
        
        StockOut stockOut = this.getById(id);
        if (stockOut == null) {
            throw new BusinessException("出库单不存在");
        }
        if (stockOut.getStatus() == 4) {
            throw new BusinessException("出库单已完成，无法取消");
        }
        
        stockOut.setStatus(5);
        boolean result = this.updateById(stockOut);
        
        log.info("出库单已取消: stockOutId={}", id);
        return result;
    }

    private void deductInventory(StockOutDetail detail) {
        log.info("扣减库存: skuId={}, locationId={}, batchNo={}, qty={}", 
                detail.getSkuId(), detail.getLocationId(), detail.getBatchNo(), detail.getCheckedQty());
        
        inventoryService.deductStock(detail.getSkuId(), detail.getLocationId(), detail.getBatchNo(), detail.getCheckedQty());
    }

    private String generateOutNo(String outType) {
        String prefix = "SALE".equals(outType) ? "SO" : "RT";
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long millis = System.currentTimeMillis() % 10000;
        return prefix + datePart + String.format("%04d", millis);
    }
}
