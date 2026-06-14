import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { DiagnosisItem, EvaluationReport, EvaluationRequest, HardwareItem, HardwareType } from '@/types/evaluation'
import { HARDWARE_TYPE_MAP } from '@/types/evaluation'
import { evaluateConfig, fetchHardwareList } from '@/api/evaluator'

export const useEvaluatorStore = defineStore('evaluator', () => {
  /* ── 表单状态 ── */
  const form = ref<EvaluationRequest>({
    cpuId: null,
    motherboardId: null,
    memoryId: null,
    gpuId: null,
    psuId: null,
  })

  /* ── 请求状态 ── */
  const loading = ref(false)
  const error = ref<string | null>(null)

  /* ── 结果 ── */
  const report = ref<EvaluationReport | null>(null)
  const hasEvaluated = ref(false)

  /* ── 硬件列表（下拉数据源） ── */
  const hardwareLists = ref<Record<string, HardwareItem[]>>({})
  const hardwareLoading = ref(false)

  /* ── 计算属性 ── */
  const hasResult = computed(() => report.value !== null)

  const passedCount = computed(() =>
    report.value?.details.filter((d) => d.status === 'PASSED').length ?? 0,
  )

  const failedItems = computed(() =>
    report.value?.details.filter((d) => d.status === 'FAILED') ?? [],
  )

  const warnItems = computed(() =>
    report.value?.details.filter((d) => d.status === 'WARN') ?? [],
  )

  const filledCount = computed(() =>
    Object.values(form.value).filter((v) => v !== null && v !== undefined).length,
  )

  const canSubmit = computed(() => filledCount.value > 0 && !loading.value)

  /* ── 硬件列表操作 ── */
  function getHardwareByType(type: HardwareType): HardwareItem[] {
    const backendType = HARDWARE_TYPE_MAP[type]
    return hardwareLists.value[backendType] ?? []
  }

  async function fetchAllHardware() {
    if (Object.keys(hardwareLists.value).length > 0) return // 已加载则跳过
    hardwareLoading.value = true
    try {
      const types = ['CPU', 'MOTHERBOARD', 'MEMORY', 'GPU', 'PSU'] as const
      const results = await Promise.all(types.map((t) => fetchHardwareList(t)))
      types.forEach((t, i) => {
        hardwareLists.value[t] = results[i]
      })
    } catch (e) {
      console.error('获取硬件列表失败:', e)
    } finally {
      hardwareLoading.value = false
    }
  }

  /* ── 表单操作 ── */
  function updateField(field: keyof EvaluationRequest, value: number | null) {
    form.value[field] = value
  }

  function resetForm() {
    form.value = { cpuId: null, motherboardId: null, memoryId: null, gpuId: null, psuId: null }
    report.value = null
    error.value = null
    hasEvaluated.value = false
  }

  async function submit() {
    if (!canSubmit.value) return

    loading.value = true
    error.value = null
    report.value = null

    try {
      report.value = await evaluateConfig({ ...form.value })
      hasEvaluated.value = true
    } catch (e) {
      error.value = e instanceof Error ? e.message : '未知错误'
    } finally {
      loading.value = false
    }
  }

  return {
    form,
    loading,
    error,
    report,
    hasResult,
    hasEvaluated,
    passedCount,
    failedItems,
    warnItems,
    filledCount,
    canSubmit,
    hardwareLists,
    hardwareLoading,
    getHardwareByType,
    fetchAllHardware,
    updateField,
    resetForm,
    submit,
  }
})
