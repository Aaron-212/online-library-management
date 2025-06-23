<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { BookmarkPlus, BookOpen, Clock, Eye, Heart, History, Search, User } from 'lucide-vue-next'
import { booksService, borrowService, noticesService, statisticsService } from '@/lib/api'
import type { BookSummaryDto, BookStatisticsDto, Borrow, Notice } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()

// Data
const isLoading = ref(false)
const statistics = ref<BookStatisticsDto | null>(null)
const recentBooks = ref<BookSummaryDto[]>([])
const userBorrows = ref<Borrow[]>([])
const recentNotices = ref<Notice[]>([])
const userStats = ref({
  totalBorrows: 0,
  activeBorrows: 0,
  overdueBorrows: 0,
  favoriteGenres: [] as string[],
})

// Computed
const userDashboardCards = computed(() => [
  {
    title: 'Available Books',
    value: statistics.value?.availableBooks || 0,
    description: 'Ready to borrow',
    icon: BookOpen,
    color: 'text-green-600',
    bgColor: 'bg-green-50',
  },
  {
    title: 'Your Active Loans',
    value: userStats.value.activeBorrows,
    description: 'Currently borrowed',
    icon: Clock,
    color: 'text-blue-600',
    bgColor: 'bg-blue-50',
  },
  {
    title: 'Books Read',
    value: userStats.value.totalBorrows - userStats.value.activeBorrows,
    description: 'Successfully returned',
    icon: Heart,
    color: 'text-purple-600',
    bgColor: 'bg-purple-50',
  },
  {
    title: 'Overdue Books',
    value: userStats.value.overdueBorrows,
    description: 'Need to return',
    icon: Clock,
    color: 'text-red-600',
    bgColor: 'bg-red-50',
  },
])

const quickActions = [
  {
    title: 'Browse Books',
    description: 'Explore available books',
    icon: Search,
    action: () => router.push('/books'),
    variant: 'default' as const,
  },
  {
    title: 'My Borrowings',
    description: 'View borrowing history',
    icon: History,
    action: () => router.push('/borrows'),
    variant: 'default' as const,
  },
  {
    title: 'My Profile',
    description: 'Manage your account',
    icon: User,
    action: () => router.push('/profile'),
    variant: 'outline' as const,
  },
  {
    title: 'Reading List',
    description: 'Manage your wishlist',
    icon: BookmarkPlus,
    action: () => router.push('/reading-list'),
    variant: 'outline' as const,
  },
]

// Methods
const loadStatistics = async () => {
  try {
    statistics.value = await statisticsService.getBookStatistics()
  } catch (error) {
    console.error('Error loading statistics:', error)
    toast.error('Failed to load statistics')
  }
}

const loadUserStats = async () => {
  try {
    if (authStore.isAuthenticated) {
      // Get user's borrowing history to calculate stats
      const borrowsResponse = await borrowService.getUserBorrows({ page: 0, size: 100 })
      const allBorrows = borrowsResponse.content

      userStats.value = {
        totalBorrows: allBorrows.length,
        activeBorrows: allBorrows.filter((b) => b.status === 'BORROWED').length,
        overdueBorrows: allBorrows.filter((b) => {
          if (b.status !== 'BORROWED') return false
          const dueDate = new Date(b.returnTime)
          return dueDate < new Date()
        }).length,
        favoriteGenres: [], // This would require additional API to get user's reading preferences
      }
    }
  } catch (error) {
    console.error('Error loading user stats:', error)
  }
}

const loadRecentBooks = async () => {
  try {
    const response = await booksService.getAllSummary({ page: 0, size: 5, sort: 'id,desc' })
    recentBooks.value = response.content
  } catch (error) {
    console.error('Error loading recent books:', error)
  }
}

const loadUserBorrows = async () => {
  try {
    if (authStore.isAuthenticated) {
      const response = await borrowService.getUserBorrows({ page: 0, size: 5 })
      userBorrows.value = response.content
    }
  } catch (error) {
    console.error('Error loading user borrows:', error)
  }
}

const loadRecentNotices = async () => {
  try {
    const response = await noticesService.getAll({ page: 0, size: 3 })
    recentNotices.value = response.content
  } catch (error) {
    console.error('Error loading recent notices:', error)
  }
}

const loadDashboardData = async () => {
  isLoading.value = true
  try {
    await Promise.all([
      loadStatistics(),
      loadRecentBooks(),
      loadUserBorrows(),
      loadRecentNotices(),
      loadUserStats(),
    ])
  } finally {
    isLoading.value = false
  }
}

const getBorrowStatusBadge = (borrow: Borrow) => {
  if (borrow.status === 'RETURNED') {
    return { variant: 'success' as const, text: 'Returned' }
  }

  if (borrow.status === 'BORROWED') {
    const dueDate = new Date(borrow.returnTime)
    const now = new Date()

    if (dueDate < now) {
      return { variant: 'destructive' as const, text: 'Overdue' }
    }

    const daysUntilDue = Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
    if (daysUntilDue <= 3) {
      return { variant: 'secondary' as const, text: 'Due Soon' }
    }

    return { variant: 'default' as const, text: 'Active' }
  }

  return { variant: 'default' as const, text: borrow.status }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 12) return 'Good morning'
  if (hour < 18) return 'Good afternoon'
  return 'Good evening'
}

// Lifecycle
onMounted(() => {
  loadDashboardData()
})
</script>

<template>
  <div class="user-dashboard space-y-6">
    <!-- Welcome Section -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold">
          {{ getGreeting() }}{{ authStore.user?.username ? ', ' + authStore.user.username : '' }}!
        </h1>
        <p class="text-muted-foreground">Ready to explore your library today?</p>
      </div>
      <div v-if="authStore.isAuthenticated" class="text-right">
        <Badge variant="outline" class="text-sm px-3 py-1">
          {{ authStore.user?.role === 'ADMIN' ? 'Administrator' : 'Reader' }}
        </Badge>
        <p class="text-xs text-muted-foreground mt-1">
          Member since
          {{ authStore.user?.createdTime ? formatDate(authStore.user.createdTime) : 'recently' }}
        </p>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-8">Loading your dashboard...</div>

    <template v-else>
      <!-- User Statistics Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card
          v-for="card in userDashboardCards"
          :key="card.title"
          class="hover:shadow-lg transition-shadow"
        >
          <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle class="text-sm font-medium">{{ card.title }}</CardTitle>
            <div :class="[card.bgColor, 'p-2 rounded-md']">
              <component :is="card.icon" :class="[card.color, 'h-4 w-4']" />
            </div>
          </CardHeader>
          <CardContent>
            <div class="text-2xl font-bold">{{ card.value.toLocaleString() }}</div>
            <p class="text-xs text-muted-foreground">{{ card.description }}</p>
          </CardContent>
        </Card>
      </div>

      <!-- Quick Actions -->
      <Card>
        <CardHeader>
          <CardTitle>Quick Actions</CardTitle>
          <CardDescription>What would you like to do today?</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <Button
              v-for="action in quickActions"
              :key="action.title"
              :variant="action.variant"
              class="h-auto p-4 flex flex-col items-center gap-2"
              @click="action.action"
            >
              <component :is="action.icon" class="h-6 w-6" />
              <div class="text-center">
                <div class="font-medium">{{ action.title }}</div>
                <div class="text-xs text-muted-foreground">{{ action.description }}</div>
              </div>
            </Button>
          </div>
        </CardContent>
      </Card>

      <!-- Content Grid -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Recently Added Books -->
        <Card>
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>New Arrivals</CardTitle>
              <CardDescription>Recently added to the library</CardDescription>
            </div>
            <Button variant="outline" size="sm" @click="router.push('/books')">
              <Eye class="h-4 w-4 mr-2" />
              Browse All
            </Button>
          </CardHeader>
          <CardContent>
            <div v-if="recentBooks.length === 0" class="text-center py-4 text-muted-foreground">
              No recent books found
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="book in recentBooks"
                :key="book.id"
                class="flex items-center space-x-3 p-2 rounded-lg hover:bg-muted/50 cursor-pointer"
                @click="router.push(`/books/${book.id}`)"
              >
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium truncate">{{ book.title }}</p>
                  <p class="text-xs text-muted-foreground truncate">
                    {{ Array.isArray(book.authors) ? book.authors.join(', ') : 'Unknown author' }}
                  </p>
                </div>
                <Badge :variant="book.availableQuantity > 0 ? 'success' : 'destructive'" size="sm">
                  {{ book.availableQuantity > 0 ? 'Available' : 'Unavailable' }}
                </Badge>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Your Recent Borrows -->
        <Card>
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Your Recent Activity</CardTitle>
              <CardDescription>Your borrowing history</CardDescription>
            </div>
            <Button variant="outline" size="sm" @click="router.push('/borrows')">
              <Eye class="h-4 w-4 mr-2" />
              View All
            </Button>
          </CardHeader>
          <CardContent>
            <div v-if="!authStore.isAuthenticated" class="text-center py-4 text-muted-foreground">
              Please log in to view your activity
            </div>
            <div
              v-else-if="userBorrows.length === 0"
              class="text-center py-4 text-muted-foreground"
            >
              No borrowing activity yet
              <div class="mt-2">
                <Button variant="outline" size="sm" @click="router.push('/books')">
                  Start browsing books
                </Button>
              </div>
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="borrow in userBorrows"
                :key="borrow.borrowId"
                class="flex items-center space-x-3 p-2 rounded-lg hover:bg-muted/50 cursor-pointer"
              >
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium truncate">{{ borrow.bookTitle }}</p>
                  <p class="text-xs text-muted-foreground">
                    {{ borrow.status === 'RETURNED' ? 'Returned' : 'Due' }}:
                    {{
                      borrow.status === 'RETURNED' && borrow.actualReturnTime
                        ? formatDate(borrow.actualReturnTime)
                        : formatDate(borrow.returnTime)
                    }}
                  </p>
                </div>
                <Badge :variant="getBorrowStatusBadge(borrow).variant" size="sm">
                  {{ getBorrowStatusBadge(borrow).text }}
                </Badge>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Library Notices -->
        <Card class="lg:col-span-2">
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Library Notices</CardTitle>
              <CardDescription>Important updates and announcements</CardDescription>
            </div>
            <Button variant="outline" size="sm" @click="router.push('/notices')">
              <Eye class="h-4 w-4 mr-2" />
              View All
            </Button>
          </CardHeader>
          <CardContent>
            <div v-if="recentNotices.length === 0" class="text-center py-4 text-muted-foreground">
              No recent notices
            </div>
            <div v-else class="space-y-4">
              <div
                v-for="notice in recentNotices"
                :key="notice.id"
                class="border-l-4 border-primary pl-4 py-2 hover:bg-muted/50 rounded-r cursor-pointer"
                @click="router.push(`/notices/${notice.id}`)"
              >
                <h4 class="font-medium">{{ notice.title }}</h4>
                <p class="text-sm text-muted-foreground mt-1 line-clamp-2">
                  {{ notice.content }}
                </p>
                <p class="text-xs text-muted-foreground mt-2">
                  {{ formatDate(notice.publishTime) }}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Personalized Recommendations Section (if authenticated) -->
      <Card v-if="authStore.isAuthenticated" class="mt-6">
        <CardHeader>
          <CardTitle>Recommended for You</CardTitle>
          <CardDescription>Based on your reading history and preferences</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="text-center py-8 text-muted-foreground">
            <BookOpen class="h-12 w-12 mx-auto mb-4 opacity-50" />
            <p>Personalized recommendations coming soon!</p>
            <p class="text-sm mt-2">Keep borrowing books to help us learn your preferences.</p>
          </div>
        </CardContent>
      </Card>
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
</style>
