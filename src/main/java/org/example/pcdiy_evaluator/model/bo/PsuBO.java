package org.example.pcdiy_evaluator.model.bo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PsuBO extends BaseHardwareBO {
    private Integer ratedPower;       // 电源额定功率(W)
    private String formFactor;        // 电源规格，如 ATX, SFX
    private String powerConnector;    // 供电接口类型，如 ATX3.0 12VHPWR

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void parseAttributes(String attributesJson) throws Exception {
        if (attributesJson == null || attributesJson.trim().isEmpty() || "{}".equals(attributesJson.trim())) {
            return;
        }

        JsonNode node = MAPPER.readTree(attributesJson);
        this.ratedPower = node.has("rated_power") ? node.get("rated_power").asInt() : 0;
        this.formFactor = node.has("form_factor") ? node.get("form_factor").asText() : "";
        this.powerConnector = node.has("power_connector") ? node.get("power_connector").asText() : "";
    }

    // 重写 loadFromEntity，正确映射电源的 powerSupply 字段
    @Override
    public void loadFromEntity(org.example.pcdiy_evaluator.model.entity.HardwareComponent entity) {
        super.loadFromEntity(entity);
        // 电源的额定功率存在 powerSupply 字段，优先使用
        if (entity.getPowerSupply() != null) {
            this.tdp = entity.getPowerSupply();
        }
    }

    // 特有属性 Getters
    public Integer getRatedPower() { return ratedPower != null ? ratedPower : tdp; }
    public String getFormFactor() { return formFactor; }
    public String getPowerConnector() { return powerConnector; }
}