package org.example.pcdiy_evaluator.model.bo;

import org.example.pcdiy_evaluator.model.entity.HardwareComponent;

/**
 * 硬件业务对象基类
 */
public abstract class BaseHardwareBO {
    protected Long id;
    protected String name;
    protected String brand;
    protected Integer tdp;
    protected Integer performanceScore;

    // 接收实体类，进行通用属性赋值
    public void loadFromEntity(HardwareComponent entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.brand = entity.getBrand();
        this.tdp = entity.getTdp();
        this.performanceScore = entity.getPerformanceScore();
    }

    // 抽象方法：强制子类实现自己特有 JSON 属性的解析逻辑
    public abstract void parseAttributes(String attributesJson) throws Exception;

    // --- 补全所有基础属性的 Getters，解决编译报错 ---
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public Integer getTdp() {
        return tdp;
    }

    public Integer getPerformanceScore() {
        return performanceScore;
    }
}