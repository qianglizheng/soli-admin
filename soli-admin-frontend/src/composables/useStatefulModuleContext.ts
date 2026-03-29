import { ref } from 'vue';
import type { ModuleContext } from '@/api/moduleCenter';

interface UseStatefulModuleContextOptions<TState extends string> {
  loadContext: (stateCode?: TState) => Promise<ModuleContext | null>;
}

export function useStatefulModuleContext<TState extends string>(
  options: UseStatefulModuleContextOptions<TState>
) {
  const listContext = ref<ModuleContext | null>(null);
  const activeContext = ref<ModuleContext | null>(null);
  const stateContextMap = ref<Partial<Record<TState, ModuleContext | null>>>({});

  const loadListContext = async () => {
    listContext.value = await options.loadContext();
    return listContext.value;
  };

  const ensureStateContext = async (stateCode: TState) => {
    if (Object.prototype.hasOwnProperty.call(stateContextMap.value, stateCode)) {
      return stateContextMap.value[stateCode] || null;
    }
    const context = await options.loadContext(stateCode);
    stateContextMap.value = {
      ...stateContextMap.value,
      [stateCode]: context
    };
    return context;
  };

  const preloadStateContexts = async (stateCodes: TState[]) => {
    await Promise.all(stateCodes.map((stateCode) => ensureStateContext(stateCode)));
  };

  const setActiveStateContext = async (stateCode: TState) => {
    activeContext.value = await ensureStateContext(stateCode);
    return activeContext.value;
  };

  const getStateContext = (stateCode: TState) => {
    return stateContextMap.value[stateCode] || null;
  };

  return {
    activeContext,
    ensureStateContext,
    getStateContext,
    listContext,
    loadListContext,
    preloadStateContexts,
    setActiveStateContext,
    stateContextMap
  };
}
