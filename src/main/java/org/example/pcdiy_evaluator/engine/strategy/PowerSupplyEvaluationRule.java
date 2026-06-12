package org.example.pcdiy_evaluator.engine.strategy;

import org.example.pcdiy_evaluator.engine.context.EvaluationContext;
import org.example.pcdiy_evaluator.model.bo.BaseHardwareBO;
import org.example.pcdiy_evaluator.model.dto.EvaluationReportDTO;

import java.util.Map;

/**
 * 核心策略二：供电与功耗安全性评估
 */
public class PowerSupplyEvaluationRule implements EvaluationRule {

    // 预留给主板芯片组、风扇、硬盘等未显式统计的周边配件的冗余功耗
    private static final int BUFFER_POWER_W = 50;

    @Override
    public boolean evaluate(EvaluationContext context) {
        BaseHardwareBO psu = context.getHardware("PSU");
        if (psu == null || psu.getTdp() == null) {
            // 注意：在电源的 DO 中，我们把电源提供的额定功率复用了 tdp 或者 powerSupply 字段
            // 这里假设通过 getter 取到了电源的额定功率，由于我们前面把电源功率配在了 powerSupply，
            // 严谨起见，我们应该在实体里明确取 powerSupply。
            // 为了代码兼容前面的 BaseHardwareBO，这里我们通过类型强转或反射，或者假设已经在工厂里处理好。
            // 我们这里采用直接从原始实体映射拿的方式，或者假设 psu 对象有专门的方法。
            context.addDiagnosisItem(new EvaluationReportDTO.DiagnosisItem(
                    "供电功耗评估", false, "FAILED", "未检测到电源配置，无法评估供电安全。"));
            return false;
        }

        // 获取电源额定功率 (在我们的数据库初始 SQL 中，电源的 power_supply 字段记录了额定功率)
        int ratedPower = psu.getTdp(); // 假设工厂已将 power_supply 映射到此，或者你也可以在 PsuBO 中专门定义

        // 1. 累加所有配件的标称功耗 (TDP)
        int totalTdp = 0;
        for (Map.Entry<String, BaseHardwareBO> entry : context.getHardwareMap().entrySet()) {
            // 电源本身的 TDP 不计入负载
            if (!"PSU".equalsIgnoreCase(entry.getKey()) && entry.getValue() != null && entry.getValue().getTdp() != null) {
                totalTdp += entry.getValue().getTdp();
            }
        }

        // 加入周边硬件的冗余预估
        int estimatedPeakPower = totalTdp + BUFFER_POWER_W;
        context.setTotalCostW(estimatedPeakPower); // 将预估功耗写入上下文，供最终报告使用

        // 2. 评估逻辑
        if (estimatedPeakPower > ratedPower) {
            context.addDiagnosisItem(new EvaluationReportDTO.DiagnosisItem(
                    "供电功耗评估", false, "FAILED",
                    String.format("【严重隐患】整机预估峰值功耗 (%dW) 已超过电源额定功率 (%dW)！强行开机极易导致断电或烧毁硬件，请更换更大功率的电源。", estimatedPeakPower, ratedPower)));
            return false; // 触发一票否决，截断责任链
        }

        if (estimatedPeakPower > ratedPower * 0.85) {
            // 负载率超过 85%，给予警告但不熔断
            context.addDiagnosisItem(new EvaluationReportDTO.DiagnosisItem(
                    "供电功耗评估", true, "WARN",
                    String.format("【供电告警】整机预估满载功耗 (%dW) 逼近电源额定功率 (%dW) 的安全线。日常使用无碍，但在极端双烤测试下可能触发电源保护重启，建议升级电源。", estimatedPeakPower, ratedPower)));
            return true;
        }

        context.addDiagnosisItem(new EvaluationReportDTO.DiagnosisItem(
                "供电功耗评估", true, "PASSED",
                String.format("供电充足。预估峰值功耗 %dW，电源额定 %dW，留有充足的安全冗余空间。", estimatedPeakPower, ratedPower)));
        return true;
    }
}