import { nextTick, onBeforeUnmount, onMounted, ref, watch, type Ref } from 'vue';

/**
 * 搜索条件折叠逻辑。
 * 当首行无法容纳全部查询条件时，显示“更多”按钮并折叠扩展条件。
 */
export function useSearchCollapse(visibleRef?: Ref<boolean>) {
  const searchCollapseRef = ref<HTMLElement>();
  const isSearchMeasured = ref(false);
  const showMoreButton = ref(false);
  const showMoreSearch = ref(false);

  let resizeObserver: ResizeObserver | null = null;
  let frameId = 0;

  const getElementOuterWidth = (element: HTMLElement) => {
    const styles = window.getComputedStyle(element);
    const marginLeft = Number.parseFloat(styles.marginLeft || '0');
    const marginRight = Number.parseFloat(styles.marginRight || '0');

    return element.offsetWidth + marginLeft + marginRight;
  };

  const refreshMoreButton = () => {
    if (frameId) {
      window.cancelAnimationFrame(frameId);
    }

    frameId = window.requestAnimationFrame(() => {
      const container = searchCollapseRef.value;

      if (!container || container.clientWidth === 0) {
        return;
      }

      const searchItems = Array.from(
        container.querySelectorAll<HTMLElement>('[data-search-item="true"]')
      );
      const searchActions = container.querySelector<HTMLElement>('[data-search-actions="true"]');
      const hasMoreItem = searchItems.some((item) => item.dataset.searchMore === 'true');

      isSearchMeasured.value = true;

      if (!hasMoreItem) {
        showMoreButton.value = false;
        showMoreSearch.value = true;
        return;
      }

      const totalWidth =
        searchItems.reduce((sum, item) => {
          return sum + getElementOuterWidth(item);
        }, 0) + (searchActions ? getElementOuterWidth(searchActions) : 0);
      const shouldShowMoreButton = totalWidth > container.clientWidth + 1;
      const lastShowMoreButton = showMoreButton.value;

      showMoreButton.value = shouldShowMoreButton;

      if (!shouldShowMoreButton) {
        showMoreSearch.value = true;
        return;
      }

      if (!lastShowMoreButton) {
        showMoreSearch.value = false;
      }
    });
  };

  const toggleMoreSearch = () => {
    if (!showMoreButton.value) {
      return;
    }

    showMoreSearch.value = !showMoreSearch.value;
  };

  onMounted(() => {
    nextTick(() => {
      refreshMoreButton();

      if (typeof ResizeObserver === 'undefined') {
        return;
      }

      const container = searchCollapseRef.value;

      if (!container) {
        return;
      }

      resizeObserver = new ResizeObserver(() => {
        refreshMoreButton();
      });
      resizeObserver.observe(container);
    });
  });

  if (visibleRef) {
    watch(visibleRef, (value) => {
      if (!value) {
        return;
      }

      nextTick(() => {
        refreshMoreButton();
      });
    });
  }

  onBeforeUnmount(() => {
    resizeObserver?.disconnect();

    if (frameId) {
      window.cancelAnimationFrame(frameId);
    }
  });

  return {
    searchCollapseRef,
    isSearchMeasured,
    showMoreButton,
    showMoreSearch,
    toggleMoreSearch,
    refreshMoreButton,
  };
}
