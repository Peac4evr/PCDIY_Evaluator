package org.example.pcdiy_evaluator.engine.strategy;

import org.example.pcdiy_evaluator.engine.context.EvaluationContext;
import org.example.pcdiy_evaluator.model.bo.BaseHardwareBO;
import org.example.pcdiy_evaluator.model.dto.EvaluationReportDTO;

/**
 * 核心策略三：性能瓶颈与算力均衡度分析 (木桶效应)
 */
public class PerformanceBottleneckRule implements EvaluationRule {

    @Override
    public boolean evaluate(EvaluationContext context) {
        BaseHardwareBO cpu = context.getHardware("CPU");
        BaseHardwareBO gpu = context.getHardware("GPU");

        if (cpu == null || gpu == null || cpu.getPerformanceScore() == null || gpu.getPerformanceScore() == null) {
            context.addDiagnosisItem(new EvaluationReportDTO.DiagnosisItem(
                    "性能瓶颈分析", true, "WARN",
                    "缺少 CPU 或 GPU 的基准性能数据，无法进行算力瓶颈诊断。",
                    buildRelatedParts(cpu, gpu)));
            return true;
        }

        int cpuScore = cpu.getPerformanceScore();
        int gpuScore = gpu.getPerformanceScore();

        // 计算算力比值 (为了避免除数为 0，做个保护)
        if (cpuScore == 0) cpuScore = 1;
        double ratio = (double) gpuScore / cpuScore;

        String message;
        String status = "PASSED";

        // 业务逻辑：判断高U低显 vs 小马拉大车
        // (注：这里的阈值 1.8 和 0.4 是基于我们 SQL 字典中初始化的跑分设定的经验值)
        if (ratio > 1.8) {
            status = "WARN";
            message = String.format("【显卡过剩 / CPU瓶颈】，"
                    + "显卡「%s」(跑分 %d) 性能极其强悍，但 CPU「%s」(跑分 %d) 算力较弱 (GPU/CPU比值=%.1f)，"
                    + "在 1080P/2K 分辨率玩网游时，CPU 会先满载从而限制显卡帧数发挥。",
                    gpu.getName(), gpuScore, cpu.getName(), cpuScore, ratio);
        } else if (ratio < 0.4) {
            status = "WARN";
            message = String.format("【CPU过剩 / 显卡瓶颈】，"
                    + "CPU「%s」(跑分 %d) 性能远超显卡「%s」(跑分 %d) (GPU/CPU比值=%.2f)。"
                    + "如果用于 3D 游戏，显卡将成为严重拖累；如果这只是办公/编译用机，那这种配置非常合理。",
                    cpu.getName(), cpuScore, gpu.getName(), gpuScore, ratio);
        } else {
            message = String.format("算力分配合理。CPU「%s」(跑分 %d) 与显卡「%s」(跑分 %d) 性能均衡 (比值 %.2f)，"
                    + "不存在明显的木桶短板，能够充分发挥整机性能。",
                    cpu.getName(), cpuScore, gpu.getName(), gpuScore, ratio);
        }

        context.addDiagnosisItem(new EvaluationReportDTO.DiagnosisItem(
                "性能瓶颈分析", true, status, message,
                buildRelatedParts(cpu, gpu)));

        return true; // 瓶颈诊断不影响点亮，永远返回 true 放行
    }

    /**
     * 收集涉及的硬件名称列表
     */
    private java.util.List<String> buildRelatedParts(BaseHardwareBO cpu, BaseHardwareBO gpu) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        if (cpu != null) parts.add(cpu.getName());
        if (gpu != null) parts.add(gpu.getName());
        return parts;
    }
}