
import { defineStore } from 'pinia';
import { type RouteLocationNormalized } from 'vue-router';

export interface TagView extends Partial<RouteLocationNormalized> {
  title?: string;
}

export const useTagsViewStore = defineStore('tagsView', {
  state: () => ({
    visitedViews: [] as TagView[],
    cachedViews: [] as string[]
  }),
  actions: {
    addView(view: TagView) {
      this.addVisitedView(view);
      this.addCachedView(view);
    },
    addVisitedView(view: TagView) {
      if (this.visitedViews.some(v => v.path === view.path)) return;
      this.visitedViews.push(
        Object.assign({}, view, {
          title: view.meta?.title || 'no-name'
        })
      );
    },
    addCachedView(view: TagView) {
      if (this.cachedViews.includes(view.name as string)) return;
      if (!view.meta?.noCache) {
        this.cachedViews.push(view.name as string);
      }
    },
    delView(view: TagView) {
      return new Promise(resolve => {
        this.delVisitedView(view);
        this.delCachedView(view);
        resolve({
          visitedViews: [...this.visitedViews],
          cachedViews: [...this.cachedViews]
        });
      });
    },
    delVisitedView(view: TagView) {
      for (const [i, v] of this.visitedViews.entries()) {
        if (v.path === view.path) {
          this.visitedViews.splice(i, 1);
          break;
        }
      }
    },
    delCachedView(view: TagView) {
      const index = this.cachedViews.indexOf(view.name as string);
      index > -1 && this.cachedViews.splice(index, 1);
    }
  }
});
