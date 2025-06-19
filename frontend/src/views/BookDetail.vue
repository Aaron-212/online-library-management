<template>
  <div class="book-detail">
    <el-row :gutter="20">
      <!-- 图书基本信息 -->
      <el-col :span="8">
        <el-image
          :src="book.coverUrl"
          fit="cover"
          class="detail-cover"
        >
          <template #error>
            <div class="cover-error">暂无封面</div>
          </template>
        </el-image>
      </el-col>

      <el-col :span="16">
        <h1 class="title">{{ book.title }}</h1>
        <el-divider />

        <el-descriptions :column="1" border>
          <el-descriptions-item label="作者">{{ book.author }}</el-descriptions-item>
          <el-descriptions-item label="出版社">{{ book.publisher }}</el-descriptions-item>
          <el-descriptions-item label="出版日期">{{ formatDate(book.publishDate) }}</el-descriptions-item>
          <el-descriptions-item label="ISBN">{{ book.isbn }}</el-descriptions-item>
          <el-descriptions-item label="库存状态">
            <el-tag :type="book.available ? 'success' : 'danger'">
              {{ book.available ? '可借阅' : '已借出' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button
            type="primary"
            v-if="book.available && !hasBorrowed"
            @click="handleBorrow"
            :loading="borrowLoading"
          >
            借阅
          </el-button>

          <el-button
            type="warning"
            v-if="!book.available"
            @click="handleReserve"
            :loading="reserveLoading"
          >
            预约
          </el-button>
        </div>
      </el-col>
    </el-row>

    <!-- 评论区 -->
    <el-card class="comment-section">
      <template #header>
        <span>读者评论</span>
      </template>

      <CommentList :comments="comments" />

      <el-form
        v-if="userStore.isLoggedIn"
        :model="commentForm"
        @submit.prevent="submitComment"
      >
        <el-form-item prop="content">
          <el-input
            v-model="commentForm.content"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
          />
        </el-form-item>
        <el-button type="primary" native-type="submit">提交评论</el-button>
      </el-form>

      <div v-else class="login-tip">
        请<a @click="router.push('/login')">登录</a>后发表评论
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getBookDetail, borrowBook, reserveBook } from '@/api/book'
import { getComments, addComment } from '@/api/comment'
import { useUserStore } from '@/stores/user'
import CommentList from '@/components/CommentList.vue'
import { formatDate } from '@/utils/date'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const book = ref({})
const comments = ref([])
const hasBorrowed = ref(false)

// 评论表单
const commentForm = ref({
  content: ''
})

// 加载状态
const borrowLoading = ref(false)
const reserveLoading = ref(false)
const commentLoading = ref(false)

// 获取图书详情
const fetchBookDetail = async () => {
  try {
    const res = await getBookDetail(route.params.id)
    book.value = res.data.book
    hasBorrowed.value = res.data.hasBorrowed
  } catch (error) {
    console.error('获取图书详情失败:', error)
  }
}

// 获取评论
const fetchComments = async () => {
  try {
    const res = await getComments(route.params.id)
    comments.value = res.data
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}

// 借阅操作
const handleBorrow = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    borrowLoading.value = true
    await borrowBook(route.params.id)
    ElMessage.success('借阅成功')
    fetchBookDetail() // 刷新状态
  } catch (error) {
    ElMessage.error(error.message || '借阅失败')
  } finally {
    borrowLoading.value = false
  }
}

// 预约操作
const handleReserve = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    reserveLoading.value = true
    await reserveBook(route.params.id)
    ElMessage.success('预约成功')
  } catch (error) {
    ElMessage.error(error.message || '预约失败')
  } finally {
    reserveLoading.value = false
  }
}

// 提交评论
const submitComment = async () => {
  if (!commentForm.value.content.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  try {
    commentLoading.value = true
    await addComment({
      bookId: route.params.id,
      content: commentForm.value.content
    })
    ElMessage.success('评论已提交')
    commentForm.value.content = ''
    fetchComments() // 刷新评论列表
  } catch (error) {
    ElMessage.error(error.message || '评论提交失败')
  } finally {
    commentLoading.value = false
  }
}

// 初始化加载
onMounted(() => {
  fetchBookDetail()
  fetchComments()
})
</script>

<style scoped>
.book-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-cover {
  width: 100%;
  height: 400px;
  border-radius: 8px;
}

.title {
  margin-top: 0;
}

.action-buttons {
  margin-top: 20px;
}

.comment-section {
  margin-top: 30px;
}

.login-tip {
  color: #666;
  text-align: center;
  padding: 20px 0;
}

.login-tip a {
  color: #409eff;
  cursor: pointer;
}
</style>
