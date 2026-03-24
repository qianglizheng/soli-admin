<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="1120px" top="4vh" destroy-on-close>
    <div v-if="postId" class="employee-dialog">
      <el-alert
        v-if="mode === 'assign'"
        title="左侧是可选员工，右侧是已分配员工，两侧都按分页实时加载，不再一次性拉全量数据。"
        type="info"
        :closable="false"
        show-icon
      />

      <div v-if="mode === 'assign'" class="dual-panel">
        <section class="panel-card">
          <div class="panel-card__header">
            <span>可选员工</span>
            <div class="panel-card__tools">
              <el-input
                v-model="optionKeyword"
                clearable
                placeholder="搜索账号 / 姓名 / 手机"
                style="width: 240px"
                @keyup.enter="handleOptionSearch"
                @clear="handleOptionSearch"
              />
              <el-button type="primary" plain @click="handleOptionSearch">搜索</el-button>
            </div>
          </div>

          <el-table
            ref="optionTableRef"
            v-loading="optionLoading"
            :data="optionResult.list"
            border
            height="420"
            @selection-change="handleOptionSelectionChange"
          >
            <el-table-column type="selection" width="50" />
            <el-table-column prop="username" label="账号" min-width="130" />
            <el-table-column prop="nickname" label="姓名" min-width="120" />
            <el-table-column prop="phone" label="手机号" min-width="140" />
            <el-table-column label="状态" width="90" align="center">
              <template #default="scope">
                <el-tag size="small" :type="scope.row.status === '0' ? 'success' : 'danger'" effect="plain">
                  {{ scope.row.status === '0' ? '启用' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <div class="panel-card__footer">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :current-page="optionResult.pageNum"
              :page-size="optionResult.pageSize"
              :total="optionResult.total"
              @current-change="handleOptionPageChange"
            />
            <el-button type="primary" :disabled="!selectedOptionUserIds.length" :loading="actionLoading" @click="handleBindUsers">
              添加已选
            </el-button>
          </div>
        </section>

        <section class="panel-card">
          <div class="panel-card__header">
            <span>已分配员工</span>
            <div class="panel-card__tools">
              <el-input
                v-model="assignedKeyword"
                clearable
                placeholder="搜索账号 / 姓名 / 手机"
                style="width: 240px"
                @keyup.enter="handleAssignedSearch"
                @clear="handleAssignedSearch"
              />
              <el-button plain @click="handleAssignedSearch">搜索</el-button>
            </div>
          </div>

          <el-table v-loading="assignedLoading" :data="assignedResult.list" border height="420">
            <el-table-column prop="username" label="账号" min-width="130" />
            <el-table-column prop="nickname" label="姓名" min-width="120" />
            <el-table-column prop="phone" label="手机号" min-width="140" />
            <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
            <el-table-column label="状态" width="90" align="center">
              <template #default="scope">
                <el-tag size="small" :type="scope.row.status === '0' ? 'success' : 'danger'" effect="plain">
                  {{ scope.row.status === '0' ? '启用' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="110" fixed="right" align="center">
              <template #default="scope">
                <el-button link type="danger" :loading="actionLoading" @click="handleUnbindUsers([scope.row.userId])">解除分配</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="panel-card__footer">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :current-page="assignedResult.pageNum"
              :page-size="assignedResult.pageSize"
              :total="assignedResult.total"
              @current-change="handleAssignedPageChange"
            />
          </div>
        </section>
      </div>

      <div v-else class="single-panel">
        <div class="panel-card">
          <div class="panel-card__header">
            <span>岗位员工列表</span>
            <div class="panel-card__tools">
              <el-input
                v-model="assignedKeyword"
                clearable
                placeholder="搜索账号 / 姓名 / 手机"
                style="width: 260px"
                @keyup.enter="handleAssignedSearch"
                @clear="handleAssignedSearch"
              />
              <el-button type="primary" plain @click="handleAssignedSearch">搜索</el-button>
            </div>
          </div>

          <el-table v-loading="assignedLoading" :data="assignedResult.list" border height="460">
            <el-table-column prop="username" label="账号" min-width="130" />
            <el-table-column prop="nickname" label="姓名" min-width="120" />
            <el-table-column prop="phone" label="手机号" min-width="140" />
            <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
            <el-table-column label="状态" width="90" align="center">
              <template #default="scope">
                <el-tag size="small" :type="scope.row.status === '0' ? 'success' : 'danger'" effect="plain">
                  {{ scope.row.status === '0' ? '启用' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="110" fixed="right" align="center">
              <template #default="scope">
                <el-button link type="danger" :loading="actionLoading" @click="handleUnbindUsers([scope.row.userId])">解除分配</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="panel-card__footer panel-card__footer--single">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :current-page="assignedResult.pageNum"
              :page-size="assignedResult.pageSize"
              :total="assignedResult.total"
              @current-change="handleAssignedPageChange"
            />
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { bindOrgPostUsers, getOrgPostUserOptionPage, getOrgPostUserPage, unbindOrgPostUsers, type OrgPostUser } from '@/api/orgPost';
import type { PageResult } from '@/types/global';

interface Props {
  modelValue: boolean;
  mode: 'assign' | 'view';
  postId?: number;
  postName?: string;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submit'): void;
  (e: 'cancel'): void;
}>();

const createEmptyPageResult = (): PageResult<OrgPostUser> => ({
  list: [],
  pageNum: 1,
  pageSize: 10,
  total: 0
});

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const optionTableRef = ref();
const optionKeyword = ref('');
const assignedKeyword = ref('');
const optionLoading = ref(false);
const assignedLoading = ref(false);
const actionLoading = ref(false);
const selectedOptionUserIds = ref<number[]>([]);
const optionResult = ref<PageResult<OrgPostUser>>(createEmptyPageResult());
const assignedResult = ref<PageResult<OrgPostUser>>(createEmptyPageResult());

watch(
  () => [props.modelValue, props.postId, props.mode],
  ([open, postId]) => {
    if (!open || !postId) {
      return;
    }
    optionKeyword.value = '';
    assignedKeyword.value = '';
    selectedOptionUserIds.value = [];
    optionResult.value = createEmptyPageResult();
    assignedResult.value = createEmptyPageResult();
    void loadAssignedUsers(1);
    if (props.mode === 'assign') {
      void loadOptionUsers(1);
    }
  },
  { immediate: true }
);

const dialogTitle = computed(() => {
  if (props.mode === 'assign') {
    return `给岗位分配员工${props.postName ? ` - ${props.postName}` : ''}`;
  }
  return `岗位员工列表${props.postName ? ` - ${props.postName}` : ''}`;
});

const loadOptionUsers = async (pageNum = optionResult.value.pageNum || 1) => {
  if (!props.postId) {
    return;
  }
  optionLoading.value = true;
  try {
    const { data } = await getOrgPostUserOptionPage({
      keyword: optionKeyword.value || undefined,
      orgPostId: props.postId,
      pageNum,
      pageSize: 8
    });
    optionResult.value = data;
  } finally {
    optionLoading.value = false;
  }
};

const loadAssignedUsers = async (pageNum = assignedResult.value.pageNum || 1) => {
  if (!props.postId) {
    return;
  }
  assignedLoading.value = true;
  try {
    const { data } = await getOrgPostUserPage({
      keyword: assignedKeyword.value || undefined,
      orgPostId: props.postId,
      pageNum,
      pageSize: props.mode === 'assign' ? 8 : 10
    });
    assignedResult.value = data;
  } finally {
    assignedLoading.value = false;
  }
};

const handleOptionSelectionChange = (rows: OrgPostUser[]) => {
  selectedOptionUserIds.value = rows.map((item) => item.userId);
};

const handleOptionSearch = () => {
  void loadOptionUsers(1);
};

const handleAssignedSearch = () => {
  void loadAssignedUsers(1);
};

const handleOptionPageChange = (pageNum: number) => {
  void loadOptionUsers(pageNum);
};

const handleAssignedPageChange = (pageNum: number) => {
  void loadAssignedUsers(pageNum);
};

const handleBindUsers = async () => {
  if (!props.postId || !selectedOptionUserIds.value.length) {
    return;
  }
  actionLoading.value = true;
  try {
    await bindOrgPostUsers({
      orgPostId: props.postId,
      userIds: [...selectedOptionUserIds.value]
    });
    ElMessage.success('岗位员工分配成功');
    emit('submit');
    selectedOptionUserIds.value = [];
    optionTableRef.value?.clearSelection();
    await Promise.all([loadOptionUsers(1), loadAssignedUsers(1)]);
  } finally {
    actionLoading.value = false;
  }
};

const handleUnbindUsers = async (userIds: number[]) => {
  if (!props.postId || !userIds.length) {
    return;
  }
  try {
    await ElMessageBox.confirm('确定解除当前员工与岗位的分配关系吗？', '提示', {
      type: 'warning'
    });
  } catch {
    return;
  }
  actionLoading.value = true;
  try {
    await unbindOrgPostUsers({
      orgPostId: props.postId,
      userIds
    });
    ElMessage.success('岗位员工已解除分配');
    emit('submit');
    await Promise.all([
      loadAssignedUsers(assignedResult.value.pageNum || 1),
      props.mode === 'assign' ? loadOptionUsers(optionResult.value.pageNum || 1) : Promise.resolve()
    ]);
  } finally {
    actionLoading.value = false;
  }
};

const handleClose = () => {
  emit('cancel');
  visible.value = false;
};
</script>

<style scoped>
.employee-dialog,
.single-panel,
.panel-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dual-panel {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.panel-card {
  min-width: 0;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 12px;
  background: var(--el-bg-color);
  padding: 16px;
}

.panel-card__header,
.panel-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.panel-card__tools {
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel-card__footer--single {
  justify-content: flex-end;
}

@media (max-width: 1280px) {
  .dual-panel {
    grid-template-columns: 1fr;
  }

  .panel-card__header,
  .panel-card__footer {
    flex-direction: column;
    align-items: flex-start;
  }

  .panel-card__tools {
    width: 100%;
    flex-wrap: wrap;
  }
}
</style>
