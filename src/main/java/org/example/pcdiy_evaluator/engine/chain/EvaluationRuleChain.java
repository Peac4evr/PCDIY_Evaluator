package org.example.pcdiy_evaluator.engine.chain;

import org.example.pcdiy_evaluator.engine.context.EvaluationContext;
import org.example.pcdiy_evaluator.engine.strategy.EvaluationRule;
import org.example.pcdiy_evaluator.engine.strategy.PerformanceBottleneckRule;
import org.example.pcdiy_evaluator.engine.strategy.PhysicalCompatibilityRule;
import org.example.pcdiy_evaluator.engine.strategy.PowerSupplyEvaluationRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EvaluationRuleChain {

    private final List<EvaluationRule> chain = new ArrayList<>();

    // 构造函数：在此处严格定义责任链的执行顺序！
    public EvaluationRuleChain() {
        // 第一关：物理与协议兼容校验 (必须最先执行)
        chain.add(new PhysicalCompatibilityRule());

        // 第二关：供电功耗评估
        chain.add(new PowerSupplyEvaluationRule());

        // 第三关：性能瓶颈分析 (木桶效应)
        chain.add(new PerformanceBottleneckRule());
    }

    /**
     * 触发责任链执行
     * @param context 评估上下文
     */
    public void execute(EvaluationContext context) {
        for (EvaluationRule rule : chain) {
            // 执行具体的诊断规则
            boolean shouldContinue = rule.evaluate(context);

            // 【责任链核心逻辑：一票否决短路】
            if (!shouldContinue) {
                // 将上下文的全局状态标记为失败，并跳出循环，不再执行后续规则
                context.setPassed(false);
                break;
            }
        }
    }
}