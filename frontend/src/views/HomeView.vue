<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import {
  ArrowRight,
  BookOpen,
  Clock,
  Heart,
  Library,
  LogIn,
  Search,
  TrendingUp,
  UserPlus,
  Users,
} from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { useI18n } from 'vue-i18n'
import { booksService, categoriesService, noticesService, statisticsService } from '@/lib/api'
import type { BookSummaryDto, BookStatisticsDto, Notice } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

// Data
const isLoading = ref(false)
// 推荐图书
const recommendedBooks = ref<BookSummaryDto[]>([])
// 热门借阅榜
const topBorrowBooks = ref<BookStatisticsDto[]>([])
// 公告轮播
const recentNotices = ref<Notice[]>([])
// 分类导航
const categories = ref<string[]>([])

// 搜索关键字
const searchKeyword = ref('')

// Computed
const heroTitle = computed(() => {
  if (authStore.isAuthenticated) {
    const username = authStore.user?.username || 'Reader'
    return t('home.hero.welcomeBack', { username })
  }
  return t('home.hero.welcomeToLibrary')
})

const heroSubtitle = computed(() => {
  if (authStore.isAuthenticated) {
    return t('home.hero.subtitleAuthenticated')
  }
  return t('home.hero.subtitleGuest')
})

// Quick action buttons for different user states
const quickActions = computed(() => {
  if (authStore.isAuthenticated) {
    return [
      {
        title: t('home.quickActions.browseBooks.title'),
        description: t('home.quickActions.browseBooks.description'),
        icon: Search,
        action: () => router.push('/books'),
        variant: 'default' as const,
        featured: true,
      },
      {
        title: t('home.quickActions.myBorrows.title'),
        description: t('home.quickActions.myBorrows.description'),
        icon: Clock,
        action: () => router.push('/borrows'),
        variant: 'outline' as const,
      },
      {
        title: t('home.quickActions.dashboard.title'),
        description: t('home.quickActions.dashboard.description'),
        icon: TrendingUp,
        action: () => router.push('/dashboard'),
        variant: 'outline' as const,
      },
    ]
  } else {
    return [
      {
        title: t('home.quickActions.signIn.title'),
        description: t('home.quickActions.signIn.description'),
        icon: LogIn,
        action: () => router.push('/login'),
        variant: 'default' as const,
        featured: true,
      },
      {
        title: t('home.quickActions.register.title'),
        description: t('home.quickActions.register.description'),
        icon: UserPlus,
        action: () => router.push('/register'),
        variant: 'outline' as const,
        featured: true,
      },
      {
        title: t('home.quickActions.browseBooks.title'),
        description: t('home.quickActions.browseBooks.description'),
        icon: Search,
        action: () => router.push('/books'),
        variant: 'outline' as const,
      },
    ]
  }
})

// Library features showcase
const libraryFeatures = computed(() => [
  {
    icon: BookOpen,
    title: t('home.libraryFeatures.vastCollection.title'),
    description: t('home.libraryFeatures.vastCollection.description'),
    color: 'text-blue-600',
    bgColor: 'bg-blue-50',
  },
  {
    icon: Users,
    title: t('home.libraryFeatures.community.title'),
    description: t('home.libraryFeatures.community.description'),
    color: 'text-green-600',
    bgColor: 'bg-green-50',
  },
  {
    icon: Clock,
    title: t('home.libraryFeatures.easyManagement.title'),
    description: t('home.libraryFeatures.easyManagement.description'),
    color: 'text-purple-600',
    bgColor: 'bg-purple-50',
  },
  {
    icon: Heart,
    title: t('home.libraryFeatures.personalized.title'),
    description: t('home.libraryFeatures.personalized.description'),
    color: 'text-red-600',
    bgColor: 'bg-red-50',
  },
])

// Methods
// 加载推荐图书（最新上架）
const loadRecommendedBooks = async () => {
  try {
    const res = await booksService.getAllSummary({ page: 0, size: 8, sort: 'id,desc' })
    recommendedBooks.value = res.content
  } catch (e) {
    console.error(e)
  }
}

// 加载热门借阅榜
const loadTopBorrowBooks = async () => {
  try {
    const res = await statisticsService.getTopBooks({ limit: 10 })
    topBorrowBooks.value = res
  } catch (e) {
    console.error(e)
  }
}

// 加载最新公告
const loadRecentNotices = async () => {
  try {
    const res = await noticesService.getAll({ page: 0, size: 5 })
    recentNotices.value = res.content
  } catch (e) {
    console.error(e)
  }
}

// 加载分类导航
const loadCategories = async () => {
  try {
    const allCategories = await categoriesService.getAll()
    categories.value = allCategories.map((category) => category.name).sort()
  } catch (e) {
    console.error(e)
  }
}

const loadHomeData = async () => {
  isLoading.value = true
  try {
    await Promise.all([
      loadRecommendedBooks(),
      loadTopBorrowBooks(),
      loadRecentNotices(),
      loadCategories(),
    ])
  } catch {
    toast.error(t('home.loading.error'))
  } finally {
    isLoading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  if (!searchKeyword.value.trim()) return
  router.push({ path: '/books', query: { keyword: searchKeyword.value.trim() } })
}

const goToCategory = (cat: string) => {
  router.push({ path: '/books', query: { category: cat } })
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

const goToBookDetail = (bookId: number) => {
  router.push(`/books/${bookId}`)
}

// Lifecycle
onMounted(() => {
  loadHomeData()
})
</script>

<template>
  <div class="home-view space-y-8">
    <!-- Hero Section -->
    <section
      class="relative overflow-hidden rounded-lg bg-gradient-to-br from-primary/10 via-primary/5 to-background p-8 md:p-12"
    >
      <div class="relative z-10">
        <div class="flex items-center gap-3 mb-4">
          <Library class="h-8 w-8 text-primary" />
          <h1 class="text-4xl md:text-5xl font-bold tracking-tight">
            {{ heroTitle }}
          </h1>
        </div>
        <p class="text-xl text-muted-foreground mb-8 max-w-2xl">
          {{ heroSubtitle }}
        </p>

        <!-- Search Bar -->
        <div class="mt-8 max-w-xl">
          <div class="flex items-center gap-2 p-2 border rounded-md bg-background">
            <Search class="h-5 w-5 text-muted-foreground ml-2" />
            <Input
              v-model="searchKeyword"
              :placeholder="t('home.hero.searchPlaceholder')"
              class="flex-1 border-0 focus-visible:ring-0 focus-visible:ring-offset-0"
              @keyup.enter="handleSearch"
            />
            <Button @click="handleSearch">{{ t('home.hero.searchButton') }}</Button>
          </div>
        </div>
      </div>

      <!-- Decorative background elements -->
      <div
        class="absolute top-0 right-0 w-64 h-64 bg-primary/5 rounded-full -translate-y-32 translate-x-32"
      ></div>
      <div
        class="absolute bottom-0 left-0 w-48 h-48 bg-primary/3 rounded-full translate-y-24 -translate-x-24"
      ></div>
    </section>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-12">
      <div
        class="animate-spin inline-block w-6 h-6 border-[3px] border-current border-t-transparent text-primary rounded-full"
      />
      <p class="mt-2 text-muted-foreground">{{ t('home.loading.content') }}</p>
    </div>

    <template v-else>
      <!-- Notice Carousel (desktop only) -->
      <section v-if="recentNotices.length" class="hidden sm:block">
        <div class="overflow-x-auto flex gap-6 snap-x pb-4 scrollbar-hide">
          <Card
            v-for="notice in recentNotices"
            :key="notice.id"
            class="min-w-[280px] snap-start cursor-pointer hover:shadow-md transition-shadow"
            @click="router.push('/notices')"
          >
            <CardHeader>
              <CardTitle class="line-clamp-2">{{ notice.title }}</CardTitle>
              <CardDescription>{{ formatDate(notice.publishTime) }}</CardDescription>
            </CardHeader>
            <CardContent>
              <p class="text-sm text-muted-foreground line-clamp-3">{{ notice.content }}</p>
            </CardContent>
          </Card>
        </div>
      </section>

      <!-- Featured Books Section -->
      <section v-if="recommendedBooks.length > 0">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-2xl font-bold">{{ t('home.recommendedBooks.title') }}</h2>
            <p class="text-muted-foreground">{{ t('home.recommendedBooks.subtitle') }}</p>
          </div>
          <Button variant="outline" @click="router.push('/books')">
            {{ t('home.recommendedBooks.viewAll') }}
            <ArrowRight class="h-4 w-4 ml-2" />
          </Button>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 xl:grid-cols-5 gap-6">
          <Card
            v-for="book in recommendedBooks"
            :key="book.id"
            class="group cursor-pointer hover:shadow-lg transition-all duration-200 hover:-translate-y-1"
            @click="goToBookDetail(book.id)"
          >
            <CardHeader class="pb-3">
              <div class="flex items-start justify-between">
                <div class="flex-1 min-w-0">
                  <CardTitle
                    class="text-lg line-clamp-2 group-hover:text-primary transition-colors"
                  >
                    {{ book.title }}
                  </CardTitle>
                  <CardDescription class="mt-1">
                    {{
                      Array.isArray(book.authors)
                        ? book.authors.join(', ')
                        : t('home.status.unknownAuthor')
                    }}
                  </CardDescription>
                </div>
                <Badge
                  :variant="book.availableQuantity > 0 ? 'success' : 'destructive'"
                  class="ml-2 shrink-0"
                >
                  {{
                    book.availableQuantity > 0
                      ? t('home.status.available')
                      : t('home.status.outOfStock')
                  }}
                </Badge>
              </div>
            </CardHeader>
            <CardContent>
              <div class="flex items-center justify-between text-xs text-muted-foreground">
                <span
                  >{{ book.availableQuantity }}/{{ book.totalQuantity }}
                  {{ t('home.status.available_count') }}</span
                >
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      <!-- Popular Borrow Leaderboard -->
      <section v-if="topBorrowBooks.length" class="space-y-4">
        <h2 class="text-2xl font-bold">{{ t('home.popularBooks.title') }}</h2>
        <p class="text-muted-foreground">{{ t('home.popularBooks.subtitle') }}</p>
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          <Card
            v-for="(book, idx) in topBorrowBooks"
            :key="book.bookId"
            class="cursor-pointer hover:shadow-md transition-shadow"
            @click="goToBookDetail(book.bookId)"
          >
            <CardHeader class="flex-row items-center gap-4">
              <div class="text-2xl font-bold w-8">#{{ idx + 1 }}</div>
              <div class="flex-1 min-w-0">
                <CardTitle class="text-lg line-clamp-2">{{ book.title }}</CardTitle>
                <CardDescription>{{
                  Array.isArray(book.authors)
                    ? book.authors.join(', ')
                    : t('home.status.unknownAuthor')
                }}</CardDescription>
              </div>
            </CardHeader>
            <CardContent class="text-xs text-muted-foreground flex justify-between">
              <span>{{ book.borrowCount }} {{ t('home.status.borrows') }}</span>
              <span>{{ book.isbn }}</span>
            </CardContent>
          </Card>
        </div>
      </section>

      <!-- Quick Actions Grid (for authenticated users) -->
      <section v-if="authStore.isAuthenticated">
        <h2 class="text-2xl font-bold mb-6">{{ t('home.quickActions.title') }}</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Button
            v-for="action in quickActions"
            :key="action.title"
            :variant="action.variant"
            class="h-auto p-6 flex flex-col items-center gap-3 text-center"
            @click="action.action"
          >
            <component :is="action.icon" class="h-8 w-8" />
            <div>
              <div class="font-semibold">{{ action.title }}</div>
              <div class="text-sm opacity-80">{{ action.description }}</div>
            </div>
          </Button>
        </div>
      </section>

      <!-- Library Features (for non-authenticated users) -->
      <section v-if="!authStore.isAuthenticated">
        <div class="text-center mb-8">
          <h2 class="text-2xl font-bold mb-2">{{ t('home.libraryFeatures.title') }}</h2>
          <p class="text-muted-foreground">
            {{ t('home.libraryFeatures.subtitle') }}
          </p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <Card v-for="feature in libraryFeatures" :key="feature.title" class="text-center p-6">
            <div
              :class="[
                feature.bgColor,
                'w-12 h-12 rounded-lg flex items-center justify-center mx-auto mb-4',
              ]"
            >
              <component :is="feature.icon" :class="[feature.color, 'h-6 w-6']" />
            </div>
            <h3 class="font-semibold mb-2">{{ feature.title }}</h3>
            <p class="text-sm text-muted-foreground">{{ feature.description }}</p>
          </Card>
        </div>
      </section>
    </template>
  </div>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
