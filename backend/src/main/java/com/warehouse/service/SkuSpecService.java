package com.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.warehouse.entity.SkuSpec;

import java.util.List;

public interface SkuSpecService extends IService<SkuSpec> {

    List<SkuSpec> getBySkuId(Long skuId);
}
