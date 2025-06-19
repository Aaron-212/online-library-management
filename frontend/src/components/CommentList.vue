<template>
  <div class="comment-list">
    <div v-for="comment in comments" :key="comment.id" class="comment-item">
      <div class="comment-header">
        <span class="font-semibold text-sm">{{ comment.user.username }}</span>
        <span class="text-xs text-muted-foreground">{{ formatTime(comment.createTime) }}</span>
      </div>
      <div class="comment-content text-sm leading-relaxed">{{ comment.content }}</div>
    </div>

    <div v-if="comments.length === 0" class="flex flex-col items-center justify-center py-12 text-center">
      <div class="w-12 h-12 rounded-full bg-muted flex items-center justify-center mb-4">
        <MessageCircle class="w-6 h-6 text-muted-foreground" />
      </div>
      <p class="text-sm text-muted-foreground">暂无评论</p>
      <p class="text-xs text-muted-foreground mt-1">成为第一个发表评论的人吧！</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { MessageCircle } from 'lucide-vue-next'

interface User {
  id: number | string
  username: string
}

interface Comment {
  id: number | string
  content: string
  createTime: string | Date
  user: User
}

defineProps<{
  comments: Comment[]
}>()

// Helper function to format time - you might want to move this to a utils file
const formatTime = (time: string | Date) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid hsl(var(--border));
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-content {
  line-height: 1.6;
  color: hsl(var(--foreground));
}
</style>
