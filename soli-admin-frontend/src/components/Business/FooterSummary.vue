<template>
  <div class="footer-summary-container">
    <div class="summary-wrapper">
      <!-- 基础合计展示区 (始终可见) -->
      <div class="summary-main">
        <div class="summary-section">
          <template v-for="(item, index) in items" :key="'main-'+index">
            <div class="summary-item" :class="item.type">
              <div class="item-content">
                <span class="label">{{ item.label }}</span>
                <span class="value" :class="item.valueClass">
                  <span class="currency" v-if="item.isMoney">¥</span>{{ item.value }}
                </span>
              </div>
            </div>
            <!-- 项目之间的垂直分割线 -->
            <div v-if="index < items.length - 1" class="summary-divider"></div>
          </template>
        </div>

        <!-- 更多合计扩展按钮：仅在有 moreItems 时显示 -->
        <div class="summary-actions" v-if="moreItems && moreItems.length > 0">
          <el-button link class="no-style-btn" @click="isExpanded = !isExpanded">
            {{ isExpanded ? '收起详情' : '更多合计' }}
            <el-icon class="el-icon--right toggle-icon" :class="{ expanded: isExpanded }">
              <ArrowDown />
            </el-icon>
          </el-button>
        </div>
      </div>

      <!-- 扩展明细展示区：使用纯 CSS 高度动画，避免底部吸附场景下的顿挫 -->
      <div v-if="moreItems && moreItems.length > 0" class="summary-expand" :class="{ expanded: isExpanded }">
        <div class="summary-expand-inner">
          <div class="summary-more">
            <div class="more-grid">
              <div v-for="(item, index) in moreItems" :key="'more-'+index" class="more-item">
                <span class="more-label">{{ item.label }}：</span>
                <span class="more-value" :class="{ 'is-money': item.isMoney }">
                  {{ item.isMoney ? '¥' : '' }}{{ item.value }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ArrowDown } from '@element-plus/icons-vue';

/**
 * 合计项接口定义
 */
export interface SummaryItem {
  label: string;
  value: string | number;
  type?: string;       /* 样式类型：如 'amount' 表示金额红色显示 */
  valueClass?: string; /* 自定义 CSS 类 */
  isMoney?: boolean;   /* 是否显示人民币符号 */
}

interface Props {
  items: SummaryItem[];      /* 主展示区项目 */
  moreItems?: SummaryItem[]; /* 扩展展示区项目 */
}

defineProps<Props>();
const isExpanded = ref(false);
</script>

<style scoped lang="scss">
/* 合计栏容器：采用 Flex 布局流，通过父级控制固定到底部 */
.footer-summary-container {
  flex-shrink: 0;
  background: #ffffff;
  border-top: 1px solid var(--app-border-color);
}

.summary-wrapper {
  padding: 0 24px;
}

/* 主合计行布局 */
.summary-main {
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.summary-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.summary-item {
  display: flex;
  align-items: center;
  
  .item-content {
    display: flex;
    align-items: baseline;
    gap: 8px;
    
    .label { font-size: 13px; color: #595959; }
    .value {
      font-size: 16px;
      font-weight: 700;
      color: #262626;
      font-family: 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
      .currency { font-size: 12px; margin-right: 2px; }
    }
  }

  /* 强调金额样式 */
  &.amount .value { color: #cf1322; }
}

.summary-divider {
  width: 1px;
  height: 16px;
  background: #f0f0f0;
}

/* 无干扰按钮样式 */
.no-style-btn {
  color: #8c8c8c !important;
  font-size: 13px;
  padding: 0 4px;
  &:hover { color: var(--app-primary) !important; }
}

.toggle-icon {
  transition: transform 0.28s cubic-bezier(0.22, 1, 0.36, 1);

  &.expanded {
    transform: rotate(180deg);
  }
}

.summary-expand {
  display: grid;
  grid-template-rows: 0fr;
  transition: grid-template-rows 0.28s cubic-bezier(0.22, 1, 0.36, 1);
}

.summary-expand.expanded {
  grid-template-rows: 1fr;
}

.summary-expand-inner {
  min-height: 0;
  overflow: hidden;
}

.summary-more {
  padding: 0;
  border-top: 1px solid transparent;
  opacity: 0;
  transform: translateY(8px);
  transition:
    padding 0.28s cubic-bezier(0.22, 1, 0.36, 1),
    border-color 0.28s cubic-bezier(0.22, 1, 0.36, 1),
    opacity 0.2s ease,
    transform 0.28s cubic-bezier(0.22, 1, 0.36, 1);
  will-change: opacity, transform;
}

.summary-expand.expanded .summary-more {
  padding: 12px 0 16px;
  border-top-color: #f5f5f5;
  opacity: 1;
  transform: translateY(0);
}

.more-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.more-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  .more-label { color: #8c8c8c; }
  .more-value { 
    color: #262626; font-weight: 600;
    &.is-money { color: #cf1322; }
  }
}
</style>
