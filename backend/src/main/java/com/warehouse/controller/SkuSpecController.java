package com.warehouse.controller;

import com.warehouse.common.Result;
import com.warehouse.entity.SkuSpec;
import com.warehouse.service.SkuSpecService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sku-spec")
@RequiredArgsConstructor
public class SkuSpecController {

    private final SkuSpecService skuSpecService;

    @GetMapping("/sku/{skuId}")
    public Result<List<SkuSpec>> getBySkuId(@PathVariable Long skuId) {
        List<SkuSpec> list = skuSpecService.getBySkuId(skuId);
        return Result.success(list);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody SkuSpec skuSpec) {
        boolean result = skuSpecService.save(skuSpec);
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody SkuSpec skuSpec) {
        boolean result = skuSpecService.updateById(skuSpec);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = skuSpecService.removeById(id);
        return Result.success(result);
    }

    @DeleteMapping("/sku/{skuId}")
    public Result<Boolean> deleteBySkuId(@PathVariable Long skuId) {
        boolean result = skuSpecService.remove(skuSpecService.lambdaQuery()
                .eq(SkuSpec::getSkuId, skuId)
                .getWrapper());
        return Result.success(result);
    }
}
