<template>
  <div class="tech-card detail-header">
    <el-page-header @back="$emit('back')" class="custom-header">
      <template #content>
        <div class="header-info">
          <span class="bill-title">{{ title }}</span>
          <span v-if="billNo" class="bill-no">{{ billNo }}</span>
          <el-tag v-if="statusName" :type="statusType" effect="plain" size="small" class="status-tag">
            {{ statusName }}
          </el-tag>
        </div>
      </template>
      <template #extra>
        <div class="right-actions">
          <div v-if="showActions && hasVisibleHeaderActions" class="bill-header-actions">
            <el-dropdown
              v-if="isButtonVisible('save')"
              split-button
              type="primary"
              :disabled="isButtonReadonly('save')"
              @click="handleSaveClick"
              @command="handleSaveCommand"
            >
              <el-icon><DocumentChecked /></el-icon>&nbsp;保存
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="item in saveOptions" :key="item.command" :command="item.command">
                    {{ item.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <el-button
              v-if="isButtonVisible('audit')"
              icon="Check"
              :disabled="isButtonReadonly('audit')"
              @click="$emit('audit')"
            >
              审核通过
            </el-button>
            <el-button
              v-if="isButtonVisible('print')"
              icon="Printer"
              :disabled="isButtonReadonly('print')"
              @click="$emit('print')"
            >
              打印
            </el-button>

            <el-dropdown v-if="hasVisibleMoreActions" trigger="click" @command="handleMoreCommand">
              <el-button icon="MoreFilled" :disabled="moreActionsReadonly">更多操作</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-if="isButtonVisible('log')"
                    command="log"
                    icon="Files"
                    :disabled="isButtonReadonly('log')"
                  >
                    操作日志
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="isButtonVisible('copy')"
                    command="copy"
                    icon="CopyDocument"
                    :disabled="isButtonReadonly('copy')"
                  >
                    复制单据
                  </el-dropdown-item>
                  <el-divider v-if="showMoreActionDivider" style="margin: 4px 0" />
                  <el-dropdown-item
                    v-if="isButtonVisible('void')"
                    command="void"
                    icon="CircleClose"
                    type="danger"
                    :disabled="isButtonReadonly('void')"
                  >
                    作废单据
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <slot name="extra"></slot>
        </div>
      </template>
    </el-page-header>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { DocumentChecked } from '@element-plus/icons-vue';
import {
  hasAnyVisiblePermission,
  isPermissionReadonly,
  isPermissionVisible,
  type BillPermissionSet
} from './billPermission';

interface SaveOption {
  command: string;
  label: string;
}

interface Props {
  title: string;
  billNo?: string;
  statusName?: string;
  statusType?: string;
  showActions?: boolean;
  permissions?: BillPermissionSet;
  saveOptions?: SaveOption[];
}

const props = withDefaults(defineProps<Props>(), {
  showActions: false,
  saveOptions: () => ([
    { command: '0', label: '保存为未审核 (暂存)' },
    { command: '1', label: '保存并提交审核' },
    { command: '2', label: '保存并直接过账' }
  ])
});

const emit = defineEmits<{
  back: []
  save: [command: string]
  audit: []
  print: []
  'more-action': [command: string]
}>();

const moreActionKeys = ['log', 'copy', 'void'];
const headerActionKeys = ['save', 'audit', 'print', ...moreActionKeys];

/**
 * 判断按钮是否允许显示。
 */
const isButtonVisible = (key: string) => {
  return isPermissionVisible(props.permissions, 'buttons', key);
};

/**
 * 判断按钮是否处于只读状态。
 */
const isButtonReadonly = (key: string) => {
  return isPermissionReadonly(props.permissions, 'buttons', key);
};

/**
 * 判断头部是否还有可见操作按钮。
 */
const hasVisibleHeaderActions = computed(() => {
  return hasAnyVisiblePermission(props.permissions, 'buttons', headerActionKeys);
});

/**
 * 判断“更多操作”菜单是否仍有可见项。
 */
const hasVisibleMoreActions = computed(() => {
  return hasAnyVisiblePermission(props.permissions, 'buttons', moreActionKeys);
});

/**
 * 当更多操作中的全部按钮都只读时，禁用更多操作按钮。
 */
const moreActionsReadonly = computed(() => {
  const visibleKeys = moreActionKeys.filter((key) => {
    return isButtonVisible(key);
  });
  return visibleKeys.length > 0 && visibleKeys.every((key) => {
    return isButtonReadonly(key);
  });
});

/**
 * 控制更多操作中的分割线显示。
 */
const showMoreActionDivider = computed(() => {
  return isButtonVisible('void') && (isButtonVisible('log') || isButtonVisible('copy'));
});

/**
 * 点击主保存按钮时，默认按暂存处理。
 */
const handleSaveClick = () => {
  emit('save', '0');
};

/**
 * 处理保存下拉菜单命令。
 */
const handleSaveCommand = (command: string | number) => {
  emit('save', String(command));
};

/**
 * 处理更多操作命令。
 */
const handleMoreCommand = (command: string | number) => {
  emit('more-action', String(command));
};
</script>

<style scoped lang="scss">
.detail-header {
  flex-shrink: 0;
  padding: 6px 16px !important;
  margin: 0 !important;
}

.custom-header {
  width: 100%;
  :deep(.el-page-header__header) {
    align-items: center;
    height: 32px;
  }
  :deep(.el-page-header__left) {
    margin-right: 12px;
    .el-page-header__back {
      font-size: 13px;
    }
    &::after {
      content: "";
      width: 1px;
      height: 14px;
      background-color: var(--app-border-color);
      margin-left: 12px;
    }
  }
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bill-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.bill-no {
  font-family: 'Consolas', monospace;
  font-size: 12px;
  color: var(--el-text-color-regular);
  background: var(--app-bg-color);
  padding: 1px 6px;
  border-radius: 4px;
  border: 1px solid var(--app-border-color);
}

.status-tag {
  font-weight: 600;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.bill-header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
