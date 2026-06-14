package org.example.pcdiy_evaluator.controller;

import org.example.pcdiy_evaluator.mapper.HardwareComponentMapper;
import org.example.pcdiy_evaluator.model.dto.EvaluationReportDTO;
import org.example.pcdiy_evaluator.model.dto.EvaluationRequestDTO;
import org.example.pcdiy_evaluator.model.dto.HardwareItemDTO;
import org.example.pcdiy_evaluator.model.entity.HardwareComponent;
import org.example.pcdiy_evaluator.service.impl.EvaluationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/diy")
// 解决本地 Vue 联调时的跨域问题
@CrossOrigin(origins = "*")
public class EvaluationController {

    @Autowired
    private EvaluationServiceImpl evaluationService;

    @Autowired
    private HardwareComponentMapper hardwareMapper;

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

    /**
     * 查询硬件列表（供前端下拉选择使用）
     *
     * @param type 可选，硬件类型筛选 (CPU, MOTHERBOARD, MEMORY, GPU, PSU)
     * @return 硬件摘要列表
     */
    @GetMapping("/hardware")
    public List<HardwareItemDTO> listHardware(@RequestParam(required = false) String type) {
        List<HardwareComponent> entities;
        if (type != null && !type.trim().isEmpty()) {
            entities = hardwareMapper.selectByType(type.trim().toUpperCase());
        } else {
            // 无筛选时查全部类型
            entities = new java.util.ArrayList<>();
            for (String t : new String[]{"CPU", "MOTHERBOARD", "MEMORY", "GPU", "PSU"}) {
                entities.addAll(hardwareMapper.selectByType(t));
            }
        }
        return entities.stream().map(HardwareItemDTO::from).collect(Collectors.toList());
    }
}