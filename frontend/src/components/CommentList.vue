<template>
  <div class="comment-list">
    <div v-for="comment in comments" :key="comment.id" class="comment-item">
      <div class="comment-header">
        <span class="username">{{ comment.user.username }}</span>
        <span class="time">{{ formatTime(comment.createTime) }}</span>
      </div>
      <div class="comment-content">{{ comment.content }}</div>
    </div>

    <el-empty v-if="comments.length === 0" description="暂无评论" />
  </div>
</template>

<script lang="ts">

defineProps({
  comments: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.username {
  font-weight: bold;
}

.time {
  color: #999;
  font-size: 12px;
}

.comment-content {
  line-height: 1.6;
}
</style>
