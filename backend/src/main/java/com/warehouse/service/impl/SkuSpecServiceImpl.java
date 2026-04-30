package com.warehouse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.entity.SkuSpec;
import com.warehouse.mapper.SkuSpecMapper;
import com.warehouse.service.SkuSpecService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuSpecServiceImpl extends ServiceImpl<SkuSpecMapper, SkuSpec> implements SkuSpecService {

    @Override
    public List<SkuSpec> getBySkuId(Long skuId) {
        return this.lambdaQuery()
                .eq(SkuSpec::getSkuId, skuId)
                .list();
    }
}
