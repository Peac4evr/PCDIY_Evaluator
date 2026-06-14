package org.example.pcdiy_evaluator.model.dto;

import java.util.ArrayList;
import java.util.List;

public class EvaluationReportDTO {
    private boolean passed;            // 是否通过基础兼容检查（责任链最终状态）
    private int totalHardwareCostW;    // 整机预估峰值功耗
    private String bottleneckAnalysis; // 性能瓶颈一句话总结
    private List<DiagnosisItem> details = new ArrayList<>(); // 各项策略的详细诊断报告

    // 内部类：单项诊断详情
    public static class DiagnosisItem {
        private String ruleName;       // 规则名称 (如: 物理兼容校验, 供电功耗评估)
        private boolean success;       // 该项校验是否通过
        private String status;         // 状态状态码: PASSED, WARN, FAILED, SKIPPED (被责任链截断)
        private String message;        // 核心诊断建议描述
        private List<String> relatedParts = new ArrayList<>(); // 涉及的硬件名称列表

        public DiagnosisItem() {}

        public DiagnosisItem(String ruleName, boolean success, String status, String message) {
            this.ruleName = ruleName;
            this.success = success;
            this.status = status;
            this.message = message;
        }

        public DiagnosisItem(String ruleName, boolean success, String status, String message, List<String> relatedParts) {
            this.ruleName = ruleName;
            this.success = success;
            this.status = status;
            this.message = message;
            this.relatedParts = relatedParts != null ? relatedParts : new ArrayList<>();
        }

        // Getters and Setters
        public String getRuleName() { return ruleName; }
        public void setRuleName(String ruleName) { this.ruleName = ruleName; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<String> getRelatedParts() { return relatedParts; }
        public void setRelatedParts(List<String> relatedParts) { this.relatedParts = relatedParts != null ? relatedParts : new ArrayList<>(); }
    }

    // Getters and Setters
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public int getTotalHardwareCostW() { return totalHardwareCostW; }
    public void setTotalHardwareCostW(int totalHardwareCostW) { this.totalHardwareCostW = totalHardwareCostW; }

    public String getBottleneckAnalysis() { return bottleneckAnalysis; }
    public void setBottleneckAnalysis(String bottleneckAnalysis) { this.bottleneckAnalysis = bottleneckAnalysis; }

    public List<DiagnosisItem> getDetails() { return details; }
    public void setDetails(List<DiagnosisItem> details) { this.details = details; }

    public void addDetail(DiagnosisItem item) { this.details.add(item); }
}