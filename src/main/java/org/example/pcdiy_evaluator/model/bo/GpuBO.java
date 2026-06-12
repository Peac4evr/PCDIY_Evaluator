package org.example.pcdiy_evaluator.model.bo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GpuBO extends BaseHardwareBO {
    private Integer lengthMm;       // 显卡长度(毫米) - 预留用于未来校验机箱限长
    private String powerConnector;  // 供电接口类型 - 预留用于校验电源线材(如 ATX3.0 12VHPWR)

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void parseAttributes(String attributesJson) throws Exception {
        // 显卡的扩展属性可能为空，需要做防御性处理
        if (attributesJson == null || attributesJson.trim().isEmpty() || "{}".equals(attributesJson.trim())) {
            return;
        }

        JsonNode node = MAPPER.readTree(attributesJson);
        this.lengthMm = node.has("length_mm") ? node.get("length_mm").asInt() : 0;
        this.powerConnector = node.has("power_connector") ? node.get("power_connector").asText() : "";
    }

    // 特有属性 Getters
    public Integer getLengthMm() { return lengthMm; }
    public String getPowerConnector() { return powerConnector; }
}