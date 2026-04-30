package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Inventory;
import com.warehouse.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/page")
    public Result<Page<Inventory>> page(PageQuery pageQuery, Inventory inventory) {
        Page<Inventory> result = inventoryService.queryPage(pageQuery, inventory);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Inventory> getById(@PathVariable Long id) {
        Inventory inventory = inventoryService.getById(id);
        return Result.success(inventory);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Inventory inventory) {
        boolean result = inventoryService.save(inventory);
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Inventory inventory) {
        boolean result = inventoryService.updateById(inventory);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = inventoryService.removeById(id);
        return Result.success(result);
    }

    @PostMapping("/deduct")
    public Result<Void> deductStock(@RequestParam Long skuId,
                                      @RequestParam Long locationId,
                                      @RequestParam(required = false) String batchNo,
                                      @RequestParam Integer quantity) {
        inventoryService.deductStock(skuId, locationId, batchNo, quantity);
        return Result.success();
    }

    @PostMapping("/add")
    public Result<Void> addStock(@RequestParam Long skuId,
                                   @RequestParam Long locationId,
                                   @RequestParam(required = false) String batchNo,
                                   @RequestParam Integer quantity) {
        inventoryService.addStock(skuId, locationId, batchNo, quantity);
        return Result.success();
    }

    @PostMapping("/lock")
    public Result<Void> lockStock(@RequestParam Long skuId,
                                    @RequestParam Long locationId,
                                    @RequestParam(required = false) String batchNo,
                                    @RequestParam Integer quantity) {
        inventoryService.lockStock(skuId, locationId, batchNo, quantity);
        return Result.success();
    }

    @PostMapping("/unlock")
    public Result<Void> unlockStock(@RequestParam Long skuId,
                                      @RequestParam Long locationId,
                                      @RequestParam(required = false) String batchNo,
                                      @RequestParam Integer quantity) {
        inventoryService.unlockStock(skuId, locationId, batchNo, quantity);
        return Result.success();
    }
}
