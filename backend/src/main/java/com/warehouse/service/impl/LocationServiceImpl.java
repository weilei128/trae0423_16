package com.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Location;
import com.warehouse.mapper.LocationMapper;
import com.warehouse.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location> implements LocationService {

    private static final String LOCATION_CACHE_KEY = "warehouse:location:";
    private static final long CACHE_EXPIRE_HOURS = 1;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<Location> queryPage(PageQuery pageQuery, Location location) {
        Page<Location> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        LambdaQueryWrapper<Location> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(location.getLocationCode())) {
            wrapper.like(Location::getLocationCode, location.getLocationCode());
        }
        if (StringUtils.hasText(location.getLocationName())) {
            wrapper.like(Location::getLocationName, location.getLocationName());
        }
        if (StringUtils.hasText(location.getWarehouseCode())) {
            wrapper.eq(Location::getWarehouseCode, location.getWarehouseCode());
        }
        if (StringUtils.hasText(location.getArea())) {
            wrapper.eq(Location::getArea, location.getArea());
        }
        if (location.getStatus() != null) {
            wrapper.eq(Location::getStatus, location.getStatus());
        }
        
        wrapper.orderByAsc(Location::getWarehouseCode, Location::getArea, Location::getRow, Location::getColumn, Location::getLayer);
        return this.page(page, wrapper);
    }

    @Override
    public Location getByLocationCode(String locationCode) {
        log.info("查询货位信息: locationCode={}", locationCode);
        
        String cacheKey = LOCATION_CACHE_KEY + locationCode;
        
        Location location = (Location) redisTemplate.opsForValue().get(cacheKey);
        if (location != null) {
            log.info("从缓存获取货位信息: locationCode={}", locationCode);
            return location;
        }
        
        log.info("缓存未命中，查询数据库: locationCode={}", locationCode);
        location = this.lambdaQuery()
                .eq(Location::getLocationCode, locationCode)
                .one();
        
        if (location != null) {
            log.info("将货位信息写入缓存: locationCode={}", locationCode);
            redisTemplate.opsForValue().set(cacheKey, location, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
        
        return location;
    }

    @Override
    public boolean updateLocation(Location location) {
        log.info("更新货位信息: locationCode={}", location.getLocationCode());
        
        boolean result = this.updateById(location);
        
        if (result) {
            String cacheKey = LOCATION_CACHE_KEY + location.getLocationCode();
            Location updatedLocation = this.getById(location.getId());
            if (updatedLocation != null) {
                log.info("更新货位缓存: locationCode={}", location.getLocationCode());
                redisTemplate.opsForValue().set(cacheKey, updatedLocation, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
            }
        }
        
        return result;
    }

    @Override
    public boolean removeLocation(Long id) {
        log.info("删除货位信息: id={}", id);
        
        Location location = this.getById(id);
        if (location == null) {
            return false;
        }
        
        boolean result = this.removeById(id);
        
        if (result) {
            String cacheKey = LOCATION_CACHE_KEY + location.getLocationCode();
            log.info("删除货位缓存: locationCode={}", location.getLocationCode());
            redisTemplate.delete(cacheKey);
        }
        
        return result;
    }

    @Override
    public void clearCache(String locationCode) {
        String cacheKey = LOCATION_CACHE_KEY + locationCode;
        log.info("清除货位缓存: locationCode={}", locationCode);
        redisTemplate.delete(cacheKey);
    }
}
