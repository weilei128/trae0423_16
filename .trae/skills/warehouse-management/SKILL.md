---
name: "warehouse-management"
description: "仓储物流配送管理系统开发技能. 当用户需要开发仓储物流相关功能、处理库存扣减、货位缓存、出入库管理、配送追踪等业务时调用此技能."
---

# 仓储物流配送管理系统开发技能

## 项目概述
基于 Spring Boot 3.x + Vue 3 + MySQL + Redis + MyBatis-Plus 的仓储物流配送管理系统

### 技术要点
1. **库存扣减**: 使用数据库悲观锁 (`SELECT ... FOR UPDATE`) 保证数据一致性
2. **货位状态**: 使用 Redis 缓存提升查询性能
3. **前后端分离**: 后端端口 10026, 前端端口 10016

### 数据库配置
- MySQL: 49.235.161.106:10011, 用户名 wl, 密码 Wl1733811, 数据库 db1
- Redis: 49.235.161.106:10012, 无密码

## 核心功能模块

### 1. 货品档案模块
- SKU 管理
- 规格参数管理
- 供应商绑定

### 2. 入库管理模块
- 采购入库单
- 验货确认
- 上架至货位
- 更新库存 (使用悲观锁)

### 3. 出库管理模块
- 出库单生成
- 拣货任务分配
- 复核装箱
- 库存扣减 (使用悲观锁)

### 4. 库存管理模块
- 实时库存查询
- 货位管理 (Redis 缓存)
- 预警阈值设置
- 定期盘点

### 5. 配送管理模块
- 运单生成
- 承运商配置
- 配送状态追踪

### 6. 退货管理模块
- 退货入库
- 质检结果
- 异常上报

### 7. 报表中心
- 库存周转率
- 出入库统计
- 承运商对账

## 项目结构

### 后端 (Spring Boot)
```
backend/
├── src/main/java/com/warehouse/
│   ├── controller/          # 控制器
│   ├── service/             # 业务逻辑
│   │   └── impl/
│   ├── mapper/              # MyBatis-Plus Mapper
│   ├── entity/              # 实体类
│   ├── dto/                 # 数据传输对象
│   ├── config/              # 配置类
│   ├── common/              # 通用类 (响应、异常等)
│   └── util/                # 工具类
├── src/main/resources/
│   ├── application.yml      # 配置文件
│   └── mapper/              # XML Mapper
└── pom.xml
```

### 前端 (Vue 3)
```
frontend/
├── src/
│   ├── api/                 # API 接口
│   ├── views/               # 页面视图
│   ├── components/          # 通用组件
│   ├── router/              # 路由配置
│   ├── store/               # 状态管理
│   ├── utils/               # 工具函数
│   └── assets/              # 静态资源
├── package.json
└── vite.config.js
```

## 开发规范

### 后端规范
1. 使用 MyBatis-Plus 进行 CRUD 操作
2. 库存扣减使用悲观锁: `@Transactional` + `SELECT ... FOR UPDATE`
3. 货位信息使用 Redis 缓存, Key 格式: `warehouse:location:{locationCode}`
4. 统一响应格式: `Result<T>`
5. 统一异常处理: `@RestControllerAdvice`

### 前端规范
1. 使用 Vue 3 Composition API
2. 使用 Element Plus UI 组件库
3. 使用 Axios 进行 HTTP 请求
4. 统一错误处理和 Loading 状态

## 关键实现要点

### 库存扣减 (悲观锁)
```java
@Transactional
public void deductStock(Long skuId, Integer quantity) {
    // 使用悲观锁查询库存
    Inventory inventory = inventoryMapper.selectOne(
        new QueryWrapper<Inventory>().eq("sku_id", skuId).last("FOR UPDATE")
    );
    
    if (inventory == null || inventory.getStock() < quantity) {
        throw new BusinessException("库存不足");
    }
    
    inventory.setStock(inventory.getStock() - quantity);
    inventoryMapper.updateById(inventory);
}
```

### 货位缓存 (Redis)
```java
@Service
public class LocationService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String LOCATION_KEY = "warehouse:location:";
    
    public Location getLocation(String locationCode) {
        String key = LOCATION_KEY + locationCode;
        // 先查缓存
        Location location = (Location) redisTemplate.opsForValue().get(key);
        if (location == null) {
            // 查数据库
            location = locationMapper.selectByCode(locationCode);
            if (location != null) {
                // 写入缓存
                redisTemplate.opsForValue().set(key, location, 1, TimeUnit.HOURS);
            }
        }
        return location;
    }
}
```

## 开发顺序
1. 创建后端项目结构和基础配置
2. 设计并创建数据库表
3. 实现核心实体和 Mapper
4. 实现库存扣减和货位缓存核心逻辑
5. 逐个实现功能模块
6. 创建前端项目
7. 实现前端页面和交互
8. 联调测试
