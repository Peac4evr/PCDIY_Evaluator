package org.example.pcdiy_evaluator.model.bo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

public class CpuBO extends BaseHardwareBO {
    private String socket;                     // 针脚，如 LGA1700
    private List<String> supportMemory;        // 支持的内存类型，如 ["DDR4", "DDR5"]
    private String requiredBiosVersion;        // 点亮所需的最低主板 BIOS 版本

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void parseAttributes(String attributesJson) throws Exception {
        if (attributesJson == null || attributesJson.trim().isEmpty() || "{}".equals(attributesJson.trim())) {
            return;
        }

        JsonNode node = MAPPER.readTree(attributesJson);
        this.socket = node.has("socket") ? node.get("socket").asText() : "";
        this.requiredBiosVersion = node.has("required_bios_version") ? node.get("required_bios_version").asText() : "";

        this.supportMemory = new ArrayList<>();
        if (node.has("support_memory")) {
            for (JsonNode memNode : node.get("support_memory")) {
                this.supportMemory.add(memNode.asText());
            }
        }
    }

    // 特有属性 Getters
    public String getSocket() { return socket; }
    public List<String> getSupportMemory() { return supportMemory; }
    public String getRequiredBiosVersion() { return requiredBiosVersion; }
}