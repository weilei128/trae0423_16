package com.warehouse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.dto.PageQuery;
import com.warehouse.entity.Location;

public interface LocationService extends IService<Location> {

    Page<Location> queryPage(PageQuery pageQuery, Location location);

    Location getByLocationCode(String locationCode);

    boolean updateLocation(Location location);

    boolean removeLocation(Long id);

    void clearCache(String locationCode);
}
