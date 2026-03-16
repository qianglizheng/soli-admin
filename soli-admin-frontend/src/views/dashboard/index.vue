<template>
  <div class="dashboard-container">
    <!-- 顶部数据卡片 -->
    <el-row :gutter="20" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-people">
            <el-icon class="card-panel-icon">
              <User />
            </el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">总用户数</div>
            <div class="card-panel-num">1,245</div>
            <div class="card-panel-trend">
              <span class="trend-up"><el-icon>
                  <Top />
                </el-icon> 12%</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-message">
            <el-icon class="card-panel-icon">
              <ChatLineSquare />
            </el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">未读消息</div>
            <div class="card-panel-num">8</div>
            <div class="card-panel-trend">
              <span class="trend-down"><el-icon>
                  <Bottom />
                </el-icon> 5%</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-money">
            <el-icon class="card-panel-icon">
              <Money />
            </el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">今日交易额</div>
            <div class="card-panel-num">¥ 24,560</div>
            <div class="card-panel-trend">
              <span class="trend-up"><el-icon>
                  <Top />
                </el-icon> 8%</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-shopping">
            <el-icon class="card-panel-icon">
              <ShoppingCart />
            </el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">总订单数</div>
            <div class="card-panel-num">1,452</div>
            <div class="card-panel-trend">
              <span class="trend-up"><el-icon>
                  <Top />
                </el-icon> 22%</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-wrapper">
      <el-col :span="16" :xs="24">
        <div class="chart-container">
          <div class="chart-header">
            <h3>流量趋势</h3>
            <div class="chart-actions">
              <el-radio-group v-model="timeRange" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">全年</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <v-chart class="chart" :option="lineOption" autoresize />
        </div>
      </el-col>
      <el-col :span="8" :xs="24">
        <div class="chart-container">
          <div class="chart-header">
            <h3>访问来源</h3>
          </div>
          <v-chart class="chart" :option="pieOption" autoresize />
        </div>
      </el-col>
    </el-row>

    <!-- 底部表格和列表 -->
    <el-row :gutter="20">
      <el-col :span="12" :xs="24">
        <div class="table-container">
          <div class="card-header">
            <span>最新订单</span>
            <el-button link type="primary">查看更多</el-button>
          </div>
          <el-table :data="tableData" style="width: 100%" :show-header="true">
            <el-table-column prop="orderNo" label="订单号" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.statusType" size="small">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" />
            <el-table-column prop="date" label="时间" width="140" align="right" />
          </el-table>
        </div>
      </el-col>
      <el-col :span="12" :xs="24">
        <div class="todo-container">
          <div class="card-header">
            <span>待办事项</span>
            <el-button type="primary" icon="Plus" circle size="small" />
          </div>
          <div class="todo-list">
            <div v-for="(item, index) in todoList" :key="index" class="todo-item">
              <el-checkbox v-model="item.done" :label="item.title" />
              <el-tag size="small" :type="item.tagType" class="todo-tag">{{ item.tag }}</el-tag>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { User, ChatLineSquare, Money, ShoppingCart, Top, Bottom, Plus } from '@element-plus/icons-vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { LineChart, PieChart } from 'echarts/charts';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components';
import VChart from 'vue-echarts';

defineOptions({
  name: "DashBorad"
})

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
]);

const timeRange = ref('week');

const lineOption = reactive({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['访问量', '订单量']
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '访问量',
      type: 'line',
      smooth: true,
      data: [120, 132, 101, 134, 90, 230, 210],
      itemStyle: { color: '#409EFF' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [{ offset: 0, color: 'rgba(64, 158, 255, 0.3)' }, { offset: 1, color: 'rgba(64, 158, 255, 0)' }]
        }
      }
    },
    {
      name: '订单量',
      type: 'line',
      smooth: true,
      data: [220, 182, 191, 234, 290, 330, 310],
      itemStyle: { color: '#67C23A' },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [{ offset: 0, color: 'rgba(103, 194, 58, 0.3)' }, { offset: 1, color: 'rgba(103, 194, 58, 0)' }]
        }
      }
    }
  ]
});

const pieOption = reactive({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    bottom: '5%',
    left: 'center'
  },
  series: [
    {
      name: '访问来源',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 20,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: 1048, name: '搜索引擎' },
        { value: 735, name: '直接访问' },
        { value: 580, name: '邮件营销' },
        { value: 484, name: '联盟广告' },
        { value: 300, name: '视频广告' }
      ]
    }
  ]
});

const tableData = [
  { orderNo: 'ORD-001', status: '已完成', statusType: 'success', amount: '¥ 120.00', date: '10:23' },
  { orderNo: 'ORD-002', status: '待付款', statusType: 'warning', amount: '¥ 560.00', date: '11:15' },
  { orderNo: 'ORD-003', status: '处理中', statusType: 'primary', amount: '¥ 230.50', date: '12:45' },
  { orderNo: 'ORD-004', status: '已取消', statusType: 'info', amount: '¥ 100.00', date: '14:20' },
  { orderNo: 'ORD-005', status: '已完成', statusType: 'success', amount: '¥ 890.00', date: '15:30' },
];

const todoList = ref([
  { title: '修复登录页面 Bug', done: false, tag: 'High', tagType: 'danger' },
  { title: '更新用户文档', done: true, tag: 'Low', tagType: 'info' },
  { title: '设计新版首页', done: false, tag: 'Medium', tagType: 'warning' },
  { title: '代码审查', done: false, tag: 'Medium', tagType: 'warning' },
  { title: '周会准备', done: true, tag: 'Low', tagType: 'info' },
]);
</script>

<style scoped lang="scss">
.dashboard-container {
  background-color: #fff;
  min-height: 100%;

  .panel-group {
    margin-bottom: 20px;

    .card-panel-col {
      margin-bottom: 20px;
    }

    .card-panel {
      height: 108px;
      cursor: pointer;
      font-size: 12px;
      position: relative;
      overflow: hidden;
      color: #666;
      background: #fff;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      border-radius: 4px;
      display: flex;
      align-items: center;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);

        .card-panel-icon-wrapper {
          color: #fff;
        }

        .icon-people {
          background: #40c9c6;
        }

        .icon-message {
          background: #36a3f7;
        }

        .icon-money {
          background: #f4516c;
        }

        .icon-shopping {
          background: #34bfa3;
        }
      }

      .card-panel-icon-wrapper {
        float: left;
        margin: 14px 0 0 14px;
        padding: 16px;
        transition: all 0.38s ease-out;
        border-radius: 6px;

        .card-panel-icon {
          float: left;
          font-size: 48px;
        }
      }

      .icon-people {
        color: #40c9c6;
      }

      .icon-message {
        color: #36a3f7;
      }

      .icon-money {
        color: #f4516c;
      }

      .icon-shopping {
        color: #34bfa3;
      }

      .card-panel-description {
        float: right;
        font-weight: bold;
        margin: 26px;
        margin-left: 0px;
        flex: 1;
        text-align: right;

        .card-panel-text {
          line-height: 18px;
          color: rgba(0, 0, 0, 0.45);
          font-size: 16px;
          margin-bottom: 12px;
        }

        .card-panel-num {
          font-size: 20px;
          margin-bottom: 8px;
          color: #333;
        }

        .card-panel-trend {
          font-size: 14px;

          .trend-up {
            color: #f56c6c;
          }

          .trend-down {
            color: #67c23a;
          }
        }
      }
    }
  }

  .chart-wrapper {
    margin-bottom: 20px;

    .chart-container {
      background: #fff;
      padding: 16px 16px 0;
      margin-bottom: 20px;
      border-radius: 4px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

      .chart-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        padding-left: 10px;
        border-left: 4px solid #409EFF;

        h3 {
          margin: 0;
          font-size: 18px;
          color: #303133;
        }
      }

      .chart {
        height: 350px;
        width: 100%;
      }
    }
  }

  .table-container,
  .todo-container {
    background: #fff;
    padding: 20px;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    min-height: 300px;
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
      font-size: 16px;
      font-weight: bold;
      color: #303133;
    }
  }

  .todo-list {
    .todo-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 0;
      border-bottom: 1px solid #EBEEF5;

      &:last-child {
        border-bottom: none;
      }

      .todo-tag {
        margin-left: 10px;
      }
    }
  }
}
</style>
