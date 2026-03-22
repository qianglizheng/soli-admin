<template>
  <div class="tab-pane-content table-tab-pane">
    <div class="table-toolbar">
      <BillTemplateColumnSetting
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
          :label="getFieldTitle('fileName')"
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
          :label="getFieldTitle('fileType')"
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
          :label="getFieldTitle('fileSize')"
          prop="fileSize"
          width="120"
          align="center"
          sortable
          :sort-method="sortByFileSize"
        />

        <el-table-column
          v-if="canShowColumn('uploadUser')"
          :label="getFieldTitle('uploadUser')"
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
          :label="getFieldTitle('uploadTime')"
          prop="uploadTime"
          width="180"
          align="center"
          sortable
        />

        <el-table-column
          v-if="canShowColumn('remark')"
          :label="getFieldTitle('remark')"
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
  filterVisibleKeys,
  getFieldLabel,
  isPermissionVisible,
  isSameStringArray,
  normalizeVisibleKeys,
  type BillPermissionSet
} from '@/components/Bill/billPermission';
import BillTemplateColumnSetting from './BillTemplateColumnSetting.vue';
import { buildTextFilters, matchTextFilter, parseFileSize } from './tableHelper';

interface AttachmentRow {
  fileName: string;
  fileType: string;
  fileSize: string;
  uploadUser: string;
  uploadTime: string;
  remark: string;
}

const props = defineProps<{
  rows: AttachmentRow[];
  permissions?: BillPermissionSet;
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

const tableRef = ref<TableInstance>();
const visibleColumnKeys = ref([...defaultVisibleColumnKeys]);
const currentSortProp = ref('');
/**
 * 判断字段是否允许显示。
 */
const isFieldVisible = (key: string) => {
  return isPermissionVisible(props.permissions, 'fields', key);
};

/**
 * 读取字段标题，优先使用权限配置中的自定义标题。
 */
const getFieldTitle = (key: keyof typeof defaultFieldLabels) => {
  return getFieldLabel(props.permissions, key, defaultFieldLabels[key]);
};

/**
 * 判断当前列是否可显示。
 */
const canShowColumn = (key: string) => {
  return visibleColumnKeys.value.includes(key) && isFieldVisible(key);
};

/**
 * 根据字段权限动态生成列配置。
 */
const columnOptions = computed(() => {
  return allColumnKeys
    .filter((key) => {
      return isFieldVisible(key);
    })
    .map((key) => {
      return {
        key,
        label: getFieldTitle(key)
      };
    });
});

/**
 * 根据权限过滤默认可见列。
 */
const permissionDefaultVisibleColumnKeys = computed(() => {
  return filterVisibleKeys(defaultVisibleColumnKeys, props.permissions, 'fields');
});

/**
 * 生成附件名称筛选项。
 */
const fileNameFilters = computed(() => {
  return buildTextFilters(props.rows, (item) => {
    return item.fileName;
  });
});

/**
 * 生成附件类型筛选项。
 */
const fileTypeFilters = computed(() => {
  return buildTextFilters(props.rows, (item) => {
    return item.fileType;
  });
});

/**
 * 生成上传人筛选项。
 */
const uploadUserFilters = computed(() => {
  return buildTextFilters(props.rows, (item) => {
    return item.uploadUser;
  });
});

/**
 * 按附件名称筛选数据。
 */
const filterByFileName = (value: string, row: AttachmentRow) => {
  return matchTextFilter(value, row, (item) => {
    return item.fileName;
  });
};

/**
 * 按附件类型筛选数据。
 */
const filterByFileType = (value: string, row: AttachmentRow) => {
  return matchTextFilter(value, row, (item) => {
    return item.fileType;
  });
};

/**
 * 按上传人筛选数据。
 */
const filterByUploadUser = (value: string, row: AttachmentRow) => {
  return matchTextFilter(value, row, (item) => {
    return item.uploadUser;
  });
};

/**
 * 按附件大小排序。
 */
const sortByFileSize = (left: AttachmentRow, right: AttachmentRow) => {
  return parseFileSize(left.fileSize) - parseFileSize(right.fileSize);
};

/**
 * 记录当前排序列，便于列隐藏时清理排序状态。
 */
const handleSortChange = (payload: { prop?: string | null }) => {
  currentSortProp.value = payload.prop ? String(payload.prop) : '';
};

/**
 * 当字段权限变化时，同步修正列显示设置。
 */
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

/**
 * 当列被隐藏时，清理对应筛选和排序状态。
 */
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
