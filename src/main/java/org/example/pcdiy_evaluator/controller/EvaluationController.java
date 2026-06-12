package org.example.pcdiy_evaluator.controller;

import org.example.pcdiy_evaluator.model.dto.EvaluationReportDTO;
import org.example.pcdiy_evaluator.model.dto.EvaluationRequestDTO;
import org.example.pcdiy_evaluator.service.impl.EvaluationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/diy")
// 解决本地 Vue 联调时的跨域问题
@CrossOrigin(origins = "*")
public class EvaluationController {

    @Autowired
    private EvaluationServiceImpl evaluationService;

    /**
     * 智能装机配置评估接口
     * * @param request 前端传来的包含各类硬件 ID 的装机清单
     * @return 深度诊断报告
     */
    @PostMapping("/evaluate")
    public EvaluationReportDTO evaluateConfiguration(@RequestBody EvaluationRequestDTO request) {
        // 没有任何啰嗦的逻辑，直接将 DTO 交给引擎统帅执行
        return evaluationService.executeEvaluation(request);
    }
}