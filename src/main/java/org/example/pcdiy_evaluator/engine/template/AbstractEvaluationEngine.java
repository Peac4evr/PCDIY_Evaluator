package org.example.pcdiy_evaluator.engine.template;

import org.example.pcdiy_evaluator.model.dto.EvaluationReportDTO;
import org.example.pcdiy_evaluator.model.dto.EvaluationRequestDTO;
import org.example.pcdiy_evaluator.engine.context.EvaluationContext;

/**
 * 模板方法模式：定义评估引擎的核心骨架
 */
public abstract class AbstractEvaluationEngine {

    /**
     * 模板方法，使用 final 修饰，严禁子类篡改核心执行流程！
     */
    public final EvaluationReportDTO executeEvaluation(EvaluationRequestDTO request) {
        // 1. 初始化上下文环境 (从 DB 加载数据，通过工厂组装成 BO)
        EvaluationContext context = buildContext(request);

        // 2. 执行核心评估规则链 (包含物理兼容、功耗、瓶颈)
        runRulesChain(context);

        // 3. 构建并返回最终的诊断报告
        return generateReport(context);
    }

    /**
     * 步骤 1：构建上下文环境，留给具体业务类实现
     */
    protected abstract EvaluationContext buildContext(EvaluationRequestDTO request);

    /**
     * 步骤 2：执行规则链条，留给具体业务类实现
     */
    protected abstract void runRulesChain(EvaluationContext context);

    /**
     * 步骤 3：生成最终报告，由于逻辑通用，可以直接在父类实现
     */
    protected EvaluationReportDTO generateReport(EvaluationContext context) {
        // 这里只是个架子，最终报告的数据会从 context 中提取
        EvaluationReportDTO report = new EvaluationReportDTO();
        report.setPassed(context.isPassed());
        report.setTotalHardwareCostW(context.getTotalCostW());
        report.setDetails(context.getDiagnosisItems());
        return report;
    }
}