package org.example.pcdiy_evaluator.engine.context;

import org.example.pcdiy_evaluator.model.bo.BaseHardwareBO;
import org.example.pcdiy_evaluator.model.dto.EvaluationReportDTO;
import org.example.pcdiy_evaluator.model.dto.EvaluationRequestDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 规则引擎上下文：在整个评估生命周期中流转，收集数据与诊断结果
 */
public class EvaluationContext {

    // 1. 原始入参
    private EvaluationRequestDTO originalRequest;

    // 2. 解析后的多态业务对象集合 (Key: 硬件类型如 "CPU", Value: 对应的 BO 对象)
    private Map<String, BaseHardwareBO> hardwareMap = new HashMap<>();

    // 3. 核心流转状态：是否被“一票否决”拦截（默认 true，一旦失败设为 false）
    private boolean passed = true;

    // 4. 统计数据：整机峰值总功耗预估
    private int totalCostW = 0;

    // 5. 诊断明细收集器
    private List<EvaluationReportDTO.DiagnosisItem> diagnosisItems = new ArrayList<>();

    // --- 快捷操作方法 ---
    public void addDiagnosisItem(EvaluationReportDTO.DiagnosisItem item) {
        this.diagnosisItems.add(item);
    }

    public BaseHardwareBO getHardware(String type) {
        return hardwareMap.get(type.toUpperCase());
    }

    public void putHardware(String type, BaseHardwareBO bo) {
        this.hardwareMap.put(type.toUpperCase(), bo);
    }

    // --- 基础 Getters and Setters ---
    public EvaluationRequestDTO getOriginalRequest() { return originalRequest; }
    public void setOriginalRequest(EvaluationRequestDTO originalRequest) { this.originalRequest = originalRequest; }

    public Map<String, BaseHardwareBO> getHardwareMap() { return hardwareMap; }
    public void setHardwareMap(Map<String, BaseHardwareBO> hardwareMap) { this.hardwareMap = hardwareMap; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public int getTotalCostW() { return totalCostW; }
    public void setTotalCostW(int totalCostW) { this.totalCostW = totalCostW; }

    public List<EvaluationReportDTO.DiagnosisItem> getDiagnosisItems() { return diagnosisItems; }
    public void setDiagnosisItems(List<EvaluationReportDTO.DiagnosisItem> diagnosisItems) { this.diagnosisItems = diagnosisItems; }
}