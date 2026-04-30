package com.warehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.entity.Inventory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface InventoryMapper extends BaseMapper<Inventory> {

    @Select("SELECT * FROM inventory WHERE sku_id = #{skuId} AND location_id = #{locationId} AND batch_no = #{batchNo} FOR UPDATE")
    Inventory selectBySkuAndLocationWithLock(@Param("skuId") Long skuId,
                                                @Param("locationId") Long locationId,
                                                @Param("batchNo") String batchNo);

    @Select("SELECT * FROM inventory WHERE id = #{id} FOR UPDATE")
    Inventory selectByIdWithLock(@Param("id") Long id);
}
