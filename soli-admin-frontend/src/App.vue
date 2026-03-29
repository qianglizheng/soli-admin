<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <RouterView />
</template>

<style>
/* 全局样式或基础重置，通常在 styles/index.scss 中统一处理 */
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>
