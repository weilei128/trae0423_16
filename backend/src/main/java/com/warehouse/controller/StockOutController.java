package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.dto.PageQuery;
import com.warehouse.dto.StockOutCreateRequest;
import com.warehouse.entity.StockOut;
import com.warehouse.entity.StockOutDetail;
import com.warehouse.service.StockOutDetailService;
import com.warehouse.service.StockOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-out")
@RequiredArgsConstructor
public class StockOutController {

    private final StockOutService stockOutService;
    private final StockOutDetailService stockOutDetailService;

    @GetMapping("/page")
    public Result<Page<StockOut>> page(PageQuery pageQuery, StockOut stockOut) {
        Page<StockOut> result = stockOutService.queryPage(pageQuery, stockOut);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<StockOut> getById(@PathVariable Long id) {
        StockOut stockOut = stockOutService.getById(id);
        return Result.success(stockOut);
    }

    @GetMapping("/{id}/details")
    public Result<List<StockOutDetail>> getDetails(@PathVariable Long id) {
        List<StockOutDetail> details = stockOutDetailService.getByStockOutId(id);
        return Result.success(details);
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody StockOutCreateRequest request) {
        boolean result = stockOutService.createStockOut(request.getStockOut(), request.getDetails());
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody StockOut stockOut) {
        boolean result = stockOutService.updateById(stockOut);
        return Result.success(result);
    }

    @PostMapping("/pick/{id}")
    public Result<Boolean> pick(@PathVariable Long id, @RequestBody List<StockOutDetail> details) {
        boolean result = stockOutService.pick(id, details);
        return Result.success(result);
    }

    @PostMapping("/check/{id}")
    public Result<Boolean> check(@PathVariable Long id, @RequestBody List<StockOutDetail> details) {
        boolean result = stockOutService.check(id, details);
        return Result.success(result);
    }

    @PostMapping("/cancel/{id}")
    public Result<Boolean> cancel(@PathVariable Long id) {
        boolean result = stockOutService.cancel(id);
        return Result.success(result);
    }
}
