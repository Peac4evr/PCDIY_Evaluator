package org.example.pcdiy_evaluator.model.bo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MemoryBO extends BaseHardwareBO {
    private String memoryType; // 内存代数，如 DDR4, DDR5
    private Integer capacity;  // 单条容量，如 16, 32 (GB)

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void parseAttributes(String attributesJson) throws Exception {
        if (attributesJson == null || attributesJson.trim().isEmpty() || "{}".equals(attributesJson.trim())) {
            return;
        }

        JsonNode node = MAPPER.readTree(attributesJson);
        this.memoryType = node.has("memory_type") ? node.get("memory_type").asText() : "";
        this.capacity = node.has("capacity") ? node.get("capacity").asInt() : 0;
    }

    // 特有属性 Getters
    public String getMemoryType() { return memoryType; }
    public Integer getCapacity() { return capacity; }
}