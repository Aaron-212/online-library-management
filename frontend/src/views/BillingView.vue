<template>
  <div class="p-4">
    <h1 class="text-2xl font-bold mb-4">账单中心</h1>
    <div class="bg-white p-4 rounded-md shadow-md">
      <h2 class="text-xl font-bold mb-2">逾期费</h2>
      <table class="w-full border-collapse">
        <thead>
          <tr>
            <th class="border-b p-2">借阅记录ID</th>
            <th class="border-b p-2">逾期费用</th>
            <th class="border-b p-2">状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="fee in overdueFees" :key="fee.id">
            <td class="border-b p-2">{{ fee.borrowId }}</td>
            <td class="border-b p-2">{{ fee.fine }} 元</td>
            <td class="border-b p-2">
              <span v-if="fee.paid" class="px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs">
                已支付
              </span>
              <span v-else class="px-2 py-1 bg-red-100 text-red-800 rounded-full text-xs">
                未支付
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="bg-white p-4 rounded-md shadow-md mt-4">
      <h2 class="text-xl font-bold mb-2">赔书费</h2>
      <table class="w-full border-collapse">
        <thead>
          <tr>
            <th class="border-b p-2">借阅记录ID</th>
            <th class="border-b p-2">赔书费用</th>
            <th class="border-b p-2">状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="fee in compensationFees" :key="fee.id">
            <td class="border-b p-2">{{ fee.borrowId }}</td>
            <td class="border-b p-2">{{ fee.fine }} 元</td>
            <td class="border-b p-2">
              <span v-if="fee.paid" class="px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs">
                已支付
              </span>
              <span v-else class="px-2 py-1 bg-red-100 text-red-800 rounded-full text-xs">
                未支付
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

// 定义费用项的接口
interface Fee {
  id: number;
  borrowId: number;
  fine: number;
  paid: boolean;
  createdAt: string;
  // 可根据实际需求添加更多字段
}

const overdueFees = ref<Fee[]>([]);
const compensationFees = ref<Fee[]>([]);

// 格式化日期的辅助函数
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString();
};

onMounted(async () => {
  try {
    const overdueResponse = await axios.get('/api/v1/fees/overdue/1'); // 替换为实际用户ID
    overdueFees.value = overdueResponse.data as Fee[]; // 类型断言

    const compensationResponse = await axios.get('/api/v1/fees/compensation/1'); // 替换为实际用户ID
    compensationFees.value = compensationResponse.data as Fee[]; // 类型断言
  } catch (error) {
    console.error('获取费用信息失败:', error);
  }
});
</script>

<style scoped>
table {
  border-collapse: collapse;
}

th, td {
  border: 1px solid #e2e8f0;
}

.rounded-full {
  border-radius: 9999px;
}
</style>