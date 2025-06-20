<template>
  <div class="p-4">
    <h1 class="text-2xl font-bold mb-4">收藏夹</h1>
    <div class="bg-white p-4 rounded-md shadow-md">
      <table class="w-full border-collapse">
        <thead>
          <tr>
            <th class="border-b p-2">收藏ID</th>
            <th class="border-b p-2">书籍ID</th>
            <th class="border-b p-2">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="favorite in favorites" :key="favorite.id">
            <td class="border-b p-2">{{ favorite.id }}</td>
            <td class="border-b p-2">{{ favorite.bookId }}</td>
            <td class="border-b p-2">
              <button 
                class="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded text-sm"
                @click="removeFavorite(favorite.id)"
              >
                移除
              </button>
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

// 定义收藏项的类型接口
interface Favorite {
  id: number;
  bookId: number;
  // 可根据实际需求添加更多字段
}

// 明确指定 favorites 的类型
const favorites = ref<Favorite[]>([]);

// 获取收藏列表
const fetchFavorites = async () => {
  try {
    const response = await axios.get('/api/v1/favorites/user/1'); // 替换为实际用户ID
    favorites.value = response.data as Favorite[]; // 类型断言
  } catch (error) {
    console.error('获取收藏信息失败:', error);
  }
};

// 移除收藏
const removeFavorite = async (favoriteId: number) => {
  if (confirm('确定要移除这个收藏吗？')) {
    try {
      await axios.delete(`/api/v1/favorites/${favoriteId}`);
      fetchFavorites(); // 刷新列表
    } catch (error) {
      console.error('移除收藏失败:', error);
    }
  }
};

onMounted(fetchFavorites);
</script>

<style scoped>
table {
  border-collapse: collapse;
}

th, td {
  border: 1px solid #e2e8f0;
}

button {
  transition: background-color 0.2s;
}
</style>