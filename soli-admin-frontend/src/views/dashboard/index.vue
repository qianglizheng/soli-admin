<template>
  <div class="app-container dashboard-page">
    <section class="hero-card">
      <div class="hero-main">
        <div class="hero-eyebrow">工作台总览</div>
        <div class="hero-title">{{ greetingText }}，{{ displayName }}</div>
        <div class="hero-subtitle">
          {{ todayLabel }}。当前账号已开放 {{ accessiblePageCount }} 个页面入口，权限平台核心工作台已接入
          {{ permissionWorkbenchCount }} / {{ permissionWorkbenchTotal }}。
        </div>
        <div class="hero-tags">
          <el-tag v-for="role in roleLabels" :key="role" effect="plain" type="primary">
            {{ role }}
          </el-tag>
          <el-tag effect="plain" type="success">已接入 {{ completedTodoCount }} 个入口</el-tag>
          <el-tag effect="plain" type="warning">待开通 {{ unfinishedTodoCount }} 个入口</el-tag>
        </div>
        <div class="hero-actions">
          <el-button
            v-if="primaryShortcut"
            type="primary"
            :icon="ArrowRight"
            @click="handleShortcutClick(primaryShortcut)"
          >
            进入{{ primaryShortcut.title }}
          </el-button>
          <el-button
            v-if="secondaryShortcut"
            plain
            :icon="Position"
            @click="handleShortcutClick(secondaryShortcut)"
          >
            查看{{ secondaryShortcut.title }}
          </el-button>
        </div>
      </div>

      <div class="hero-side">
        <div v-for="item in heroHighlights" :key="item.label" class="hero-highlight">
          <div class="hero-highlight__label">{{ item.label }}</div>
          <div class="hero-highlight__value">{{ item.value }}</div>
          <div class="hero-highlight__note">{{ item.note }}</div>
        </div>
      </div>
    </section>

    <el-row :gutter="16" class="overview-row">
      <el-col v-for="item in overviewCards" :key="item.key" :xl="6" :lg="12" :md="12" :sm="12" :xs="24">
        <div class="tech-card overview-card">
          <div class="overview-card__icon" :class="`is-${item.tone}`">
            <el-icon>
              <component :is="item.icon" />
            </el-icon>
          </div>
          <div class="overview-card__content">
            <div class="overview-card__label">{{ item.label }}</div>
            <div class="overview-card__value">{{ item.value }}</div>
            <div class="overview-card__note">{{ item.note }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="analytics-row">
      <el-col :xl="16" :lg="16" :md="24" :sm="24" :xs="24">
        <div class="tech-card analytics-card">
          <div class="section-header">
            <div>
              <div class="section-title">页面覆盖分布</div>
              <div class="section-subtitle">按工作域统计当前账号可访问页面数量</div>
            </div>
          </div>
          <v-chart class="chart chart--large" :option="moduleBarOption" autoresize />
        </div>
      </el-col>

      <el-col :xl="8" :lg="8" :md="24" :sm="24" :xs="24">
        <div class="tech-card analytics-card">
          <div class="section-header">
            <div>
              <div class="section-title">核心入口接入情况</div>
              <div class="section-subtitle">根据当前账号权限实时统计核心工作台开通进度</div>
            </div>
          </div>
          <v-chart class="chart chart--small" :option="todoPieOption" autoresize />
          <div class="todo-summary">
            <div class="todo-summary__item">
              <span>待开通</span>
              <strong>{{ unfinishedTodoCount }}</strong>
            </div>
            <div class="todo-summary__item">
              <span>已接入</span>
              <strong>{{ completedTodoCount }}</strong>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="workspace-row">
      <el-col :xl="15" :lg="14" :md="24" :sm="24" :xs="24">
        <div class="tech-card workspace-card">
          <div class="section-header">
            <div>
              <div class="section-title">快捷入口</div>
              <div class="section-subtitle">直接进入当前账号已接入的核心工作台</div>
            </div>
          </div>
          <div class="shortcut-grid">
            <button
              v-for="item in shortcutCards"
              :key="item.key"
              type="button"
              class="shortcut-card"
              :class="{ 'is-disabled': !item.available }"
              @click="handleShortcutClick(item)"
            >
              <div class="shortcut-card__head">
                <div class="shortcut-card__icon">
                  <el-icon>
                    <component :is="item.icon" />
                  </el-icon>
                </div>
                <el-tag size="small" :type="item.available ? 'success' : 'info'" effect="plain">
                  {{ item.available ? '已开放' : '未授权' }}
                </el-tag>
              </div>
              <div class="shortcut-card__title">{{ item.title }}</div>
              <div class="shortcut-card__desc">{{ item.description }}</div>
              <div class="shortcut-card__path">{{ item.path }}</div>
            </button>
          </div>
        </div>
      </el-col>

      <el-col :xl="9" :lg="10" :md="24" :sm="24" :xs="24">
        <div class="column-stack">
          <div class="tech-card workspace-card">
            <div class="section-header">
              <div>
                <div class="section-title">接入清单</div>
                <div class="section-subtitle">根据当前账号可访问路由自动生成核心工作台接入结果</div>
              </div>
            </div>
            <div class="todo-list">
              <div v-for="item in todoList" :key="item.title" class="todo-item">
                <div class="todo-item__main">
                  <el-checkbox :model-value="item.done" disabled>{{ item.title }}</el-checkbox>
                  <div class="todo-item__meta">{{ item.category }}</div>
                </div>
                <el-tag size="small" :type="item.tagType" effect="plain">{{ item.tag }}</el-tag>
              </div>
            </div>
          </div>

          <div class="tech-card workspace-card">
            <div class="section-header">
              <div>
                <div class="section-title">核心模块状态</div>
                <div class="section-subtitle">首页直接反映重点工作台的可用情况</div>
              </div>
            </div>
            <div class="status-list">
              <div v-for="item in statusList" :key="item.title" class="status-item">
                <div class="status-item__main">
                  <span class="status-dot" :class="{ active: item.available }"></span>
                  <div>
                    <div class="status-item__title">{{ item.title }}</div>
                    <div class="status-item__desc">{{ item.description }}</div>
                  </div>
                </div>
                <el-tag size="small" :type="item.available ? 'success' : 'info'" effect="plain">
                  {{ item.available ? '可进入' : '待授权' }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, type Component } from 'vue';
import { useRouter, type RouteRecordRaw } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  ArrowRight,
  Collection,
  DataAnalysis,
  Document,
  EditPen,
  Grid,
  Lock,
  OfficeBuilding,
  Position,
  SetUp,
  UserFilled
} from '@element-plus/icons-vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { BarChart, PieChart } from 'echarts/charts';
import {
  GridComponent,
  LegendComponent,
  TooltipComponent
} from 'echarts/components';
import VChart from 'vue-echarts';
import { useUserStore } from '@/store/modules/user';
import { usePermissionStore } from '@/store/modules/permission';

defineOptions({
  name: 'Dashboard'
});

use([
  CanvasRenderer,
  BarChart,
  PieChart,
  GridComponent,
  LegendComponent,
  TooltipComponent
]);

interface OverviewCard {
  key: string;
  icon: Component;
  label: string;
  note: string;
  tone: 'primary' | 'success' | 'warning' | 'danger';
  value: number;
}

interface ShortcutCard {
  key: string;
  icon: Component;
  title: string;
  description: string;
  path: string;
  available: boolean;
}

interface TodoItem {
  title: string;
  done: boolean;
  tag: string;
  tagType: 'danger' | 'warning' | 'success' | 'info';
  category: string;
}

interface RouteLeaf {
  path: string;
}

const router = useRouter();
const userStore = useUserStore();
const permissionStore = usePermissionStore();

const roleLabelMap: Record<string, string> = {
  admin: '超级管理员'
};

const shortcutSeeds: Omit<ShortcutCard, 'available'>[] = [
  {
    key: 'module-center',
    icon: Grid,
    title: '模块管理',
    description: '维护模块、Tab、字段、按钮、状态与流转定义，并同步关联资源。',
    path: '/system/module-center'
  },
  {
    key: 'post-manage',
    icon: OfficeBuilding,
    title: '岗位管理',
    description: '查看组织岗位树、岗位摘要和员工挂靠关系。',
    path: '/system/post-manage'
  },
  {
    key: 'function-auth',
    icon: Lock,
    title: '功能授权',
    description: '配置岗位基线权限，控制模块、字段与按钮能力。',
    path: '/system/function-auth'
  },
  {
    key: 'module-title',
    icon: EditPen,
    title: '字段标题',
    description: '统一维护字段显示标题、占位提示和帮助说明。',
    path: '/system/module-title'
  },
  {
    key: 'state-auth',
    icon: SetUp,
    title: '状态权限',
    description: '按状态维度维护收紧规则，不放大岗位基线。',
    path: '/system/state-auth'
  },
  {
    key: 'bill-template',
    icon: Document,
    title: '单据模板',
    description: '进入采购单据模板页，查看实际业务页面联动效果。',
    path: '/purchase/bill-template'
  }
];

const displayName = computed(() => userStore.name || '管理员');

const roleLabels = computed(() => {
  const sourceRoles = userStore.roles.length ? userStore.roles : ['admin'];
  return sourceRoles.map((role) => roleLabelMap[role] || role);
});

const greetingText = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) {
    return '凌晨好';
  }
  if (hour < 12) {
    return '上午好';
  }
  if (hour < 18) {
    return '下午好';
  }
  return '晚上好';
});

const todayLabel = computed(() => {
  return new Intl.DateTimeFormat('zh-CN', {
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  }).format(new Date());
});

const accessiblePages = computed(() => collectVisibleLeafRoutes(permissionStore.routes));

const accessiblePageCount = computed(() => accessiblePages.value.length);

const accessiblePathSet = computed(() => new Set(accessiblePages.value.map((item) => item.path)));

const shortcutCards = computed<ShortcutCard[]>(() => {
  return shortcutSeeds.map((item) => ({
    ...item,
    available: accessiblePathSet.value.has(item.path)
  }));
});

const availableShortcutCards = computed(() => {
  return shortcutCards.value.filter((item) => item.available);
});

const primaryShortcut = computed(() => availableShortcutCards.value[0]);

const secondaryShortcut = computed(() => availableShortcutCards.value[1]);

const permissionWorkbenchCount = computed(() => {
  return shortcutCards.value.filter((item) => item.available && item.path.startsWith('/system/')).length;
});

const permissionWorkbenchTotal = computed(() => {
  return shortcutCards.value.filter((item) => item.path.startsWith('/system/')).length;
});

const unfinishedTodoCount = computed(() => {
  return todoList.value.filter((item) => !item.done).length;
});

const completedTodoCount = computed(() => {
  return todoList.value.length - unfinishedTodoCount.value;
});

const todoList = computed<TodoItem[]>(() => {
  return shortcutCards.value.map((item) => ({
    category: item.path.startsWith('/system/') ? '权限平台' : '业务模块',
    done: item.available,
    tag: item.available ? '已接入' : '待开通',
    tagType: item.available ? 'success' : 'warning',
    title: item.available ? `${item.title}入口已开通` : `待为当前账号开通${item.title}入口`
  }));
});

const moduleCoverageBuckets = computed(() => {
  const bucketDefinitions = [
    {
      color: '#1677ff',
      label: '首页入口',
      match: (path: string) => path === '/dashboard'
    },
    {
      color: '#13c2c2',
      label: '权限平台',
      match: (path: string) => [
        '/system/module-center',
        '/system/post-manage',
        '/system/function-auth',
        '/system/module-title',
        '/system/state-auth'
      ].includes(path)
    },
    {
      color: '#52c41a',
      label: '基础配置',
      match: (path: string) => [
        '/system/user',
        '/system/role',
        '/system/menu',
        '/system/dict',
        '/system/config'
      ].includes(path)
    },
    {
      color: '#faad14',
      label: '日志审计',
      match: (path: string) => [
        '/system/log/operlog',
        '/system/log/logininfor'
      ].includes(path)
    },
    {
      color: '#722ed1',
      label: '业务模块',
      match: (path: string) => path.startsWith('/purchase/')
    }
  ];

  return bucketDefinitions.map((item) => ({
    color: item.color,
    count: accessiblePages.value.filter((page) => item.match(page.path)).length,
    label: item.label
  })).filter((item) => item.count > 0);
});

const heroHighlights = computed(() => {
  return [
    {
      label: '可访问页面',
      note: '依据当前账号路由权限自动统计',
      value: `${accessiblePageCount.value}`
    },
    {
      label: '快捷入口',
      note: '可直接跳转的重点工作台数量',
      value: `${availableShortcutCards.value.length}`
    },
    {
      label: '角色数量',
      note: '当前账号已挂载角色',
      value: `${roleLabels.value.length}`
    },
    {
      label: '待办事项',
      note: '尚未完成的首页任务清单',
      value: `${unfinishedTodoCount.value}`
    }
  ];
});

const overviewCards = computed<OverviewCard[]>(() => {
  return [
    {
      key: 'page-count',
      icon: Collection,
      label: '可访问页面',
      note: '按当前账号实际可进入页面统计',
      tone: 'primary',
      value: accessiblePageCount.value
    },
    {
      key: 'permission-workbench',
      icon: Grid,
      label: '权限工作台',
      note: '模块、岗位、功能、字段标题、状态五类工作台',
      tone: 'success',
      value: permissionWorkbenchCount.value
    },
    {
      key: 'todo-count',
      icon: DataAnalysis,
      label: '进行中事项',
      note: '当前首页待办清单中未完成项',
      tone: 'warning',
      value: unfinishedTodoCount.value
    },
    {
      key: 'role-count',
      icon: UserFilled,
      label: '当前角色',
      note: '账号在本系统内已挂载角色数量',
      tone: 'danger',
      value: roleLabels.value.length
    }
  ];
});

const moduleBarOption = computed(() => {
  return {
    grid: {
      bottom: 24,
      left: 20,
      right: 20,
      top: 12,
      containLabel: true
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      axisLabel: {
        color: '#64748b'
      },
      axisLine: {
        lineStyle: {
          color: '#dbe2ea'
        }
      },
      data: moduleCoverageBuckets.value.map((item) => item.label),
      type: 'category'
    },
    yAxis: {
      axisLabel: {
        color: '#64748b'
      },
      splitLine: {
        lineStyle: {
          color: '#eef2f6'
        }
      },
      type: 'value'
    },
    series: [
      {
        barWidth: 26,
        data: moduleCoverageBuckets.value.map((item) => ({
          itemStyle: {
            color: item.color
          },
          value: item.count
        })),
        itemStyle: {
          borderRadius: [10, 10, 0, 0]
        },
        type: 'bar'
      }
    ]
  };
});

const todoPieOption = computed(() => {
  return {
    legend: {
      bottom: 0,
      icon: 'circle',
      itemHeight: 8,
      itemWidth: 8,
      textStyle: {
        color: '#64748b'
      }
    },
    tooltip: {
      trigger: 'item'
    },
    series: [
      {
        center: ['50%', '42%'],
        data: [
          {
            itemStyle: { color: '#1677ff' },
            name: '待开通',
            value: unfinishedTodoCount.value
          },
          {
            itemStyle: { color: '#52c41a' },
            name: '已接入',
            value: completedTodoCount.value
          }
        ],
        label: {
          color: '#334155',
          formatter: '{b}\n{d}%',
          fontSize: 12
        },
        radius: ['54%', '78%'],
        type: 'pie'
      }
    ]
  };
});

const statusList = computed(() => {
  return shortcutCards.value
    .filter((item) => item.path.startsWith('/system/'))
    .map((item) => ({
      available: item.available,
      description: item.description,
      title: item.title
    }));
});

function handleShortcutClick(item?: ShortcutCard) {
  if (!item) {
    return;
  }
  if (!item.available) {
    ElMessage.info('当前账号暂未开通该入口');
    return;
  }
  router.push(item.path);
}

function collectVisibleLeafRoutes(routes: RouteRecordRaw[], parentPath = ''): RouteLeaf[] {
  return routes.flatMap((route) => {
    const currentPath = resolveRoutePath(parentPath, route.path || '');
    const children = Array.isArray(route.children) ? route.children : [];

    if (children.length > 0) {
      return collectVisibleLeafRoutes(children, currentPath);
    }

    if (route.meta?.hidden) {
      return [];
    }

    return [{
      path: currentPath
    }];
  });
}

function resolveRoutePath(parentPath: string, currentPath: string) {
  if (!currentPath) {
    return parentPath || '/';
  }
  if (currentPath.startsWith('/')) {
    return currentPath;
  }
  if (!parentPath || parentPath === '/') {
    return `/${currentPath}`;
  }
  return `${parentPath.replace(/\/$/, '')}/${currentPath}`;
}
</script>

<style scoped lang="scss">
.dashboard-page {
  gap: 16px;
  padding-bottom: 16px;
}

.hero-card {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) 360px;
  gap: 20px;
  overflow: hidden;
  padding: 28px;
  border: 1px solid var(--app-border-color);
  border-radius: 18px;
  background:
    radial-gradient(circle at top right, rgba(22, 119, 255, 0.14), transparent 34%),
    linear-gradient(135deg, #f8fbff 0%, #ffffff 48%, #f6f9fc 100%);
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.06);
}

.hero-card::after {
  position: absolute;
  top: -72px;
  right: -46px;
  width: 220px;
  height: 220px;
  border-radius: 50%;
  background: rgba(22, 119, 255, 0.06);
  content: '';
}

.hero-main,
.hero-side {
  position: relative;
  z-index: 1;
}

.hero-eyebrow {
  color: var(--app-primary);
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.hero-title {
  margin-top: 10px;
  color: #0f172a;
  font-size: 30px;
  font-weight: 700;
  line-height: 1.2;
}

.hero-subtitle {
  max-width: 720px;
  margin-top: 14px;
  color: #475569;
  font-size: 14px;
  line-height: 1.8;
}

.hero-tags,
.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hero-tags {
  margin-top: 18px;
}

.hero-actions {
  margin-top: 22px;
}

.hero-side {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  align-content: start;
}

.hero-highlight {
  padding: 16px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(4px);
}

.hero-highlight__label {
  color: #64748b;
  font-size: 12px;
}

.hero-highlight__value {
  margin-top: 10px;
  color: #0f172a;
  font-size: 24px;
  font-weight: 700;
}

.hero-highlight__note {
  margin-top: 8px;
  color: #94a3b8;
  font-size: 12px;
  line-height: 1.6;
}

.overview-row,
.analytics-row,
.workspace-row {
  margin: 0;
  row-gap: 16px;
}

.overview-card {
  display: flex;
  align-items: center;
  gap: 14px;
  height: 100%;
  min-height: 112px;
}

.overview-card__icon {
  display: inline-flex;
  width: 50px;
  height: 50px;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  font-size: 24px;
}

.overview-card__icon.is-primary {
  background: rgba(22, 119, 255, 0.10);
  color: #1677ff;
}

.overview-card__icon.is-success {
  background: rgba(82, 196, 26, 0.12);
  color: #52c41a;
}

.overview-card__icon.is-warning {
  background: rgba(250, 173, 20, 0.14);
  color: #faad14;
}

.overview-card__icon.is-danger {
  background: rgba(245, 34, 45, 0.10);
  color: #f5222d;
}

.overview-card__content {
  min-width: 0;
  flex: 1;
}

.overview-card__label,
.section-subtitle,
.shortcut-card__path,
.todo-item__meta,
.status-item__desc {
  color: #64748b;
  font-size: 13px;
}

.overview-card__value {
  margin-top: 10px;
  color: #0f172a;
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}

.overview-card__note {
  margin-top: 8px;
  color: #94a3b8;
  font-size: 12px;
  line-height: 1.6;
}

.analytics-card,
.workspace-card {
  height: 100%;
}

.section-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.section-title {
  color: #0f172a;
  font-size: 17px;
  font-weight: 700;
}

.section-subtitle {
  margin-top: 6px;
  line-height: 1.7;
}

.chart {
  width: 100%;
}

.chart--large {
  height: 320px;
}

.chart--small {
  height: 260px;
}

.todo-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 8px;
}

.todo-summary__item {
  padding: 14px 16px;
  border: 1px solid var(--app-border-color);
  border-radius: 12px;
  background: #fafcff;
}

.todo-summary__item span {
  color: #64748b;
  font-size: 12px;
}

.todo-summary__item strong {
  display: block;
  margin-top: 8px;
  color: #0f172a;
  font-size: 22px;
  font-weight: 700;
}

.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.shortcut-card {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 170px;
  padding: 16px;
  border: 1px solid var(--app-border-color);
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  color: inherit;
  cursor: pointer;
  text-align: left;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.shortcut-card:hover {
  border-color: rgba(22, 119, 255, 0.35);
  box-shadow: 0 12px 24px rgba(22, 119, 255, 0.10);
  transform: translateY(-2px);
}

.shortcut-card.is-disabled {
  background: #fafafa;
  cursor: not-allowed;
  opacity: 0.72;
  transform: none;
  box-shadow: none;
}

.shortcut-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.shortcut-card__icon {
  display: inline-flex;
  width: 42px;
  height: 42px;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: rgba(22, 119, 255, 0.10);
  color: var(--app-primary);
  font-size: 20px;
}

.shortcut-card__title,
.status-item__title {
  color: #0f172a;
  font-size: 15px;
  font-weight: 700;
}

.shortcut-card__desc {
  color: #475569;
  font-size: 13px;
  line-height: 1.7;
}

.column-stack {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
}

.todo-list,
.status-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-item,
.status-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid var(--app-border-color);
  border-radius: 12px;
  background: #fbfdff;
}

.todo-item__main,
.status-item__main {
  display: flex;
  gap: 12px;
  min-width: 0;
  flex: 1;
}

.todo-item__main :deep(.el-checkbox) {
  align-items: flex-start;
}

.todo-item__main :deep(.el-checkbox.is-disabled) {
  opacity: 1;
}

.todo-item__main :deep(.el-checkbox__label) {
  overflow-wrap: anywhere;
  white-space: normal;
  line-height: 1.6;
}

.todo-item__main :deep(.el-checkbox.is-disabled .el-checkbox__label) {
  color: #0f172a;
}

.status-dot {
  width: 10px;
  height: 10px;
  margin-top: 5px;
  border-radius: 50%;
  background: #cbd5e1;
  flex-shrink: 0;
}

.status-dot.active {
  background: #52c41a;
  box-shadow: 0 0 0 4px rgba(82, 196, 26, 0.12);
}

@media (max-width: 1440px) {
  .hero-card {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 992px) {
  .shortcut-grid,
  .hero-side {
    grid-template-columns: 1fr;
  }

  .hero-card {
    padding: 22px;
  }

  .hero-title {
    font-size: 26px;
  }
}

@media (max-width: 768px) {
  .todo-summary {
    grid-template-columns: 1fr;
  }

  .section-header {
    flex-direction: column;
  }

  .overview-card {
    min-height: auto;
  }
}
</style>
