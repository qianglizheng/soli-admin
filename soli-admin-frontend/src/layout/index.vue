
<template>
  <div class="app-wrapper">
    <div class="sidebar-container" :class="{ collapse: !sidebar.opened }">
      <Sidebar />
    </div>
    <div class="main-container">
      <div class="header-container">
        <Header />
      </div>
      <TagsView />
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useAppStore } from '@/store/modules/app';
import Sidebar from './components/Sidebar.vue';
import Header from './components/Header.vue';
import TagsView from './components/TagsView.vue';

const appStore = useAppStore();
const sidebar = computed(() => appStore.sidebar);
</script>

<style scoped lang="scss">
/* Transitions */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.5s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
