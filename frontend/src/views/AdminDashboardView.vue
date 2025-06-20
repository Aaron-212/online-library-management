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
  Settings,
  Eye,
  Download,
  UserCheck,
  DollarSign,
  Bell,
  BarChart3
} from 'lucide-vue-next'
import { statisticsService, booksService, borrowService, noticesService, usersService } from '@/lib/api'
import type { BookStatisticsDto, Book, Borrow, Notice, User } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()

// Data
const isLoading = ref(false)
const statistics = ref<BookStatisticsDto | null>(null)
const recentBooks = ref<Book[]>([])
const recentBorrows = ref<Borrow[]>([])
const recentNotices = ref<Notice[]>([])
const systemStats = ref({
  totalUsers: 0,
  newUsersThisMonth: 0,
  activeLoans: 0,
  overdueBooks: 0,
  totalRevenue: 0,
  pendingReturns: 0
})

// Computed
const adminStats = computed(() => [
  {
    title: 'Total Users',
    value: systemStats.value.totalUsers,
    description: 'Registered users',
    icon: Users,
    color: 'text-blue-600',
    bgColor: 'bg-blue-50',
  },
  {
    title: 'Active Loans',
    value: systemStats.value.activeLoans,
    description: 'Currently borrowed',
    icon: BookOpen,
    color: 'text-green-600',
    bgColor: 'bg-green-50',
  },
  {
    title: 'Overdue Books',
    value: systemStats.value.overdueBooks,
    description: 'Need attention',
    icon: AlertTriangle,
    color: 'text-red-600',
    bgColor: 'bg-red-50',
  },
  {
    title: 'Total Books',
    value: statistics.value?.totalBooks || 0,
    description: 'In library',
    icon: BookOpen,
    color: 'text-purple-600',
    bgColor: 'bg-purple-50',
  }
])

const adminActions = [
  {
    title: 'Add New Book',
    description: 'Add books to library',
    icon: Plus,
    action: () => router.push('/books/create'),
    variant: 'default' as const
  },
  {
    title: 'Manage Users',
    description: 'User administration',
    icon: UserCheck,
    action: () => router.push('/admin/users'),
    variant: 'default' as const
  },
  {
    title: 'Borrowing Rules',
    description: 'Configure borrowing',
    icon: Settings,
    action: () => router.push('/admin/borrowing-rules'),
    variant: 'outline' as const
  },
  {
    title: 'Fee Management',
    description: 'Manage fees & fines',
    icon: DollarSign,
    action: () => router.push('/admin/fees'),
    variant: 'outline' as const
  },
  {
    title: 'System Reports',
    description: 'Generate reports',
    icon: BarChart3,
    action: () => router.push('/admin/reports'),
    variant: 'outline' as const
  },
  {
    title: 'Manage Notices',
    description: 'System announcements',
    icon: Bell,
    action: () => router.push('/notices'),
    variant: 'outline' as const
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

const loadSystemStats = async () => {
  try {
    // These would be actual API calls to get system-wide statistics
    // For now, using mock data based on available statistics
    systemStats.value = {
      totalUsers: 150, // This would come from a real API
      newUsersThisMonth: 12,
      activeLoans: statistics.value?.activeBorrows || 0,
      overdueBooks: statistics.value?.overdueBorrows || 0,
      totalRevenue: 2450,
      pendingReturns: 8
    }
  } catch (error) {
    console.error('Error loading system stats:', error)
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
    // For admin, show overdue borrowings that need attention
    const response = await borrowService.getOverdueBorrows({ page: 0, size: 5 })
    recentBorrows.value = response.content
  } catch (error) {
    console.error('Error loading overdue borrows:', error)
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
    await loadSystemStats() // Load after statistics to use the data
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
  // Ensure user is admin
  if (!authStore.isAdmin()) {
    router.push('/dashboard')
    return
  }
  loadDashboardData()
})
</script>

<template>
  <div class="admin-dashboard space-y-6">
    <!-- Welcome Section -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold">Admin Dashboard</h1>
        <p class="text-muted-foreground">System overview and administrative controls</p>
      </div>
      <Badge variant="secondary" class="text-sm px-3 py-1">
        Administrator
      </Badge>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-8">
      Loading admin dashboard...
    </div>

    <template v-else>
      <!-- Admin Statistics Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card v-for="card in adminStats" :key="card.title" class="hover:shadow-lg transition-shadow">
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

      <!-- Admin Quick Actions -->
      <Card>
        <CardHeader>
          <CardTitle>Administrative Actions</CardTitle>
          <CardDescription>Common administrative tasks and system management</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <Button
              v-for="action in adminActions"
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
        <!-- Recent Books -->
        <Card>
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Recently Added Books</CardTitle>
              <CardDescription>Latest additions to the library</CardDescription>
            </div>
            <Button variant="outline" size="sm" @click="router.push('/admin/books')">
              <Eye class="h-4 w-4 mr-2" />
              Manage All
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
                <div class="flex flex-col items-end gap-1">
                  <Badge :variant="book.availableQuantity > 0 ? 'success' : 'destructive'" size="sm">
                    {{ book.availableQuantity }} / {{ book.totalQuantity }}
                  </Badge>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Overdue Borrows -->
        <Card>
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Overdue Borrowings</CardTitle>
              <CardDescription>Books that need admin attention</CardDescription>
            </div>
            <Button variant="outline" size="sm" @click="router.push('/admin/borrowing')">
              <Eye class="h-4 w-4 mr-2" />
              Manage All
            </Button>
          </CardHeader>
          <CardContent>
            <div v-if="recentBorrows.length === 0" class="text-center py-4 text-muted-foreground">
              No overdue borrowings
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
                    User: {{ borrow.user.username }} • Due: {{ formatDate(borrow.dueDate) }}
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

        <!-- System Notices -->
        <Card class="lg:col-span-2">
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>System Notices</CardTitle>
              <CardDescription>Manage announcements and system messages</CardDescription>
            </div>
            <div class="flex gap-2">
              <Button variant="outline" size="sm" @click="router.push('/notices')">
                <Plus class="h-4 w-4 mr-2" />
                Create Notice
              </Button>
              <Button variant="outline" size="sm" @click="router.push('/notices')">
                <Eye class="h-4 w-4 mr-2" />
                Manage All
              </Button>
            </div>
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
                <div class="flex justify-between items-start">
                  <div class="flex-1">
                    <h4 class="font-medium">{{ notice.title }}</h4>
                    <p class="text-sm text-muted-foreground mt-1 line-clamp-2">
                      {{ notice.content }}
                    </p>
                    <p class="text-xs text-muted-foreground mt-2">
                      Published: {{ formatDate(notice.publishDate) }}
                    </p>
                  </div>
                  <Button variant="ghost" size="sm" @click="router.push(`/notices/${notice.id}/edit`)">
                    Edit
                  </Button>
                </div>
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
