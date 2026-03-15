<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="600px" destroy-on-close>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="菜单名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入菜单名称" />
      </el-form-item>
      <el-form-item label="父菜单" prop="parentId">
        <el-tree-select
          v-model="form.parentId"
          :data="treeOptions"
          :props="treeSelectProps"
          filterable
          check-strictly
          placeholder="请选择父菜单"
          style="width: 100%;"
        />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input v-model="form.sort" placeholder="排序值" />
      </el-form-item>
      <el-form-item label="路由地址" prop="path">
        <el-input v-model="form.path" placeholder="如 /system/menu" />
      </el-form-item>
      <el-form-item label="组件地址" prop="component">
        <el-input v-model="form.component" placeholder="如 system/menu/index" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="form.type" placeholder="请选择类型">
          <el-option label="目录" value="0" />
          <el-option label="菜单" value="1" />
          <el-option label="按钮" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="权限代码" prop="perms">
        <el-input v-model="form.perms" placeholder="如 system:menu:add" />
      </el-form-item>
      <el-form-item label="图标" prop="icon">
        <el-input v-model="form.icon" placeholder="ElementPlus 图标名" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="form.status" placeholder="请选择状态">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import type { SysMenuDTO } from '@/types/global';

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<SysMenuDTO>;
  treeData?: SysMenuDTO[];
  currentId?: number;
}

const props = defineProps<Props>();
const emit = defineEmits(['update:modelValue', 'submit', 'cancel']);

const visible = computed({
  get: () => props.modelValue,
  set: (val: boolean) => emit('update:modelValue', val),
});

const formRef = ref();
const form = reactive<Partial<SysMenuDTO>>({
  name: '',
  parentId: 0,
  sort: '',
  path: '',
  component: '',
  type: '1',
  perms: '',
  icon: '',
  status: '0'
});

const rules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
} as any;

const treeSelectProps = {
  value: 'id',
  label: 'name',
  children: 'children',
  disabled: 'disabled'
};

const filterByType = (nodes: SysMenuDTO[] | undefined): SysMenuDTO[] => {
  if (!nodes) return [];
  const res: SysMenuDTO[] = [];
  for (const n of nodes) {
    if (n.type !== '2') {
      res.push({
        ...n,
        children: filterByType(n.children)
      });
    }
  }
  return res;
};

const markDisabled = (nodes: SysMenuDTO[] | undefined, currentId?: number): any[] => {
  if (!nodes) return [];
  return nodes.map(n => ({
    ...n,
    disabled: currentId !== undefined && n.id === currentId,
    children: markDisabled(n.children, currentId)
  }));
};

const treeOptions = computed(() => {
  const filtered = filterByType(props.treeData);
  const children = markDisabled(filtered, props.currentId);
  return [{
    id: 0,
    name: '顶级菜单',
    children
  } as any];
});

watch(() => props.initialData, (val) => {
  if (val) {
    Object.assign(form, val);
  }
}, { immediate: true });

const dialogTitle = computed(() => props.mode === 'create' ? '新增菜单' : '修改菜单');

const handleCancel = () => {
  emit('cancel');
  visible.value = false;
};

const handleSubmit = async () => {
  await formRef.value.validate();
  emit('submit', { ...form });
  visible.value = false;
};
</script>
