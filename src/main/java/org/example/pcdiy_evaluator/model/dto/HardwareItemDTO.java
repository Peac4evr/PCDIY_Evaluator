package org.example.pcdiy_evaluator.model.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pcdiy_evaluator.model.entity.HardwareComponent;

/**
 * 硬件摘要 DTO —— 供前端下拉列表使用
 * 包含硬件基本字段 + 从 attributes JSON 中提取的关键规格摘要
 */
public class HardwareItemDTO {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Long id;
    private String name;
    private String brand;
    private String type;
    private Integer tdp;
    private Integer performanceScore;
    private String specs; // 关键规格摘要，如 "LGA1700 · DDR5 · 125W"

    public HardwareItemDTO() {}

    /**
     * 从实体转换，自动根据类型解析 attributes 生成规格摘要
     */
    public static HardwareItemDTO from(HardwareComponent entity) {
        HardwareItemDTO dto = new HardwareItemDTO();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.brand = entity.getBrand();
        dto.type = entity.getType();
        dto.tdp = entity.getTdp();
        dto.performanceScore = entity.getPerformanceScore();
        dto.specs = buildSpecs(entity.getType(), entity.getAttributes());
        return dto;
    }

    /**
     * 根据硬件类型解析 attributes JSON，生成人类可读的规格摘要
     */
    private static String buildSpecs(String type, String attributesJson) {
        if (attributesJson == null || attributesJson.trim().isEmpty() || "{}".equals(attributesJson.trim())) {
            return "";
        }
        try {
            JsonNode node = MAPPER.readTree(attributesJson);
            switch (type.toUpperCase()) {
                case "CPU":
                    return buildCpuSpecs(node);
                case "MOTHERBOARD":
                    return buildMotherboardSpecs(node);
                case "MEMORY":
                    return buildMemorySpecs(node);
                case "GPU":
                    return buildGpuSpecs(node);
                case "PSU":
                    return buildPsuSpecs(node);
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private static String buildCpuSpecs(JsonNode node) {
        StringBuilder sb = new StringBuilder();
        if (node.has("socket") && !node.get("socket").asText().isEmpty()) {
            sb.append(node.get("socket").asText());
        }
        if (node.has("support_memory") && node.get("support_memory").isArray() && node.get("support_memory").size() > 0) {
            if (sb.length() > 0) sb.append(" · ");
            sb.append(String.join("/", node.get("support_memory").findValuesAsText("")));
        }
        return sb.toString();
    }

    private static String buildMotherboardSpecs(JsonNode node) {
        StringBuilder sb = new StringBuilder();
        if (node.has("socket") && !node.get("socket").asText().isEmpty()) {
            sb.append(node.get("socket").asText());
        }
        if (node.has("memory_type") && !node.get("memory_type").asText().isEmpty()) {
            if (sb.length() > 0) sb.append(" · ");
            sb.append(node.get("memory_type").asText());
        }
        return sb.toString();
    }

    private static String buildMemorySpecs(JsonNode node) {
        StringBuilder sb = new StringBuilder();
        if (node.has("memory_type") && !node.get("memory_type").asText().isEmpty()) {
            sb.append(node.get("memory_type").asText());
        }
        if (node.has("capacity") && node.get("capacity").asInt() > 0) {
            if (sb.length() > 0) sb.append(" · ");
            sb.append(node.get("capacity").asInt()).append("GB");
        }
        return sb.toString();
    }

    private static String buildGpuSpecs(JsonNode node) {
        StringBuilder sb = new StringBuilder();
        if (node.has("length_mm") && node.get("length_mm").asInt() > 0) {
            sb.append(node.get("length_mm").asInt()).append("mm");
        }
        if (node.has("power_connector") && !node.get("power_connector").asText().isEmpty()) {
            if (sb.length() > 0) sb.append(" · ");
            sb.append(node.get("power_connector").asText());
        }
        return sb.toString();
    }

    private static String buildPsuSpecs(JsonNode node) {
        StringBuilder sb = new StringBuilder();
        if (node.has("rated_power") && node.get("rated_power").asInt() > 0) {
            sb.append(node.get("rated_power").asInt()).append("W");
        }
        if (node.has("form_factor") && !node.get("form_factor").asText().isEmpty()) {
            if (sb.length() > 0) sb.append(" · ");
            sb.append(node.get("form_factor").asText());
        }
        return sb.toString();
    }

    // ── Getters & Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getTdp() { return tdp; }
    public void setTdp(Integer tdp) { this.tdp = tdp; }

    public Integer getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Integer performanceScore) { this.performanceScore = performanceScore; }

    public String getSpecs() { return specs; }
    public void setSpecs(String specs) { this.specs = specs; }
}
