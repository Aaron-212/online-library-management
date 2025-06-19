<template>
  <el-card class="book-card" @click="$emit('click')">
    <el-image
      :src="book.coverUrl"
      fit="cover"
      class="book-cover"
    >
      <template #error>
        <div class="cover-error">暂无封面</div>
      </template>
    </el-image>

    <div class="book-info">
      <h3 class="title">{{ book.title }}</h3>
      <p class="author">{{ book.author }}</p>
      <el-tag
        :type="book.available ? 'success' : 'danger'"
        size="small"
      >
        {{ book.available ? '可借阅' : '已借出' }}
      </el-tag>
    </div>
  </el-card>
</template>

<script lang="ts">
defineProps({
  book: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.book-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-cover {
  width: 100%;
  height: 180px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.cover-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
}

.book-info {
  padding: 0 5px;
}

.title {
  margin: 0 0 5px;
  font-size: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.author {
  margin: 0 0 10px;
  color: #666;
  font-size: 14px;
}
</style>
