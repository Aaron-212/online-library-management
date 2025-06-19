<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { 
  Card, 
  CardContent, 
  CardDescription, 
  CardHeader, 
  CardTitle 
} from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { 
  BookOpen, 
  Users, 
  Clock, 
  TrendingUp, 
  AlertTriangle,
  Plus,
  Search,
  Eye,
  Download
} from 'lucide-vue-next'
import { statisticsService, booksService, borrowService, noticesService } from '@/lib/api'
import type { BookStatisticsDto, Book, Borrow, Notice } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()

// Data
const isLoading = ref(false)
const statistics = ref<BookStatisticsDto | null>(null)
const recentBooks = ref<Book[]>([])
const recentBorrows = ref<Borrow[]>([])
const recentNotices = ref<Notice[]>([])

// Computed
const isAdmin = computed(() => {
  // This should be replaced with actual role checking when user roles are available
  return authStore.isAuthenticated
})

const dashboardCards = computed(() => [
  {
    title: 'Total Books',
    value: statistics.value?.totalBooks || 0,
    description: 'Books in library',
    icon: BookOpen,
    color: 'text-blue-600',
    bgColor: 'bg-blue-50',
  },
  {
    title: 'Available Books',
    value: statistics.value?.availableBooks || 0,
    description: 'Ready to borrow',
    icon: BookOpen,
    color: 'text-green-600',
    bgColor: 'bg-green-50',
  },
  {
    title: 'Active Borrows',
    value: statistics.value?.activeBorrows || 0,
    description: 'Currently borrowed',
    icon: Clock,
    color: 'text-orange-600',
    bgColor: 'bg-orange-50',
  },
  {
    title: 'Overdue Books',
    value: statistics.value?.overdueBorrows || 0,
    description: 'Need attention',
    icon: AlertTriangle,
    color: 'text-red-600',
    bgColor: 'bg-red-50',
  }
])

const quickActions = [
  {
    title: 'Browse Books',
    description: 'Explore available books',
    icon: Search,
    action: () => router.push('/books'),
    variant: 'default' as const
  },
  {
    title: 'Add New Book',
    description: 'Add books to library',
    icon: Plus,
    action: () => router.push('/admin/books/new'),
    variant: 'default' as const,
    adminOnly: true
  },
  {
    title: 'Manage Borrows',
    description: 'View borrowing history',
    icon: Clock,
    action: () => router.push('/borrows'),
    variant: 'outline' as const
  },
  {
    title: 'View Reports',
    description: 'Generate reports',
    icon: Download,
    action: () => router.push('/reports'),
    variant: 'outline' as const,
    adminOnly: true
  }
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

const loadRecentBooks = async () => {
  try {
    const response = await booksService.getAll({ page: 0, size: 5, sort: 'id,desc' })
    recentBooks.value = response.content
  } catch (error) {
    console.error('Error loading recent books:', error)
  }
}

const loadRecentBorrows = async () => {
  try {
    if (authStore.isAuthenticated) {
      const response = await borrowService.getUserBorrows({ page: 0, size: 5 })
      recentBorrows.value = response.content
    }
  } catch (error) {
    console.error('Error loading recent borrows:', error)
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
      loadRecentBorrows(),
      loadRecentNotices()
    ])
  } finally {
    isLoading.value = false
  }
}

const getBorrowStatusBadge = (borrow: Borrow) => {
  if (borrow.isReturned) {
    return { variant: 'success' as const, text: 'Returned' }
  }
  
  const dueDate = new Date(borrow.dueDate)
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

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

// Lifecycle
onMounted(() => {
  loadDashboardData()
})
</script>

<template>
  <div class="dashboard space-y-6">
    <!-- Welcome Section -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold">Welcome back{{ authStore.user?.username ? ', ' + authStore.user.username : '' }}!</h1>
        <p class="text-muted-foreground">Here's what's happening in your library today.</p>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-8">
      Loading dashboard...
    </div>

    <template v-else>
      <!-- Statistics Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card v-for="card in dashboardCards" :key="card.title" class="hover:shadow-lg transition-shadow">
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
          <CardDescription>Common tasks and shortcuts</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <Button
              v-for="action in quickActions"
              :key="action.title"
              :variant="action.variant"
              class="h-auto p-4 flex flex-col items-center gap-2"
              @click="action.action"
              :class="{ 'hidden': action.adminOnly && !isAdmin }"
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
        <!-- Recent Books -->
        <Card>
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Recently Added Books</CardTitle>
              <CardDescription>Latest additions to the library</CardDescription>
            </div>
            <Button variant="outline" size="sm" @click="router.push('/books')">
              <Eye class="h-4 w-4 mr-2" />
              View All
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
                    {{ book.authors.map(a => a.name).join(', ') }}
                  </p>
                </div>
                <Badge :variant="book.availableQuantity > 0 ? 'success' : 'destructive'" size="sm">
                  {{ book.availableQuantity > 0 ? 'Available' : 'Out of Stock' }}
                </Badge>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Recent Borrows -->
        <Card>
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Your Recent Borrows</CardTitle>
              <CardDescription>Your borrowing activity</CardDescription>
            </div>
            <Button variant="outline" size="sm" @click="router.push('/borrows')">
              <Eye class="h-4 w-4 mr-2" />
              View All
            </Button>
          </CardHeader>
          <CardContent>
            <div v-if="!authStore.isAuthenticated" class="text-center py-4 text-muted-foreground">
              Please log in to view your borrows
            </div>
            <div v-else-if="recentBorrows.length === 0" class="text-center py-4 text-muted-foreground">
              No recent borrows found
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="borrow in recentBorrows"
                :key="borrow.id"
                class="flex items-center space-x-3 p-2 rounded-lg hover:bg-muted/50"
              >
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium truncate">{{ borrow.bookCopy.book.title }}</p>
                  <p class="text-xs text-muted-foreground">
                    Due: {{ formatDate(borrow.dueDate) }}
                  </p>
                </div>
                <Badge 
                  :variant="getBorrowStatusBadge(borrow).variant" 
                  size="sm"
                >
                  {{ getBorrowStatusBadge(borrow).text }}
                </Badge>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Recent Notices -->
        <Card class="lg:col-span-2">
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Recent Notices</CardTitle>
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
                class="border-l-4 border-primary pl-4 py-2"
              >
                <h4 class="font-medium">{{ notice.title }}</h4>
                <p class="text-sm text-muted-foreground mt-1 line-clamp-2">
                  {{ notice.content }}
                </p>
                <p class="text-xs text-muted-foreground mt-2">
                  {{ formatDate(notice.publishDate) }}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
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
