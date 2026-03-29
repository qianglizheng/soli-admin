<template>
  <div class="tab-pane-content table-tab-pane">
    <div class="table-toolbar">
      <PurchaseOrderColumnSetting
        v-model="visibleColumnKeys"
        :columns="columnOptions"
        :default-keys="permissionDefaultVisibleColumnKeys"
        :permissions="permissions"
      />
    </div>

    <div class="table-main">
      <el-table
        ref="tableRef"
        :data="rows"
        border
        size="small"
        height="100%"
        @sort-change="handleSortChange"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />

        <el-table-column
          v-if="canShowColumn('fileName')"
          :label="permissionAccess.getFieldLabel('fileName')"
          prop="fileName"
          column-key="fileName"
          min-width="220"
          show-overflow-tooltip
          sortable
          :filters="fileNameFilters"
          :filter-method="filterByFileName"
        />

        <el-table-column
          v-if="canShowColumn('fileType')"
          :label="permissionAccess.getFieldLabel('fileType')"
          prop="fileType"
          column-key="fileType"
          width="120"
          align="center"
          sortable
          :filters="fileTypeFilters"
          :filter-method="filterByFileType"
        >
          <template #default="scope">
            <el-tag size="small" effect="plain">{{ scope.row.fileType }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
          v-if="canShowColumn('fileSize')"
          :label="permissionAccess.getFieldLabel('fileSize')"
          prop="fileSize"
          width="120"
          align="center"
          sortable
          :sort-method="sortByFileSize"
        />

        <el-table-column
          v-if="canShowColumn('uploadUser')"
          :label="permissionAccess.getFieldLabel('uploadUser')"
          prop="uploadUser"
          column-key="uploadUser"
          width="120"
          align="center"
          sortable
          :filters="uploadUserFilters"
          :filter-method="filterByUploadUser"
        />

        <el-table-column
          v-if="canShowColumn('uploadTime')"
          :label="permissionAccess.getFieldLabel('uploadTime')"
          prop="uploadTime"
          width="180"
          align="center"
          sortable
        />

        <el-table-column
          v-if="canShowColumn('remark')"
          :label="permissionAccess.getFieldLabel('remark')"
          prop="remark"
          min-width="180"
          show-overflow-tooltip
          sortable
        />
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue';
import { type TableInstance } from 'element-plus';
import {
  createBillPermissionAccessor,
  isSameStringArray,
  normalizeVisibleKeys,
  type BillPermissionSource
} from '@/components/Bill/billPermission';
import type { PurchaseOrderAttachment } from '@/api/purchaseOrder';
import PurchaseOrderColumnSetting from './PurchaseOrderColumnSetting.vue';
import { buildTextFilters, matchTextFilter, parseFileSize } from './tableHelper';

const props = defineProps<{
  rows: PurchaseOrderAttachment[];
  permissions?: BillPermissionSource;
}>();

const defaultVisibleColumnKeys = ['fileName', 'fileType', 'fileSize', 'uploadUser', 'uploadTime', 'remark'];
const filterableColumnKeys = ['fileName', 'fileType', 'uploadUser'];
const defaultFieldLabels = {
  fileName: '文件名称',
  fileType: '文件类型',
  fileSize: '文件大小',
  uploadUser: '上传人',
  uploadTime: '上传时间',
  remark: '备注'
} as const;
const allColumnKeys = Object.keys(defaultFieldLabels) as Array<keyof typeof defaultFieldLabels>;

const permissionAccess = createBillPermissionAccessor(() => props.permissions, {
  fieldLabels: defaultFieldLabels
});

const tableRef = ref<TableInstance>();
const visibleColumnKeys = ref([...defaultVisibleColumnKeys]);
const currentSortProp = ref('');

const canShowColumn = (key: string) => {
  return visibleColumnKeys.value.includes(key) && permissionAccess.isFieldVisible(key);
};

const columnOptions = computed(() => {
  return allColumnKeys
    .filter((key) => {
      return permissionAccess.isFieldVisible(key);
    })
    .map((key) => {
      return {
        key,
        label: permissionAccess.getFieldLabel(key)
      };
    });
});

const permissionDefaultVisibleColumnKeys = computed(() => {
  return permissionAccess.filterVisibleFields(defaultVisibleColumnKeys);
});

const fileNameFilters = computed(() => {
  return buildTextFilters(props.rows, (item) => {
    return item.fileName;
  });
});

const fileTypeFilters = computed(() => {
  return buildTextFilters(props.rows, (item) => {
    return item.fileType;
  });
});

const uploadUserFilters = computed(() => {
  return buildTextFilters(props.rows, (item) => {
    return item.uploadUser;
  });
});

const filterByFileName = (value: string, row: PurchaseOrderAttachment) => {
  return matchTextFilter(value, row, (item) => {
    return item.fileName;
  });
};

const filterByFileType = (value: string, row: PurchaseOrderAttachment) => {
  return matchTextFilter(value, row, (item) => {
    return item.fileType;
  });
};

const filterByUploadUser = (value: string, row: PurchaseOrderAttachment) => {
  return matchTextFilter(value, row, (item) => {
    return item.uploadUser;
  });
};

const sortByFileSize = (left: PurchaseOrderAttachment, right: PurchaseOrderAttachment) => {
  return parseFileSize(left.fileSize) - parseFileSize(right.fileSize);
};

const handleSortChange = (payload: { prop?: string | null }) => {
  currentSortProp.value = payload.prop ? String(payload.prop) : '';
};

watch([columnOptions, permissionDefaultVisibleColumnKeys], () => {
  const allowedKeys = columnOptions.value.map((item) => {
    return item.key;
  });
  const nextVisibleColumnKeys = normalizeVisibleKeys(
    visibleColumnKeys.value,
    permissionDefaultVisibleColumnKeys.value,
    allowedKeys
  );
  if (!isSameStringArray(nextVisibleColumnKeys, visibleColumnKeys.value)) {
    visibleColumnKeys.value = nextVisibleColumnKeys;
  }
}, { immediate: true, deep: true });

watch(visibleColumnKeys, (value, oldValue) => {
  const hiddenFilterKeys = oldValue.filter((key) => {
    return !value.includes(key) && filterableColumnKeys.includes(key);
  });
  if (hiddenFilterKeys.length) {
    tableRef.value?.clearFilter(hiddenFilterKeys);
  }
  const hiddenKeys = oldValue.filter((key) => {
    return !value.includes(key);
  });
  if (currentSortProp.value && hiddenKeys.includes(currentSortProp.value)) {
    tableRef.value?.clearSort();
    currentSortProp.value = '';
  }
  nextTick(() => {
    tableRef.value?.doLayout();
  });
}, { flush: 'sync' });
</script>

<style scoped lang="scss">
.tab-pane-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.table-tab-pane {
  padding: 12px 16px;
  box-sizing: border-box;
}

.table-toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}

.table-main {
  flex: 1;
  min-height: 0;
}
</style>
