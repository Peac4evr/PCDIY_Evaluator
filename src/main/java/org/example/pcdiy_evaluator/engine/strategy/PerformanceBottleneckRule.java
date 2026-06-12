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
                    "性能瓶颈分析", true, "WARN", "缺少 CPU 或 GPU 的基准性能数据，无法进行算力瓶颈诊断。"));
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
        // (注：这里的阈值 1.5 和 0.5 是基于我们 SQL 字典中初始化的跑分设定的经验值)
        if (ratio > 1.8) {
            status = "WARN";
            message = "【显卡过剩 / CPU瓶颈】也就是俗称的“小马拉大车”。您的显卡性能极其强悍，但 CPU 算力较弱，在 1080P/2K 分辨率玩网游时，CPU 会先满载从而限制显卡帧数发挥。";
        } else if (ratio < 0.4) {
            status = "WARN";
            message = "【CPU过剩 / 显卡瓶颈】也就是俗称的“高U低显”。您配置了顶级 CPU，但显卡性能过弱。如果用于 3D 游戏，显卡将成为严重拖累；如果这只是一台只打代码的办公机，那这种配置非常完美。";
        } else {
            message = "算力分配合理。CPU 与显卡性能十分均衡，不存在明显的木桶短板，能够充分发挥整机性能。";
        }

        context.addDiagnosisItem(new EvaluationReportDTO.DiagnosisItem(
                "性能瓶颈分析", true, status, message));

        return true; // 瓶颈诊断不影响点亮，永远返回 true 放行
    }
}