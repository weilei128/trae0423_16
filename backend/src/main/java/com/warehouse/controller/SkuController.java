package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Sku;
import com.warehouse.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sku")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @GetMapping("/page")
    public Result<Page<Sku>> page(PageQuery pageQuery, Sku sku) {
        Page<Sku> result = skuService.queryPage(pageQuery, sku);
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<Sku>> list() {
        List<Sku> list = skuService.lambdaQuery()
                .eq(Sku::getStatus, 1)
                .orderByAsc(Sku::getSkuCode)
                .list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Sku> getById(@PathVariable Long id) {
        Sku sku = skuService.getById(id);
        return Result.success(sku);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Sku sku) {
        boolean result = skuService.save(sku);
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Sku sku) {
        boolean result = skuService.updateById(sku);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = skuService.removeById(id);
        return Result.success(result);
    }
}
