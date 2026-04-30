package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.dto.PageQuery;
import com.warehouse.dto.StockInCreateRequest;
import com.warehouse.entity.StockIn;
import com.warehouse.entity.StockInDetail;
import com.warehouse.service.StockInDetailService;
import com.warehouse.service.StockInService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-in")
@RequiredArgsConstructor
public class StockInController {

    private final StockInService stockInService;
    private final StockInDetailService stockInDetailService;

    @GetMapping("/page")
    public Result<Page<StockIn>> page(PageQuery pageQuery, StockIn stockIn) {
        Page<StockIn> result = stockInService.queryPage(pageQuery, stockIn);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<StockIn> getById(@PathVariable Long id) {
        StockIn stockIn = stockInService.getById(id);
        return Result.success(stockIn);
    }

    @GetMapping("/{id}/details")
    public Result<List<StockInDetail>> getDetails(@PathVariable Long id) {
        List<StockInDetail> details = stockInDetailService.getByStockInId(id);
        return Result.success(details);
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody StockInCreateRequest request) {
        boolean result = stockInService.createStockIn(request.getStockIn(), request.getDetails());
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody StockIn stockIn) {
        boolean result = stockInService.updateById(stockIn);
        return Result.success(result);
    }

    @PostMapping("/inspect/{id}")
    public Result<Boolean> inspect(@PathVariable Long id, @RequestBody List<StockInDetail> details) {
        boolean result = stockInService.inspect(id, details);
        return Result.success(result);
    }

    @PostMapping("/shelve/{id}")
    public Result<Boolean> shelve(@PathVariable Long id, @RequestBody List<StockInDetail> details) {
        boolean result = stockInService.shelve(id, details);
        return Result.success(result);
    }

    @PostMapping("/cancel/{id}")
    public Result<Boolean> cancel(@PathVariable Long id) {
        boolean result = stockInService.cancel(id);
        return Result.success(result);
    }
}
