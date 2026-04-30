package com.warehouse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.Result;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Location;
import com.warehouse.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/page")
    public Result<Page<Location>> page(PageQuery pageQuery, Location location) {
        Page<Location> result = locationService.queryPage(pageQuery, location);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Location> getById(@PathVariable Long id) {
        Location location = locationService.getById(id);
        return Result.success(location);
    }

    @GetMapping("/code/{locationCode}")
    public Result<Location> getByCode(@PathVariable String locationCode) {
        Location location = locationService.getByLocationCode(locationCode);
        return Result.success(location);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Location location) {
        boolean result = locationService.save(location);
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Location location) {
        boolean result = locationService.updateLocation(location);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = locationService.removeLocation(id);
        return Result.success(result);
    }

    @PostMapping("/clearCache/{locationCode}")
    public Result<Void> clearCache(@PathVariable String locationCode) {
        locationService.clearCache(locationCode);
        return Result.success();
    }
}
