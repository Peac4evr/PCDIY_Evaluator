package org.example.pcdiy_evaluator.model.entity;

import java.time.LocalDateTime;

public class HardwareComponent {
    private Long id;
    private String name;
    private String type; // CPU, MOTHERBOARD, MEMORY, GPU, PSU
    private String brand;
    private Integer tdp; // 标称功耗(W)
    private Integer powerSupply; // 提供电量(W) - 仅电源有效
    private Integer performanceScore; // 性能基准跑分
    private String attributes; // 扩展属性，保存为JSON字符串
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 无参构造函数
    public HardwareComponent() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Integer getTdp() { return tdp; }
    public void setTdp(Integer tdp) { this.tdp = tdp; }

    public Integer getPowerSupply() { return powerSupply; }
    public void setPowerSupply(Integer powerSupply) { this.powerSupply = powerSupply; }

    public Integer getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Integer performanceScore) { this.performanceScore = performanceScore; }

    public String getAttributes() { return attributes; }
    public void setAttributes(String attributes) { this.attributes = attributes; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}