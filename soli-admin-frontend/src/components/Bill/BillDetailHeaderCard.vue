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
              v-if="permissionAccess.isButtonVisible('save')"
              split-button
              type="primary"
              :disabled="permissionAccess.isButtonReadonly('save')"
              @click="handleSaveClick"
              @command="handleSaveCommand"
            >
              <el-icon><DocumentChecked /></el-icon>&nbsp;{{ permissionAccess.getButtonLabel('save') }}
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="item in saveOptions" :key="item.command" :command="item.command">
                    {{ item.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <el-button
              v-if="permissionAccess.isButtonVisible('audit')"
              icon="Check"
              :disabled="permissionAccess.isButtonReadonly('audit')"
              @click="$emit('audit')"
            >
              {{ permissionAccess.getButtonLabel('audit') }}
            </el-button>
            <el-button
              v-if="permissionAccess.isButtonVisible('print')"
              icon="Printer"
              :disabled="permissionAccess.isButtonReadonly('print')"
              @click="$emit('print')"
            >
              {{ permissionAccess.getButtonLabel('print') }}
            </el-button>

            <el-dropdown v-if="hasVisibleMoreActions" trigger="click" @command="handleMoreCommand">
              <el-button icon="MoreFilled" :disabled="moreActionsReadonly">更多操作</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-if="permissionAccess.isButtonVisible('log')"
                    command="log"
                    icon="Files"
                    :disabled="permissionAccess.isButtonReadonly('log')"
                  >
                    {{ permissionAccess.getButtonLabel('log') }}
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="permissionAccess.isButtonVisible('copy')"
                    command="copy"
                    icon="CopyDocument"
                    :disabled="permissionAccess.isButtonReadonly('copy')"
                  >
                    {{ permissionAccess.getButtonLabel('copy') }}
                  </el-dropdown-item>
                  <el-divider v-if="showMoreActionDivider" style="margin: 4px 0" />
                  <el-dropdown-item
                    v-if="permissionAccess.isButtonVisible('void')"
                    command="void"
                    icon="CircleClose"
                    type="danger"
                    :disabled="permissionAccess.isButtonReadonly('void')"
                  >
                    {{ permissionAccess.getButtonLabel('void') }}
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
  createBillPermissionAccessor,
  type BillPermissionSource
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
  permissions?: BillPermissionSource;
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
const permissionAccess = createBillPermissionAccessor(() => props.permissions, {
  buttonLabels: {
    save: '保存',
    audit: '审核通过',
    print: '打印',
    log: '操作日志',
    copy: '复制单据',
    void: '作废单据'
  }
});

/**
 * 判断头部是否还有可见操作按钮。
 */
const hasVisibleHeaderActions = computed(() => {
  return permissionAccess.hasVisibleButtons(headerActionKeys);
});

/**
 * 判断“更多操作”菜单是否仍有可见项。
 */
const hasVisibleMoreActions = computed(() => {
  return permissionAccess.hasVisibleButtons(moreActionKeys);
});

/**
 * 当更多操作中的全部按钮都只读时，禁用更多操作按钮。
 */
const moreActionsReadonly = computed(() => {
  const visibleKeys = moreActionKeys.filter((key) => {
    return permissionAccess.isButtonVisible(key);
  });
  return visibleKeys.length > 0 && visibleKeys.every((key) => {
    return permissionAccess.isButtonReadonly(key);
  });
});

/**
 * 控制更多操作中的分割线显示。
 */
const showMoreActionDivider = computed(() => {
  return permissionAccess.isButtonVisible('void')
    && (permissionAccess.isButtonVisible('log') || permissionAccess.isButtonVisible('copy'));
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
