<template>
  <div class="p-4">
    <h1 class="text-2xl font-bold mb-4">我的预约</h1>
    <div class="bg-white p-4 rounded-md shadow-md">
      <table class="w-full border-collapse">
        <thead>
          <tr>
            <th class="border-b p-2">预约ID</th>
            <th class="border-b p-2">书籍ID</th>
            <th class="border-b p-2">预约时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="reservation in reservations" :key="reservation.id">
            <td class="border-b p-2">{{ reservation.id }}</td>
            <td class="border-b p-2">{{ reservation.bookId }}</td>
            <td class="border-b p-2">{{ formatDate(reservation.reservationTime) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';

// 定义预约项的接口
interface Reservation {
  id: number;
  bookId: number;
  reservationTime: string; // 假设服务器返回字符串格式的时间
}

const reservations = ref<Reservation[]>([]);

// 格式化日期的辅助函数
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString();
};

onMounted(async () => {
  try {
    const response = await axios.get('/api/v1/reservations/user/1'); // 替换为实际用户ID
    reservations.value = response.data as Reservation[]; // 类型断言
  } catch (error) {
    console.error('获取预约信息失败:', error);
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
</style>