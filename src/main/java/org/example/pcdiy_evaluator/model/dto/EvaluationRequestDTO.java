package org.example.pcdiy_evaluator.model.dto;

public class EvaluationRequestDTO {
    private Long cpuId;
    private Long motherboardId;
    private Long memoryId;
    private Long gpuId;
    private Long psuId;

    // Getters and Setters
    public Long getCpuId() { return cpuId; }
    public void setCpuId(Long cpuId) { this.cpuId = cpuId; }

    public Long getMotherboardId() { return motherboardId; }
    public void setMotherboardId(Long motherboardId) { this.motherboardId = motherboardId; }

    public Long getMemoryId() { return memoryId; }
    public void setMemoryId(Long memoryId) { this.memoryId = memoryId; }

    public Long getGpuId() { return gpuId; }
    public void setGpuId(Long gpuId) { this.gpuId = gpuId; }

    public Long getPsuId() { return psuId; }
    public void setPsuId(Long psuId) { this.psuId = psuId; }
}