package org.example.pcdiy_evaluator.engine.strategy;

import org.example.pcdiy_evaluator.engine.context.EvaluationContext;

/**
 * 策略模式 + 责任链模式核心接口
 */
public interface EvaluationRule {

    /**
     * 执行具体的诊断规则
     * * @param context 评估上下文（包含所有的硬件 BO 以及流转状态）
     * @return true: 该项规则通过或不阻断，允许责任链继续执行下一个规则。
     * false: 触发“一票否决”（如插槽不对物理上根本插不上），强制中断整个责任链。
     */
    boolean evaluate(EvaluationContext context);
}