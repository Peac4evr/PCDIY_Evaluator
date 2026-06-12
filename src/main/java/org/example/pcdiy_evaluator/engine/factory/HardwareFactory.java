package org.example.pcdiy_evaluator.engine.factory;

import org.example.pcdiy_evaluator.model.bo.*;
import org.example.pcdiy_evaluator.model.entity.HardwareComponent;

public class HardwareFactory {

    public static BaseHardwareBO create(HardwareComponent entity) {
        if (entity == null || entity.getType() == null) {
            return null;
        }

        BaseHardwareBO bo;
        switch (entity.getType().toUpperCase()) {
            case "CPU":
                bo = new CpuBO();
                break;
            case "MOTHERBOARD":
                bo = new MotherboardBO();
                break;
            // 提示：为了篇幅，MemoryBO, GpuBO, PsuBO 的解析逻辑类似，在此省略具体类
            case "MEMORY":
            case "GPU":
            case "PSU":
                bo = new BaseHardwareBO() { // 匿名内部类占位，实际开发中请建立对应的 BO 类
                    @Override
                    public void parseAttributes(String attributesJson) {}
                };
                break;
            default:
                throw new IllegalArgumentException("未知的硬件类型: " + entity.getType());
        }

        // 装载通用属性并解析专属 JSON
        bo.loadFromEntity(entity);
        try {
            bo.parseAttributes(entity.getAttributes());
        } catch (Exception e) {
            throw new RuntimeException("解析硬件扩展属性失败，ID: " + entity.getId(), e);
        }

        return bo;
    }
}