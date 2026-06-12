package org.example.pcdiy_evaluator.service.impl;

import org.example.pcdiy_evaluator.engine.chain.EvaluationRuleChain;
import org.example.pcdiy_evaluator.engine.context.EvaluationContext;
import org.example.pcdiy_evaluator.engine.factory.HardwareFactory;
import org.example.pcdiy_evaluator.engine.template.AbstractEvaluationEngine;
import org.example.pcdiy_evaluator.mapper.HardwareComponentMapper;
import org.example.pcdiy_evaluator.model.bo.BaseHardwareBO;
import org.example.pcdiy_evaluator.model.dto.EvaluationRequestDTO;
import org.example.pcdiy_evaluator.model.entity.HardwareComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EvaluationServiceImpl extends AbstractEvaluationEngine {

    @Autowired
    private HardwareComponentMapper hardwareMapper;

    @Autowired
    private EvaluationRuleChain ruleChain;

    /**
     * 步骤 1：构建上下文环境 (将零散的 ID 转化为具备行为的 BO 业务对象)
     */
    @Override
    protected EvaluationContext buildContext(EvaluationRequestDTO request) {
        EvaluationContext context = new EvaluationContext();
        context.setOriginalRequest(request);

        // 收集需要查询的硬件 ID
        List<Long> idsToQuery = new ArrayList<>();
        if (request.getCpuId() != null) idsToQuery.add(request.getCpuId());
        if (request.getMotherboardId() != null) idsToQuery.add(request.getMotherboardId());
        if (request.getMemoryId() != null) idsToQuery.add(request.getMemoryId());
        if (request.getGpuId() != null) idsToQuery.add(request.getGpuId());
        if (request.getPsuId() != null) idsToQuery.add(request.getPsuId());

        if (idsToQuery.isEmpty()) {
            throw new IllegalArgumentException("装机清单不能为空，请至少选择一件硬件！");
        }

        // 批量查询数据库：避免 N+1 问题，1次网络 IO 搞定
        List<HardwareComponent> entities = hardwareMapper.selectBatchIds(idsToQuery);

        // 核心亮点：使用【简单工厂模式】将毫无生气的 Entity 转化为多态的 BO
        for (HardwareComponent entity : entities) {
            BaseHardwareBO bo = HardwareFactory.create(entity);
            if (bo != null) {
                // 放入上下文中，按硬件类型分类存放 (如 "CPU" -> CpuBO)
                context.putHardware(entity.getType(), bo);
            }
        }

        return context;
    }

    /**
     * 步骤 2：执行规则链条
     */
    @Override
    protected void runRulesChain(EvaluationContext context) {
        // 直接将组装好的上下文丢给责任链去跑
        ruleChain.execute(context);
    }
}