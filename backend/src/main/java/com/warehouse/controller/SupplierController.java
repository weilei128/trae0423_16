package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Supplier;
import com.warehouse.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping("/page")
    public Result<Page<Supplier>> page(PageQuery pageQuery, Supplier supplier) {
        Page<Supplier> result = supplierService.queryPage(pageQuery, supplier);
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<Supplier>> list() {
        List<Supplier> list = supplierService.lambdaQuery()
                .eq(Supplier::getStatus, 1)
                .orderByAsc(Supplier::getSupplierCode)
                .list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Supplier> getById(@PathVariable Long id) {
        Supplier supplier = supplierService.getById(id);
        return Result.success(supplier);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Supplier supplier) {
        boolean result = supplierService.save(supplier);
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Supplier supplier) {
        boolean result = supplierService.updateById(supplier);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = supplierService.removeById(id);
        return Result.success(result);
    }
}
