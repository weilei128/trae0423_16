-- 仓储物流配送管理系统数据库表结构

-- 1. 供应商表
CREATE TABLE IF NOT EXISTS `supplier` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `supplier_code` varchar(50) NOT NULL COMMENT '供应商编码',
  `supplier_name` varchar(100) NOT NULL COMMENT '供应商名称',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `status` tinyint DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_supplier_code` (`supplier_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 2. SKU货品档案表
CREATE TABLE IF NOT EXISTS `sku` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sku_code` varchar(50) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(100) NOT NULL COMMENT 'SKU名称',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `specs` varchar(255) DEFAULT NULL COMMENT '规格描述',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `supplier_code` varchar(50) DEFAULT NULL COMMENT '供应商编码',
  `supplier_name` varchar(100) DEFAULT NULL COMMENT '供应商名称',
  `weight` decimal(10,2) DEFAULT 0.00 COMMENT '重量(kg)',
  `volume` decimal(10,2) DEFAULT 0.00 COMMENT '体积(m³)',
  `status` tinyint DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_code` (`sku_code`),
  KEY `idx_supplier_id` (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SKU货品档案表';

-- 3. SKU规格参数表
CREATE TABLE IF NOT EXISTS `sku_spec` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(50) NOT NULL COMMENT 'SKU编码',
  `spec_key` varchar(50) NOT NULL COMMENT '参数名',
  `spec_value` varchar(255) DEFAULT NULL COMMENT '参数值',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SKU规格参数表';

-- 4. 货位表
CREATE TABLE IF NOT EXISTS `location` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `location_code` varchar(50) NOT NULL COMMENT '货位编码',
  `location_name` varchar(100) DEFAULT NULL COMMENT '货位名称',
  `warehouse_code` varchar(50) DEFAULT NULL COMMENT '仓库编码',
  `warehouse_name` varchar(100) DEFAULT NULL COMMENT '仓库名称',
  `area` varchar(50) DEFAULT NULL COMMENT '区域',
  `row` varchar(20) DEFAULT NULL COMMENT '排',
  `column` varchar(20) DEFAULT NULL COMMENT '列',
  `layer` varchar(20) DEFAULT NULL COMMENT '层',
  `max_capacity` int DEFAULT 0 COMMENT '最大容量',
  `used_capacity` int DEFAULT 0 COMMENT '已用容量',
  `status` tinyint DEFAULT 1 COMMENT '状态 0:禁用 1:启用 2:占用 3:空闲',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_location_code` (`location_code`),
  KEY `idx_warehouse_code` (`warehouse_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='货位表';

-- 5. 库存表 (使用悲观锁)
CREATE TABLE IF NOT EXISTS `inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(50) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(100) NOT NULL COMMENT 'SKU名称',
  `location_id` bigint NOT NULL COMMENT '货位ID',
  `location_code` varchar(50) NOT NULL COMMENT '货位编码',
  `stock` int DEFAULT 0 COMMENT '库存数量',
  `locked_stock` int DEFAULT 0 COMMENT '锁定库存',
  `available_stock` int DEFAULT 0 COMMENT '可用库存',
  `batch_no` varchar(50) DEFAULT NULL COMMENT '批次号',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期至',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_location_batch` (`sku_id`, `location_id`, `batch_no`),
  KEY `idx_sku_code` (`sku_code`),
  KEY `idx_location_code` (`location_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 6. 预警阈值表
CREATE TABLE IF NOT EXISTS `alert_threshold` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(50) NOT NULL COMMENT 'SKU编码',
  `min_stock` int DEFAULT 0 COMMENT '最小库存预警值',
  `max_stock` int DEFAULT NULL COMMENT '最大库存预警值',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警阈值表';

-- 7. 入库单表
CREATE TABLE IF NOT EXISTS `stock_in` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `in_no` varchar(50) NOT NULL COMMENT '入库单号',
  `in_type` varchar(50) NOT NULL COMMENT '入库类型: PURCHASE-采购入库, RETURN-退货入库',
  `ref_no` varchar(50) DEFAULT NULL COMMENT '关联单号(采购单号/退货单号)',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `supplier_code` varchar(50) DEFAULT NULL COMMENT '供应商编码',
  `supplier_name` varchar(100) DEFAULT NULL COMMENT '供应商名称',
  `warehouse_code` varchar(50) DEFAULT NULL COMMENT '仓库编码',
  `warehouse_name` varchar(100) DEFAULT NULL COMMENT '仓库名称',
  `total_qty` int DEFAULT 0 COMMENT '总数量',
  `inspected_qty` int DEFAULT 0 COMMENT '已验货数量',
  `shelved_qty` int DEFAULT 0 COMMENT '已上架数量',
  `status` tinyint DEFAULT 0 COMMENT '状态 0:待验货 1:验货中 2:待上架 3:上架中 4:已完成 5:已取消',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_in_no` (`in_no`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表';

-- 8. 入库单明细表
CREATE TABLE IF NOT EXISTS `stock_in_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stock_in_id` bigint NOT NULL COMMENT '入库单ID',
  `in_no` varchar(50) NOT NULL COMMENT '入库单号',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(50) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(100) NOT NULL COMMENT 'SKU名称',
  `plan_qty` int DEFAULT 0 COMMENT '计划数量',
  `inspected_qty` int DEFAULT 0 COMMENT '验货数量',
  `qualified_qty` int DEFAULT 0 COMMENT '合格数量',
  `unqualified_qty` int DEFAULT 0 COMMENT '不合格数量',
  `shelved_qty` int DEFAULT 0 COMMENT '已上架数量',
  `location_id` bigint DEFAULT NULL COMMENT '货位ID',
  `location_code` varchar(50) DEFAULT NULL COMMENT '货位编码',
  `batch_no` varchar(50) DEFAULT NULL COMMENT '批次号',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期至',
  `status` tinyint DEFAULT 0 COMMENT '状态 0:待验货 1:已验货 2:已上架',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_stock_in_id` (`stock_in_id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单明细表';

-- 9. 出库单表
CREATE TABLE IF NOT EXISTS `stock_out` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `out_no` varchar(50) NOT NULL COMMENT '出库单号',
  `out_type` varchar(50) NOT NULL COMMENT '出库类型: SALE-销售出库, RETURN_TO_SUPPLIER-退货出库',
  `ref_no` varchar(50) DEFAULT NULL COMMENT '关联单号(销售单号/退货单号)',
  `customer_name` varchar(100) DEFAULT NULL COMMENT '客户名称',
  `customer_phone` varchar(20) DEFAULT NULL COMMENT '客户电话',
  `customer_address` varchar(255) DEFAULT NULL COMMENT '客户地址',
  `warehouse_code` varchar(50) DEFAULT NULL COMMENT '仓库编码',
  `warehouse_name` varchar(100) DEFAULT NULL COMMENT '仓库名称',
  `total_qty` int DEFAULT 0 COMMENT '总数量',
  `picked_qty` int DEFAULT 0 COMMENT '已拣货数量',
  `checked_qty` int DEFAULT 0 COMMENT '已复核数量',
  `status` tinyint DEFAULT 0 COMMENT '状态 0:待拣货 1:拣货中 2:待复核 3:复核中 4:已完成 5:已取消',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_out_no` (`out_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

-- 10. 出库单明细表
CREATE TABLE IF NOT EXISTS `stock_out_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stock_out_id` bigint NOT NULL COMMENT '出库单ID',
  `out_no` varchar(50) NOT NULL COMMENT '出库单号',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(50) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(100) NOT NULL COMMENT 'SKU名称',
  `plan_qty` int DEFAULT 0 COMMENT '计划数量',
  `picked_qty` int DEFAULT 0 COMMENT '已拣货数量',
  `checked_qty` int DEFAULT 0 COMMENT '已复核数量',
  `location_id` bigint DEFAULT NULL COMMENT '货位ID',
  `location_code` varchar(50) DEFAULT NULL COMMENT '货位编码',
  `batch_no` varchar(50) DEFAULT NULL COMMENT '批次号',
  `status` tinyint DEFAULT 0 COMMENT '状态 0:待拣货 1:拣货中 2:已拣货 3:已复核',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_stock_out_id` (`stock_out_id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单明细表';

-- 11. 拣货任务表
CREATE TABLE IF NOT EXISTS `picking_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_no` varchar(50) NOT NULL COMMENT '任务单号',
  `stock_out_id` bigint NOT NULL COMMENT '出库单ID',
  `out_no` varchar(50) NOT NULL COMMENT '出库单号',
  `picker_id` bigint DEFAULT NULL COMMENT '拣货人ID',
  `picker_name` varchar(50) DEFAULT NULL COMMENT '拣货人姓名',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(50) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(100) NOT NULL COMMENT 'SKU名称',
  `location_id` bigint DEFAULT NULL COMMENT '货位ID',
  `location_code` varchar(50) DEFAULT NULL COMMENT '货位编码',
  `plan_qty` int DEFAULT 0 COMMENT '计划数量',
  `actual_qty` int DEFAULT 0 COMMENT '实际数量',
  `status` tinyint DEFAULT 0 COMMENT '状态 0:待分配 1:待拣货 2:拣货中 3:已完成 4:已取消',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_no` (`task_no`),
  KEY `idx_stock_out_id` (`stock_out_id`),
  KEY `idx_picker_id` (`picker_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拣货任务表';

-- 12. 承运商表
CREATE TABLE IF NOT EXISTS `carrier` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `carrier_code` varchar(50) NOT NULL COMMENT '承运商编码',
  `carrier_name` varchar(100) NOT NULL COMMENT '承运商名称',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `api_url` varchar(255) DEFAULT NULL COMMENT 'API地址',
  `api_key` varchar(100) DEFAULT NULL COMMENT 'API密钥',
  `status` tinyint DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_carrier_code` (`carrier_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='承运商表';

-- 13. 运单表
CREATE TABLE IF NOT EXISTS `waybill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `waybill_no` varchar(50) NOT NULL COMMENT '运单号',
  `stock_out_id` bigint DEFAULT NULL COMMENT '出库单ID',
  `out_no` varchar(50) DEFAULT NULL COMMENT '出库单号',
  `carrier_id` bigint DEFAULT NULL COMMENT '承运商ID',
  `carrier_code` varchar(50) DEFAULT NULL COMMENT '承运商编码',
  `carrier_name` varchar(100) DEFAULT NULL COMMENT '承运商名称',
  `sender_name` varchar(50) DEFAULT NULL COMMENT '发货人',
  `sender_phone` varchar(20) DEFAULT NULL COMMENT '发货人电话',
  `sender_address` varchar(255) DEFAULT NULL COMMENT '发货地址',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '收货人',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT '收货人电话',
  `receiver_address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `total_weight` decimal(10,2) DEFAULT 0.00 COMMENT '总重量(kg)',
  `total_volume` decimal(10,2) DEFAULT 0.00 COMMENT '总体积(m³)',
  `freight_amount` decimal(10,2) DEFAULT 0.00 COMMENT '运费',
  `status` tinyint DEFAULT 0 COMMENT '状态 0:待发货 1:运输中 2:派送中 3:已签收 4:已拒签 5:异常',
  `current_location` varchar(255) DEFAULT NULL COMMENT '当前位置',
  `estimated_arrival_time` datetime DEFAULT NULL COMMENT '预计送达时间',
  `actual_arrival_time` datetime DEFAULT NULL COMMENT '实际送达时间',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_waybill_no` (`waybill_no`),
  KEY `idx_stock_out_id` (`stock_out_id`),
  KEY `idx_carrier_id` (`carrier_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运单表';

-- 14. 运单轨迹表
CREATE TABLE IF NOT EXISTS `waybill_trace` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `waybill_id` bigint NOT NULL COMMENT '运单ID',
  `waybill_no` varchar(50) NOT NULL COMMENT '运单号',
  `status` tinyint NOT NULL COMMENT '状态',
  `location` varchar(255) DEFAULT NULL COMMENT '位置',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人',
  `operate_time` datetime DEFAULT NULL COMMENT '操作时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_waybill_id` (`waybill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运单轨迹表';

-- 15. 退货单表
CREATE TABLE IF NOT EXISTS `return_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `return_no` varchar(50) NOT NULL COMMENT '退货单号',
  `original_out_no` varchar(50) DEFAULT NULL COMMENT '原出库单号',
  `stock_out_id` bigint DEFAULT NULL COMMENT '原出库单ID',
  `customer_name` varchar(100) DEFAULT NULL COMMENT '客户名称',
  `customer_phone` varchar(20) DEFAULT NULL COMMENT '客户电话',
  `return_reason` varchar(255) DEFAULT NULL COMMENT '退货原因',
  `total_qty` int DEFAULT 0 COMMENT '退货总数量',
  `inspected_qty` int DEFAULT 0 COMMENT '已质检数量',
  `status` tinyint DEFAULT 0 COMMENT '状态 0:待处理 1:待质检 2:质检中 3:待入库 4:已完成 5:已取消',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_return_no` (`return_no`),
  KEY `idx_stock_out_id` (`stock_out_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货单表';

-- 16. 退货单明细表
CREATE TABLE IF NOT EXISTS `return_order_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `return_order_id` bigint NOT NULL COMMENT '退货单ID',
  `return_no` varchar(50) NOT NULL COMMENT '退货单号',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(50) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(100) NOT NULL COMMENT 'SKU名称',
  `return_qty` int DEFAULT 0 COMMENT '退货数量',
  `inspected_qty` int DEFAULT 0 COMMENT '已质检数量',
  `qualified_qty` int DEFAULT 0 COMMENT '合格数量',
  `unqualified_qty` int DEFAULT 0 COMMENT '不合格数量',
  `qc_result` varchar(50) DEFAULT NULL COMMENT '质检结果: QUALIFIED-合格, UNQUALIFIED-不合格',
  `qc_remark` varchar(255) DEFAULT NULL COMMENT '质检备注',
  `status` tinyint DEFAULT 0 COMMENT '状态 0:待质检 1:质检中 2:已质检',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_return_order_id` (`return_order_id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货单明细表';

-- 17. 异常记录表
CREATE TABLE IF NOT EXISTS `exception_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exception_no` varchar(50) NOT NULL COMMENT '异常单号',
  `source_type` varchar(50) NOT NULL COMMENT '来源类型: IN-入库, OUT-出库, RETURN-退货, WAYBILL-运单',
  `source_no` varchar(50) DEFAULT NULL COMMENT '来源单号',
  `exception_type` varchar(50) DEFAULT NULL COMMENT '异常类型',
  `description` varchar(500) DEFAULT NULL COMMENT '异常描述',
  `handler_id` bigint DEFAULT NULL COMMENT '处理人ID',
  `handler_name` varchar(50) DEFAULT NULL COMMENT '处理人姓名',
  `handle_result` varchar(500) DEFAULT NULL COMMENT '处理结果',
  `status` tinyint DEFAULT 0 COMMENT '状态 0:待处理 1:处理中 2:已处理 3:已忽略',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exception_no` (`exception_no`),
  KEY `idx_source_no` (`source_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常记录表';

-- 18. 盘点记录表
CREATE TABLE IF NOT EXISTS `inventory_check` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `check_no` varchar(50) NOT NULL COMMENT '盘点单号',
  `warehouse_code` varchar(50) DEFAULT NULL COMMENT '仓库编码',
  `warehouse_name` varchar(100) DEFAULT NULL COMMENT '仓库名称',
  `check_type` varchar(50) DEFAULT NULL COMMENT '盘点类型: FULL-全盘, PARTIAL-抽盘',
  `check_date` date DEFAULT NULL COMMENT '盘点日期',
  `checker_id` bigint DEFAULT NULL COMMENT '盘点人ID',
  `checker_name` varchar(50) DEFAULT NULL COMMENT '盘点人姓名',
  `total_sku_count` int DEFAULT 0 COMMENT '盘点SKU总数',
  `abnormal_count` int DEFAULT 0 COMMENT '异常数量',
  `status` tinyint DEFAULT 0 COMMENT '状态 0:待开始 1:盘点中 2:待确认 3:已完成',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_check_no` (`check_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点记录表';

-- 19. 盘点明细表
CREATE TABLE IF NOT EXISTS `inventory_check_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `inventory_check_id` bigint NOT NULL COMMENT '盘点记录ID',
  `check_no` varchar(50) NOT NULL COMMENT '盘点单号',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(50) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(100) NOT NULL COMMENT 'SKU名称',
  `location_id` bigint NOT NULL COMMENT '货位ID',
  `location_code` varchar(50) NOT NULL COMMENT '货位编码',
  `system_stock` int DEFAULT 0 COMMENT '系统库存',
  `actual_stock` int DEFAULT 0 COMMENT '实际库存',
  `diff_qty` int DEFAULT 0 COMMENT '差异数量',
  `batch_no` varchar(50) DEFAULT NULL COMMENT '批次号',
  `is_abnormal` tinyint DEFAULT 0 COMMENT '是否异常 0:否 1:是',
  `handle_remark` varchar(255) DEFAULT NULL COMMENT '处理备注',
  `status` tinyint DEFAULT 0 COMMENT '状态 0:待盘点 1:已盘点 2:已确认',
  `deleted` tinyint DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_inventory_check_id` (`inventory_check_id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点明细表';

-- 20. 出入库统计表
CREATE TABLE IF NOT EXISTS `stock_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(50) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(100) NOT NULL COMMENT 'SKU名称',
  `beginning_stock` int DEFAULT 0 COMMENT '期初库存',
  `in_qty` int DEFAULT 0 COMMENT '入库数量',
  `out_qty` int DEFAULT 0 COMMENT '出库数量',
  `ending_stock` int DEFAULT 0 COMMENT '期末库存',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_sku` (`stat_date`, `sku_id`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出入库统计表';
