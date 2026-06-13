package org.example.pcdiy_evaluator.model.bo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MotherboardBO extends BaseHardwareBO {
    private String socket;               // 主板插槽，如 LGA1700
    private String memoryType;           // 支持的内存，如 DDR5
    private String currentBiosVersion;   // 当前出厂 BIOS 版本

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void parseAttributes(String attributesJson) throws Exception {
        if (attributesJson == null || attributesJson.trim().isEmpty() || "{}".equals(attributesJson.trim())) {
            return;
        }

        JsonNode node = MAPPER.readTree(attributesJson);
        this.socket = node.has("socket") ? node.get("socket").asText() : "";
        this.memoryType = node.has("memory_type") ? node.get("memory_type").asText() : "";
        this.currentBiosVersion = node.has("current_bios_version") ? node.get("current_bios_version").asText() : "";
    }

    // 特有属性 Getters
    public String getSocket() { return socket; }
    public String getMemoryType() { return memoryType; }
    public String getCurrentBiosVersion() { return currentBiosVersion; }
}