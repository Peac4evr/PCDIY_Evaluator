import type { EvaluationReport, EvaluationRequest, HardwareItem } from '@/types/evaluation'

const BASE_URL = '/api/v1/diy'

/** 调用后端评估接口 */
export async function evaluateConfig(request: EvaluationRequest): Promise<EvaluationReport> {
  const response = await fetch(`${BASE_URL}/evaluate`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  })

  if (!response.ok) {
    const error = await response.json().catch(() => null)
    throw new Error(error?.message ?? `HTTP ${response.status}: 评估请求失败`)
  }

  return response.json()
}

/** 获取硬件列表（供下拉选择使用） */
export async function fetchHardwareList(type?: string): Promise<HardwareItem[]> {
  const params = type ? `?type=${encodeURIComponent(type)}` : ''
  const response = await fetch(`${BASE_URL}/hardware${params}`)

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}: 获取硬件列表失败`)
  }

  return response.json()
}
