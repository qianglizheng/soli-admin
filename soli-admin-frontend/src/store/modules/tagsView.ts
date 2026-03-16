
import { defineStore } from 'pinia';
import { type RouteLocationNormalized } from 'vue-router';
import { ref } from 'vue';

export interface TagView extends Partial<RouteLocationNormalized> {
  title?: string;
}

export const useTagsViewStore = defineStore('tagsView', () => {
  const visitedViews = ref<TagView[]>([]);
  const cachedViews = ref<string[]>([]);

  const addView = (view: TagView) => {
    addVisitedView(view);
    addCachedView(view);
  };

  const addVisitedView = (view: TagView) => {
    if (visitedViews.value.some(v => v.path === view.path)) return;
    visitedViews.value.push(
      Object.assign({}, view, {
        title: view.meta?.title || 'no-name'
      })
    );
  };

  const addCachedView = (view: TagView) => {
    if (cachedViews.value.includes(view.name as string)) return;
    if (!view.meta?.noCache) {
      cachedViews.value.push(view.name as string);
    }
  };

  const delView = (view: TagView) => {
    return new Promise(resolve => {
      delVisitedView(view);
      delCachedView(view);
      resolve({
        visitedViews: [...visitedViews.value],
        cachedViews: [...cachedViews.value]
      });
    });
  };

  const delVisitedView = (view: TagView) => {
    for (const [i, v] of visitedViews.value.entries()) {
      if (v.path === view.path) {
        visitedViews.value.splice(i, 1);
        break;
      }
    }
  };

  const delCachedView = (view: TagView) => {
    const index = cachedViews.value.indexOf(view.name as string);
    if (index > -1) {
      cachedViews.value.splice(index, 1);
    }
  };

  return {
    visitedViews,
    cachedViews,
    addView,
    addVisitedView,
    addCachedView,
    delView,
    delVisitedView,
    delCachedView
  };
});
