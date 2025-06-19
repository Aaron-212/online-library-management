<template>
  <div class="books-view">
    <!-- 筛选条件栏 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="分类">
          <el-select v-model="filterForm.category" placeholder="全部分类">
            <el-option
              v-for="c in categories"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="出版社">
          <el-input v-model="filterForm.publisher" placeholder="出版社" />
        </el-form-item>

        <el-form-item label="出版年份">
          <el-date-picker
            v-model="filterForm.publishYear"
            type="year"
            placeholder="选择年份"
            value-format="YYYY"
          />
        </el-form-item>

        <el-form-item label="排序方式">
          <el-select v-model="filterForm.sortBy">
            <el-option label="按借阅量" value="borrowCount" />
            <el-option label="按评分" value="rating" />
            <el-option label="按最新" value="newest" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fetchBooks">筛选</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 图书列表 -->
    <div class="book-list">
      <BookCard
        v-for="book in books"
        :key="book.id"
        :book="book"
        @click="goToDetail(book.id)"
      />
    </div>

    <!-- 分页 -->
    <Pagination
      :current-page="pagination.current"
      :total="pagination.total"
      :page-size="pagination.size"
      @page-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBooks } from '@/api/book'
import BookCard from '@/components/BookCard.vue'
import Pagination from '@/components/Pagination.vue'

const router = useRouter()

// 筛选表单
const filterForm = ref({
  category: '',
  publisher: '',
  publishYear: '',
  sortBy: 'borrowCount'
})

// 分页数据
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 图书数据
const books = ref([])
const categories = ref([])

// 获取图书列表
const fetchBooks = async () => {
  try {
    const params = {
      ...filterForm.value,
      page: pagination.value.current,
      size: pagination.value.size
    }

    const res = await getBooks(params)
    books.value = res.data.list
    pagination.value.total = res.data.total
    categories.value = res.data.categories
  } catch (error) {
    console.error('获取图书列表失败:', error)
  }
}

// 分页变化
const handlePageChange = (page) => {
  pagination.value.current = page
  fetchBooks()
}

// 跳转详情页
const goToDetail = (bookId) => {
  router.push(`/books/${bookId}`)
}

// 初始化加载
onMounted(fetchBooks)
</script>

<style scoped>
.books-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.book-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}
</style>
