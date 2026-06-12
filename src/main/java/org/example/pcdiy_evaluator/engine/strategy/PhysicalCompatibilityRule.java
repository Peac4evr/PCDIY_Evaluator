package org.example.pcdiy_evaluator.engine.strategy;

import org.example.pcdiy_evaluator.engine.context.EvaluationContext;
import org.example.pcdiy_evaluator.model.bo.CpuBO;
import org.example.pcdiy_evaluator.model.bo.MotherboardBO;
import org.example.pcdiy_evaluator.model.bo.MemoryBO;
import org.example.pcdiy_evaluator.model.dto.EvaluationReportDTO;

/**
 * 核心策略一：物理与协议兼容性深度校验
 */
public class PhysicalCompatibilityRule implements EvaluationRule {

    @Override
    public boolean evaluate(EvaluationContext context) {
        CpuBO cpu = (CpuBO) context.getHardware("CPU");
        MotherboardBO mb = (MotherboardBO) context.getHardware("MOTHERBOARD");
        MemoryBO memory = (MemoryBO) context.getHardware("MEMORY");

        // 0. 前置基础校验
        if (cpu == null || mb == null) {
            context.addDiagnosisItem(new EvaluationReportDTO.DiagnosisItem(
                    "物理兼容性校验", false, "FAILED", "装机单缺失核心配件 (CPU 或 主板)，无法完成组装。"));
            return false;
        }

        // 1. 校验 CPU 物理插槽与主板插槽是否匹配 (例如 LGA1700)
        if (!cpu.getSocket().equalsIgnoreCase(mb.getSocket())) {
            context.addDiagnosisItem(new EvaluationReportDTO.DiagnosisItem(
                    "物理兼容性校验", false, "FAILED",
                    String.format("【严重不兼容】插槽不匹配！CPU 需要 %s 接口，但主板是 %s。", cpu.getSocket(), mb.getSocket())));
            return false;
        }

        // 2. 校验 内存代数 是否匹配 (DDR4 vs DDR5 物理防呆口不同，绝对不可混插)
        if (memory != null && mb.getMemoryType() != null && !mb.getMemoryType().isEmpty()) {
            if (!mb.getMemoryType().equalsIgnoreCase(memory.getMemoryType())) {
                context.addDiagnosisItem(new EvaluationReportDTO.DiagnosisItem(
                        "物理兼容性校验", false, "FAILED",
                        String.format("【严重不兼容】内存代数冲突！主板仅支持 %s，但您选择了 %s 内存。", mb.getMemoryType(), memory.getMemoryType())));
                return false;
            }
        }

        // 3. 高级校验：BIOS 版本是否足以点亮当前 CPU
        // (注：简单的字符串比对在实战中适用 v1.0 格式，复杂的可以用专门的 Version 类解析)
        if (cpu.getRequiredBiosVersion() != null && !cpu.getRequiredBiosVersion().isEmpty()
                && mb.getCurrentBiosVersion() != null && !mb.getCurrentBiosVersion().isEmpty()) {
            if (cpu.getRequiredBiosVersion().compareToIgnoreCase(mb.getCurrentBiosVersion()) > 0) {
                context.addDiagnosisItem(new EvaluationReportDTO.DiagnosisItem(
                        "物理兼容性校验", false, "FAILED",
                        String.format("【无法点亮】主板当前出厂 BIOS (%s) 版本过低，无法识别该 CPU。请确认商家是否可以代刷至 (%s) 或以上版本，否则开机黑屏。", mb.getCurrentBiosVersion(), cpu.getRequiredBiosVersion())));
                return false;
            }
        }

        // 全部物理与协议规则均通过
        context.addDiagnosisItem(new EvaluationReportDTO.DiagnosisItem(
                "物理兼容性校验", true, "PASSED", "CPU插槽、内存代数与主板BIOS版本完美匹配，不存在物理冲突。"));
        return true;
    }
}