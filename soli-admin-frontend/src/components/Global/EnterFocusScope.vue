<template>
  <div
    ref="scopeRef"
    class="enter-focus-scope"
    :class="{ 'enter-focus-scope--fill': fill }"
    @keydown.capture="handleKeydown"
  >
    <slot />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

type FocusContainer = string | HTMLElement;

const props = withDefaults(defineProps<{
  fill?: boolean;
  includeTextarea?: boolean;
  extraSelectors?: string[];
}>(), {
  fill: false,
  includeTextarea: false,
  extraSelectors: () => []
});

const emit = defineEmits<{
  'reach-end': [payload: { target: HTMLElement }]
}>();

const scopeRef = ref<HTMLElement>();

const focusSelectors = computed(() => {
  const selectors = [
    '.el-date-editor:not(.is-disabled) input:not([disabled])',
    '.el-select:not(.is-disabled) .el-select__wrapper',
    '.el-input-number:not(.is-disabled) .el-input__inner:not([disabled])',
    '.el-input:not(.is-disabled) .el-input__inner:not([disabled]):not([readonly])'
  ];
  if (props.includeTextarea) {
    selectors.push('.el-textarea:not(.is-disabled) .el-textarea__inner:not([disabled]):not([readonly])');
  }
  return [...selectors, ...props.extraSelectors].join(', ');
});

const isVisible = (element: HTMLElement) => {
  if (!element.isConnected) {
    return false;
  }
  const style = window.getComputedStyle(element);
  return style.display !== 'none' && style.visibility !== 'hidden' && style.pointerEvents !== 'none';
};

const collectTargets = (container?: FocusContainer) => {
  const scopeElement = scopeRef.value;
  if (!scopeElement || !focusSelectors.value) {
    return [] as HTMLElement[];
  }
  const containerElements = typeof container === 'string'
    ? Array.from(scopeElement.querySelectorAll<HTMLElement>(container))
    : container
      ? [container]
      : [];
  return Array.from(scopeElement.querySelectorAll<HTMLElement>(focusSelectors.value)).filter((element, index, list) => {
    if (!isVisible(element)) {
      return false;
    }
    if (containerElements.length && !containerElements.some(item => item.contains(element) || item === element)) {
      return false;
    }
    return list.indexOf(element) === index;
  });
};

const resolveCurrentIndex = (targets: HTMLElement[], target: HTMLElement) => {
  const activeElement = document.activeElement as HTMLElement | null;
  return targets.findIndex((item) => {
    if (item === activeElement || item === target) {
      return true;
    }
    if (activeElement && item.contains(activeElement)) {
      return true;
    }
    return item.contains(target);
  });
};

const focusTarget = (target: HTMLElement) => {
  target.focus();
  if (target instanceof HTMLInputElement || target instanceof HTMLTextAreaElement) {
    requestAnimationFrame(() => {
      try {
        const length = target.value.length;
        target.setSelectionRange(length, length);
      } catch {
        // 忽略不支持设置选区范围的组件。
      }
    });
  }
};

const focusNextFrom = (target: HTMLElement) => {
  const targets = collectTargets();
  const currentIndex = resolveCurrentIndex(targets, target);
  if (currentIndex < 0 || currentIndex >= targets.length - 1) {
    return false;
  }
  const nextTarget = targets[currentIndex + 1];
  if (!nextTarget) {
    return false;
  }
  focusTarget(nextTarget);
  return true;
};

const focusFirst = (container?: FocusContainer) => {
  const target = collectTargets(container)[0];
  if (!target) {
    return false;
  }
  focusTarget(target);
  return true;
};

const focusInLastContainer = (containerSelector: string) => {
  if (!scopeRef.value) {
    return false;
  }
  const containers = Array.from(scopeRef.value.querySelectorAll<HTMLElement>(containerSelector)).filter(isVisible);
  const lastContainer = containers[containers.length - 1];
  if (!lastContainer) {
    return false;
  }
  return focusFirst(lastContainer);
};

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key !== 'Enter' || event.isComposing || event.altKey || event.ctrlKey || event.metaKey || event.shiftKey) {
    return;
  }
  const target = event.target as HTMLElement | null;
  if (!target || !scopeRef.value?.contains(target)) {
    return;
  }
  if (target.closest('[data-enter-focus-stop]')) {
    return;
  }
  if (!props.includeTextarea && target instanceof HTMLTextAreaElement) {
    return;
  }
  const targets = collectTargets();
  const currentIndex = resolveCurrentIndex(targets, target);
  if (currentIndex < 0) {
    return;
  }
  if (currentIndex >= targets.length - 1) {
    event.preventDefault();
    event.stopPropagation();
    emit('reach-end', { target });
    return;
  }
  const nextTarget = targets[currentIndex + 1];
  if (!nextTarget) {
    return;
  }
  event.preventDefault();
  event.stopPropagation();
  focusTarget(nextTarget);
};

defineExpose({
  focusNextFrom,
  focusFirst,
  focusInLastContainer
});
</script>

<style scoped lang="scss">
.enter-focus-scope {
  min-width: 0;
}

.enter-focus-scope--fill {
  height: 100%;
  min-height: 0;
}
</style>
