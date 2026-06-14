/* ── 后端 API 类型定义 ── */

/** 评估请求 */
export interface EvaluationRequest {
  cpuId: number | null
  motherboardId: number | null
  memoryId: number | null
  gpuId: number | null
  psuId: number | null
}

/** 硬件摘要（下拉列表用） */
export interface HardwareItem {
  id: number
  name: string
  brand: string
  type: string
  tdp: number
  performanceScore: number
  specs: string
}

/** 诊断状态 */
export type DiagnosisStatus = 'PASSED' | 'WARN' | 'FAILED' | 'SKIPPED'

/** 单项诊断明细 */
export interface DiagnosisItem {
  ruleName: string
  success: boolean
  status: DiagnosisStatus
  message: string
  relatedParts: string[]
}

/** 评估报告 */
export interface EvaluationReport {
  passed: boolean
  totalHardwareCostW: number
  bottleneckAnalysis: string | null
  details: DiagnosisItem[]
}

/** API 错误响应 */
export interface ApiError {
  passed: false
  error: string
  message: string
}

/** 硬件类型标识 */
export type HardwareType = 'cpu' | 'motherboard' | 'memory' | 'gpu' | 'psu'

/** 硬件类型到后端 type 的映射 */
export const HARDWARE_TYPE_MAP: Record<HardwareType, string> = {
  cpu: 'CPU',
  motherboard: 'MOTHERBOARD',
  memory: 'MEMORY',
  gpu: 'GPU',
  psu: 'PSU',
}

/** 后端 type 到中文标签的映射 */
export const TYPE_LABEL_MAP: Record<string, string> = {
  CPU: '处理器',
  MOTHERBOARD: '主板',
  MEMORY: '内存',
  GPU: '显卡',
  PSU: '电源',
}
