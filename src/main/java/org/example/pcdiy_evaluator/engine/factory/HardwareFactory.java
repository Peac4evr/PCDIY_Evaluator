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
            case "MEMORY":
                bo = new MemoryBO();
                break;
            case "GPU":
                bo = new GpuBO();
                break;
            case "PSU":
                bo = new PsuBO();
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