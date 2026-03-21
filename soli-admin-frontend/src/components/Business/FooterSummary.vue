<template>
  <div class="footer-summary-sticky">
    <div class="footer-summary-card">
      <div class="summary-section">
        <template v-for="(item, index) in items" :key="index">
          <div class="summary-item" :class="item.type">
            <div class="item-icon-box" v-if="item.icon">
              <el-icon><component :is="item.icon" /></el-icon>
            </div>
            <div class="item-content">
              <span class="label">{{ item.label }}</span>
              <span class="value" :class="item.valueClass">
                <span class="currency" v-if="item.isMoney">¥</span>{{ item.value }}
              </span>
            </div>
          </div>
          <div v-if="index < items.length - 1" class="summary-divider"></div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
export interface SummaryItem {
  label: string;
  value: string | number;
  icon?: any;
  type?: string;
  valueClass?: string;
  isMoney?: boolean;
}

interface Props {
  items: SummaryItem[];
}

defineProps<Props>();
</script>

<style scoped lang="scss">
.footer-summary-sticky {
  flex-shrink: 0;
}

.footer-summary-card {
  background: #fff;
  border-top: 1px solid var(--app-border-color);
  padding: 8px 24px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  position: relative;
  
  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 4px;
    height: 100%;
    background: var(--app-primary);
  }
}

.summary-section {
  display: flex;
  align-items: center;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 0;

  .item-icon-box {
    width: 32px;
    height: 32px;
    background: #f0f7ff;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--app-primary);
    font-size: 18px;
  }

  /* 不同类型的颜色主题 */
  &.amount, &.payments, &.balance, &.danger {
    .item-icon-box {
      background: #fff2f0;
      color: #ff4d4f;
    }
    .value {
      color: #cf1322;
    }
  }

  .item-content {
    display: flex;
    flex-direction: column;
    
    .label {
      font-size: 12px;
      color: #8c8c8c;
      margin-bottom: 2px;
    }
    
    .value {
      font-size: 18px;
      font-weight: 700;
      font-family: 'Consolas', monospace;
      color: #262626;
      line-height: 1;

      .currency {
        font-size: 13px;
        margin-right: 2px;
        font-weight: 600;
      }
    }
  }
}

.summary-divider {
  width: 1px;
  height: 24px;
  background: #f0f0f0;
  margin: 0 40px;
}
</style>
