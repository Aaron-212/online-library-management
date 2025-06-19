<template>
  <div class="books-view">
    <!-- 筛选条件栏 -->
    <Card class="filter-card">
      <div class="flex flex-wrap gap-4 p-6">
        <div class="flex flex-col gap-2">
          <Label for="category">分类</Label>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" class="w-48 justify-between">
                {{ selectedCategory?.name || "全部分类" }}
                <ChevronDown class="ml-2 h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem @click="selectCategory(null)">
                全部分类
              </DropdownMenuItem>
              <DropdownMenuItem
                v-for="c in categories"
                :key="c.id"
                @click="selectCategory(c)"
              >
                {{ c.name }}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <div class="flex flex-col gap-2">
          <Label for="publisher">出版社</Label>
          <Input
            id="publisher"
            v-model="filterForm.publisher"
            placeholder="出版社"
            class="w-48"
          />
        </div>

        <div class="flex flex-col gap-2">
          <Label for="publishYear">出版年份</Label>
          <Input
            id="publishYear"
            v-model="filterForm.publishYear"
            placeholder="选择年份 (YYYY)"
            type="number"
            min="1800"
            max="2099"
            class="w-48"
          />
        </div>

        <div class="flex flex-col gap-2">
          <Label for="sortBy">排序方式</Label>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" class="w-48 justify-between">
                {{ getSortLabel(filterForm.sortBy) }}
                <ChevronDown class="ml-2 h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem @click="filterForm.sortBy = 'borrowCount'">
                按借阅量
              </DropdownMenuItem>
              <DropdownMenuItem @click="filterForm.sortBy = 'rating'">
                按评分
              </DropdownMenuItem>
              <DropdownMenuItem @click="filterForm.sortBy = 'newest'">
                按最新
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <div class="flex flex-col gap-2 justify-end">
          <Button @click="fetchBooks" class="w-20">筛选</Button>
        </div>
      </div>
    </Card>

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
    <div v-if="pagination.total > 0" class="flex justify-center items-center gap-2 mt-8">
      <Button
        variant="outline"
        :disabled="pagination.current <= 1"
        @click="handlePageChange(pagination.current - 1)"
      >
        <ChevronLeft class="h-4 w-4" />
        上一页
      </Button>
      
      <span class="text-sm text-muted-foreground">
        第 {{ pagination.current }} 页，共 {{ Math.ceil(pagination.total / pagination.size) }} 页
      </span>
      
      <Button
        variant="outline"
        :disabled="pagination.current >= Math.ceil(pagination.total / pagination.size)"
        @click="handlePageChange(pagination.current + 1)"
      >
        下一页
        <ChevronRight class="h-4 w-4" />
      </Button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ChevronDown, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { getBooks } from '@/api/book'
import BookCard from '@/components/BookCard.vue'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'

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

// 计算选中的分类
const selectedCategory = computed(() => {
  return categories.value.find(c => c.id === filterForm.value.category) || null
})

// 获取排序标签
const getSortLabel = (sortBy) => {
  const labels = {
    borrowCount: '按借阅量',
    rating: '按评分',
    newest: '按最新'
  }
  return labels[sortBy] || '按借阅量'
}

// 选择分类
const selectCategory = (category) => {
  filterForm.value.category = category?.id || ''
}

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
