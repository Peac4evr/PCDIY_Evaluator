# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

禁止你编译运行测试，你只负责写代码，由我负责测试代码的正确性。

---

## 项目概览

**PC DIY 智能装机配置评估引擎** — 用户选择 CPU/主板/内存/显卡/电源五个核心配件，引擎自动校验物理兼容性、供电安全与性能均衡度，生成专业级诊断报告。

### 技术栈

| 层级 | 技术 |
|------|------|
| **后端** | Java 21, Spring Boot 4.1.0, MyBatis 4.0.1 |
| **数据库** | MySQL 8.x (`pcdiy_evaluator`) |
| **前端** | Vue 3 (Composition API) + TypeScript + Vite + Pinia + Vue Router |
| **测试** | JUnit 5 + Spring Boot Test + MyBatis Test |

### 项目目录结构

```
E:\软件模式与体系结构\
├── pcdiy_evaluator/          ← 后端 Spring Boot 项目（git: master 分支）
│   ├── src/main/java/.../engine/
│   │   ├── template/         ← 模板方法模式
│   │   ├── chain/            ← 责任链模式
│   │   ├── strategy/         ← 策略模式（三条规则）
│   │   ├── factory/          ← 简单工厂模式
│   │   └── context/          ← 评估上下文
│   ├── src/main/java/.../model/
│   │   ├── entity/           ← 数据库实体
│   │   ├── bo/               ← 业务对象（多态）
│   │   └── dto/              ← 数据传输对象
│   ├── src/main/java/.../service/
│   ├── src/main/java/.../controller/
│   ├── src/main/java/.../mapper/
│   └── src/main/resources/
│       └── application.yaml
│
└── frontend/                  ← 前端 Vue 3 项目（git: frontend 分支）
    └── src/
        ├── views/             ← 4 个页面视图
        ├── components/        ← 6 个可复用组件
        ├── stores/            ← Pinia 状态管理
        ├── api/               ← 后端 API 封装
        ├── types/             ← TypeScript 类型定义
        └── router/            ← Vue Router 配置
```

### Git 仓库

前后端共用同一 GitHub 仓库 `Peac4evr/PCDIY_Evaluator`，不同分支：
- **`master`** — 后端 Java 项目（默认分支）
- **`frontend`** — 前端 Vue 3 项目

操作前端需 `cd E:\软件模式与体系结构\frontend`，提交在 `frontend` 分支上。

---

## 后端架构详解

### 请求处理流程

```
EvaluationController (/api/v1/diy)
  ├── POST /evaluate        → 装机评估
  │     → EvaluationServiceImpl.executeEvaluation()
  │       ├── 1. buildContext()     加载数据
  │       │   ├── HardwareComponentMapper.selectBatchIds()  批量查 DB
  │       │   ├── HardwareFactory.create()                  工厂: Entity → BO
  │       │   └── context.putHardware(type, bo)             放入上下文
  │       ├── 2. runRulesChain()    执行规则链
  │       │   └── EvaluationRuleChain.execute()
  │       │       ├── PhysicalCompatibilityRule.evaluate()    一票否决
  │       │       ├── PowerSupplyEvaluationRule.evaluate()    一票否决
  │       │       └── PerformanceBottleneckRule.evaluate()    永不阻断
  │       └── 3. generateReport()   组装报告
  │             └── new EvaluationReportDTO() from context
  │
  └── GET /hardware?type=TYPE  → 硬件列表查询
        → HardwareComponentMapper.selectByType()
        → List<HardwareItemDTO> (含 specs 规格摘要)
```

### 四大设计模式

| 模式 | 核心类 | 职责 |
|------|--------|------|
| **模板方法** | `AbstractEvaluationEngine` | `final executeEvaluation()` 固定三步骨架，子类实现 `buildContext()` / `runRulesChain()` |
| **责任链** | `EvaluationRuleChain` + `EvaluationRule` 接口 | 按序执行规则，`false` 立即短路（一票否决） |
| **策略** | `PhysicalCompatibilityRule` / `PowerSupplyEvaluationRule` / `PerformanceBottleneckRule` | 每条规则独立封装，可替换 |
| **简单工厂** | `HardwareFactory.create(entity)` | 根据 `entity.type` 创建对应的 BO 子类 |

### 三条评估规则详细逻辑

#### 1. PhysicalCompatibilityRule（物理兼容 — 一票否决）

| 校验项 | 判断逻辑 | 数据来源 |
|--------|----------|----------|
| CPU 插槽 vs 主板插槽 | `cpu.socket.equalsIgnoreCase(mb.socket)` | `attributes` JSON: `socket` |
| 内存代数 vs 主板支持 | `mb.memoryType.equalsIgnoreCase(memory.memoryType)` | `attributes` JSON: `memory_type` |
| BIOS 版本 | `cpu.requiredBiosVersion.compareToIgnoreCase(mb.currentBiosVersion) > 0` → 失败 | `attributes` JSON: `required_bios_version` / `current_bios_version` |

- CPU 和主板**缺失任一即 FAILED**
- 内存为 null 时跳过内存校验
- 注：BIOS 版本比对当前使用字典序（`compareToIgnoreCase`），对 "1.9" vs "1.10" 这种场景可能误判

#### 2. PowerSupplyEvaluationRule（供电评估 — 一票否决）

| 校验项 | 判断逻辑 |
|--------|----------|
| 总功耗累加 | 遍历 `context.hardwareMap`，累加所有**非 PSU** 配件的 `tdp` + 50W 固定冗余 |
| `estimatedPeakPower > ratedPower` | **FAILED** — 截断责任链 |
| `estimatedPeakPower > ratedPower * 0.85` | **WARN** — 不阻断 |
| 其他 | PASSED |

- PSU 的额定功率取值：`PsuBO.loadFromEntity()` 中优先用 `entity.getPowerSupply()` 覆盖 `this.tdp`
- ⚠️ **关键**：依赖 MyBatis `map-underscore-to-camel-case: true` 才能正确映射 `power_supply` 列

#### 3. PerformanceBottleneckRule（性能瓶颈 — 永不阻断）

| 校验项 | 判断逻辑 |
|--------|----------|
| `gpuScore / cpuScore > 1.8` | WARN: 显卡过剩 / CPU 瓶颈 |
| `gpuScore / cpuScore < 0.4` | WARN: CPU 过剩 / 显卡瓶颈 |
| `0.4 ≤ ratio ≤ 1.8` | PASSED: 算力均衡 |
| CPU/GPU 缺失跑分 | WARN: 无法诊断 |

- 始终返回 `true`，不阻断（瓶颈不影响开机）
- ⚠️ 该规则依赖 `performance_score` 列，同样需要驼峰映射

### 数据模型层次

```
Entity (HardwareComponent)      ← 数据库映射
  ↓ HardwareFactory.create()
BO (BaseHardwareBO → 子类)      ← 多态业务对象，解析 attributes JSON
  ↓ 放入 EvaluationContext
规则读取 BO.getXxx()            ← 规则层只依赖 BO 的 getter
  ↓ context.addDiagnosisItem()
DTO (EvaluationReportDTO)       ← 响应给前端
```

### BO 类及其 attributes JSON 字段

| BO 类 | type 值 | JSON 字段 | Getter |
|-------|---------|-----------|--------|
| `CpuBO` | CPU | `socket`, `support_memory[]`, `required_bios_version` | `getSocket()`, `getSupportMemory()`, `getRequiredBiosVersion()` |
| `MotherboardBO` | MOTHERBOARD | `socket`, `memory_type`, `current_bios_version` | `getSocket()`, `getMemoryType()`, `getCurrentBiosVersion()` |
| `MemoryBO` | MEMORY | `memory_type`, `capacity` | `getMemoryType()`, `getCapacity()` |
| `GpuBO` | GPU | `length_mm`, `power_connector` | `getLengthMm()`, `getPowerConnector()` |
| `PsuBO` | PSU | `rated_power`, `form_factor`, `power_connector` | `getRatedPower()`, `getFormFactor()`, `getPowerConnector()` |

> GPU 的 `lengthMm` / `powerConnector` 和 PSU 的 `formFactor` / `powerConnector` 已解析但**当前规则未使用**，预留扩展。

### 数据库

**`hardware_component` 表：**

| 列名 | Java 字段 | 说明 |
|------|-----------|------|
| `id` | `id` | 主键 |
| `name` | `name` | 硬件名称 |
| `type` | `type` | CPU / MOTHERBOARD / MEMORY / GPU / PSU |
| `brand` | `brand` | 品牌 |
| `tdp` | `tdp` | 功耗(W) — 所有硬件都应有此值 |
| `power_supply` | `powerSupply` | 电源额定功率(W) — **仅 PSU 有效** |
| `performance_score` | `performanceScore` | 性能跑分 — CPU/GPU |
| `attributes` | `attributes` | JSON 扩展属性 |
| `create_time` | `createTime` | 创建时间 |
| `update_time` | `updateTime` | 更新时间 |

**⚠️ 关键配置**：`application.yaml` 中必须开启 `map-underscore-to-camel-case: true`，否则 `power_supply`、`performance_score`、`create_time`、`update_time` 等列无法映射。

### API 接口

| 方法 | 路径 | 入参 | 出参 |
|------|------|------|------|
| POST | `/api/v1/diy/evaluate` | `EvaluationRequestDTO` (cpuId, motherboardId, memoryId, gpuId, psuId — 均可为 null) | `EvaluationReportDTO` (passed, totalHardwareCostW, bottleneckAnalysis, details[]) |
| GET | `/api/v1/diy/hardware?type=CPU` | type (可选) | `List<HardwareItemDTO>` (id, name, brand, type, tdp, performanceScore, specs) |

> 所有 API 均允许跨域 `@CrossOrigin(origins = "*")`

---

## 前端架构详解

### 技术栈

- Vue 3 (Composition API + `<script setup>`)
- TypeScript
- Vite (开发服务器 + 代理)
- Pinia (状态管理)
- Vue Router (页面路由，HTML5 History 模式)
- 纯 CSS 设计系统（设计令牌 + 自定义属性）

### 页面路由

| 路径 | 路由名 | 视图 | 功能 |
|------|--------|------|------|
| `/` | `home` | `HomeView` | 首页：Hero 品牌区 + 三大功能卡片 + CTA |
| `/hardware` | `hardware` | `HardwareLibraryView` | 硬件库：类型筛选 + 搜索 + 卡片网格 |
| `/build` | `build` | `BuildView` | 装机评估：5 个硬件选择器 + 提交按钮 |
| `/result` | `result` | `ResultView` | 评估报告：结果横幅 + 诊断详情列表 |
| `/evaluate` | — | → 重定向到 `/build` | 旧路由兼容 |

全部使用懒加载 `() => import(...)`，带 `page` 过渡动画。

### 组件树

```
App.vue
├── AppNavbar          ← 固定顶栏，毛玻璃背景，居中布局
└── <RouterView>
    ├── HomeView           ← 静态页面，无 API 调用
    │     └── FeatureCard ×3
    ├── HardwareLibraryView ← 调用 store.fetchAllHardware()
    │     └── HardwareCard ×N
    ├── BuildView           ← 核心工作流页
    │     ├── HardwareSelector ×5  ← 带搜索的下拉选择器
    │     ├── 错误条
    │     └── 空状态 SVG 插图
    └── ResultView          ← 守卫: 无 report 则重定向
          ├── ResultBanner
          └── DiagnosisCard ×N
```

### 数据流

```
store.fetchAllHardware()  →  GET /hardware?type=CPU ×5  →  hardwareLists
                                                             ↓
BuildView: store.updateField() ←→ HardwareSelector v-model   ↓ 切换页面不丢失
BuildView: store.submit()      →  POST /evaluate  →  report + hasEvaluated=true
                                                       ↓ watch → router.push('/result')
ResultView: 读取 store.report / store.passedCount / store.failedItems / store.warnItems
ResultView: store.resetForm()  →  router.push('/build')
```

- Pinia store 跨路由存活，report 通过 store 传递
- ResultView 守卫：若 `!store.report` → `router.replace('/build')`
- 所有 Vite `/api` 请求代理到 `http://localhost:8080`

### 设计系统令牌

| 类别 | 主要变量 |
|------|----------|
| 背景 | `--bg-root: #f5f6f8`, `--bg-elevated: #ffffff` |
| 主色 | `--accent: #0066ff` |
| 状态色 | PASSED `#00a86b` / WARN `#e67700` / FAILED `#e03131` / SKIPPED `#868e96` |
| 文字 | `--text-primary: #1a1a2e`, `--text-secondary: #5c5c7a`, `--text-muted: #8e8ea8` |
| 字体 | Inter (正文), JetBrains Mono (等宽/品牌) |
| 阴影 | `--shadow-card`, `--shadow-elevated`, `--shadow-glow` |

### 前端旧文件

`src/views/EvaluateView.vue` 已被拆分为 BuildView + ResultView + HomeView，不再被路由引用。可删除但非必须。

---

## 开发注意事项

### 新增硬件类型

如果向数据库添加第六种硬件类型（如硬盘 STORAGE、机箱 CASE），需要修改两处：
1. **后端**：`HardwareFactory.java` 的 switch-case 中添加新类型 → 对应 BO 类 → 解析 attributes JSON
2. **前端**：`types/evaluation.ts` 中扩展 `HardwareType` 和 `HARDWARE_TYPE_MAP`

### 新增评估规则

1. 在 `engine/strategy/` 下新建实现 `EvaluationRule` 接口的类
2. 在 `EvaluationRuleChain` 构造函数中 `chain.add()` 插入到合适位置
3. 若需一票否决 → `return false`；仅诊断不阻断 → `return true`

### 前端页面新增

1. `src/views/` 下新建 Vue 文件
2. `src/router/index.ts` 添加路由（建议懒加载）
3. 在 `AppNavbar.vue` 的 `navbar-links` 中添加对应的 `<router-link>`

### CSS 规范

- 所有新组件样式使用 `scoped`，通过 `var(--xxx)` 引用设计令牌
- 不要私自定义硬编码颜色/间距值
- 新动画使用已有 keyframes（`fade-up`, `fade-in`, `scale-in`, `glow-pulse`, `shimmer`, `spin`）

---

## 网络代理

当遇到 GitHub 等外部网络连接超时时，走本地代理 `127.0.0.1:7897`，操作完成后**立即删除**代理配置：

```bash
# 临时添加代理
git config --global http.proxy http://127.0.0.1:7897
git config --global https.proxy http://127.0.0.1:7897

# 执行网络操作（如 git push / git pull / curl 等）

# 操作完成后立即删除
git config --global --unset http.proxy
git config --global --unset https.proxy
```
