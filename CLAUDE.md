# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

禁止你编译运行测试，你只负责写代码，由我负责测试代码的正确性。

- Java 21, Spring Boot 4.1.0, MyBatis 4.0.1
- 数据库: MySQL (`pcdiy_evaluator`)，连接配置在 `application.yaml`
- 测试：JUnit 5 + Spring Boot Test + MyBatis Test

## 代码架构

这是一个 **PC DIY 智能装机配置评估引擎**，围绕四种设计模式有机组合：

### 请求流

```
EvaluationController (/api/v1/diy/evaluate)
  → EvaluationServiceImpl (extends AbstractEvaluationEngine)
    → 1. buildContext() — 加载数据
        → HardwareComponentMapper.selectBatchIds() — 批量查 DB
        → HardwareFactory.create() — 简单工厂：Entity → 多态 BO
        → EvaluationContext — 存放所有中间状态
    → 2. runRulesChain() — 执行规则
        → EvaluationRuleChain (责任链)
            → PhysicalCompatibilityRule (物理兼容 — 一票否决)
            → PowerSupplyEvaluationRule (供电评估 — 一票否决)
            → PerformanceBottleneckRule (性能瓶颈 — 不阻断)
    → 3. generateReport() — 生成报告
```

### 设计模式分布

| 模式 | 位置 | 作用 |
|---|---|---|
| **模板方法** | `AbstractEvaluationEngine` | 定义评估骨架，子类实现 buildContext / runRulesChain |
| **责任链** | `EvaluationRuleChain` + `EvaluationRule` | 规则按序执行，支持一票否决短路 |
| **策略** | `EvaluationRule` 接口的三个实现类 | 每条规则独立可替换 |
| **简单工厂** | `HardwareFactory.create()` | 根据 type 字段创建对应的 BO 子类 |

### 核心数据结构

- **Entity 层** (`HardwareComponent`): 数据库映射，通用字段 + `attributes` JSON 扩展属性
- **BO 层** (`BaseHardwareBO` + 子类): 业务对象，解析 JSON 属性为具体字段
  - `CpuBO`: socket, supportMemory, requiredBiosVersion
  - `MotherboardBO`: socket, memoryType, currentBiosVersion
  - `MemoryBO`: memoryType, capacity
  - `GpuBO`: lengthMm, powerConnector
- **DTO 层**: `EvaluationRequestDTO` (入参) / `EvaluationReportDTO` (出参)
  - `DiagnosisItem`: ruleName, success, status (PASSED/WARN/FAILED/SKIPPED), message
- **Context**: `EvaluationContext` — 在责任链各节点间传递状态，收集诊断明细

### 规则执行逻辑

- 每条规则 `evaluate()` 返回 `boolean`
  - `false` = 一票否决，责任链截断，全局 `passed = false`
  - `true` = 继续执行下一条规则
- 诊断结果通过 `context.addDiagnosisItem()` 收集，最终由 `generateReport()` 组装

### 数据库

- `hardware_component` 表：id, name, type, brand, tdp, power_supply, performance_score, attributes (JSON)
- MyBatis 注解 SQL，无 XML 映射
- 关键技巧：批量查询 `selectBatchIds()` 一次 IO 查完所有配件
