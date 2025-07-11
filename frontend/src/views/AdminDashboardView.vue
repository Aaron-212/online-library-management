<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  AlertTriangle,
  BarChart3,
  Bell,
  BookOpen,
  DollarSign,
  Eye,
  Plus,
  Settings,
  UserCheck,
  Users,
} from 'lucide-vue-next'
import { booksService, borrowService, noticesService, statisticsService } from '@/lib/api'
import type { BookSummaryDto, LibraryStatisticsDto, Borrow, Notice } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

// Data
const isLoading = ref(false)
const statistics = ref<LibraryStatisticsDto | null>(null)
const recentBooks = ref<BookSummaryDto[]>([])
const recentBorrows = ref<Borrow[]>([])
const recentNotices = ref<Notice[]>([])
const inventoryStats = ref<{
  [categoryName: string]: { totalCount: number; availableCount: number }
}>({})
const systemStats = ref({
  totalUsers: 0,
  newUsersThisMonth: 0,
  activeLoans: 0,
  overdueBooks: 0,
  totalRevenue: 0,
  pendingReturns: 0,
})

// Computed
const adminStats = computed(() => [
  {
    title: t('adminDashboard.stats.totalUsers.title'),
    value: systemStats.value.totalUsers,
    description: t('adminDashboard.stats.totalUsers.description'),
    icon: Users,
    color: 'text-blue-600',
    bgColor: 'bg-blue-50',
  },
  {
    title: t('adminDashboard.stats.activeLoans.title'),
    value: systemStats.value.activeLoans,
    description: t('adminDashboard.stats.activeLoans.description'),
    icon: BookOpen,
    color: 'text-green-600',
    bgColor: 'bg-green-50',
  },
  {
    title: t('adminDashboard.stats.overdueBooks.title'),
    value: systemStats.value.overdueBooks,
    description: t('adminDashboard.stats.overdueBooks.description'),
    icon: AlertTriangle,
    color: 'text-red-600',
    bgColor: 'bg-red-50',
  },
  {
    title: t('adminDashboard.stats.totalBooks.title'),
    value: statistics.value?.totalBooks || 0,
    description: t('adminDashboard.stats.totalBooks.description'),
    icon: BookOpen,
    color: 'text-purple-600',
    bgColor: 'bg-purple-50',
  },
])

const formattedInventoryStats = computed(() => {
  return Object.entries(inventoryStats.value).map(([category, stats]) => ({
    category,
    total: stats.totalCount,
    available: stats.availableCount,
    borrowed: stats.totalCount - stats.availableCount,
    availabilityRate:
      stats.totalCount > 0 ? Math.round((stats.availableCount / stats.totalCount) * 100) : 0,
  }))
})

const adminActions = computed(() => [
  {
    title: t('adminDashboard.actions.addNewBook.title'),
    description: t('adminDashboard.actions.addNewBook.description'),
    icon: Plus,
    action: () => router.push('/books/create'),
    variant: 'default' as const,
  },
  {
    title: t('adminDashboard.actions.manageUsers.title'),
    description: t('adminDashboard.actions.manageUsers.description'),
    icon: UserCheck,
    action: () => router.push('/admin/users'),
    variant: 'default' as const,
  },
  {
    title: t('adminDashboard.actions.borrowingRules.title'),
    description: t('adminDashboard.actions.borrowingRules.description'),
    icon: Settings,
    action: () => router.push('/admin/borrowing-rules'),
    variant: 'outline' as const,
  },
  {
    title: t('adminDashboard.actions.feeManagement.title'),
    description: t('adminDashboard.actions.feeManagement.description'),
    icon: DollarSign,
    action: () => router.push('/admin/fees'),
    variant: 'outline' as const,
  },
  {
    title: t('adminDashboard.actions.systemReports.title'),
    description: t('adminDashboard.actions.systemReports.description'),
    icon: BarChart3,
    action: () => router.push('/admin/reports'),
    variant: 'outline' as const,
  },
  {
    title: t('adminDashboard.actions.manageNotices.title'),
    description: t('adminDashboard.actions.manageNotices.description'),
    icon: Bell,
    action: () => router.push('/notices'),
    variant: 'outline' as const,
  },
])

// Methods
const loadStatistics = async () => {
  try {
    statistics.value = await statisticsService.getBookStatistics()
  } catch (error) {
    console.error('Error loading statistics:', error)
    toast.error(t('adminDashboard.messages.loadStatisticsError'))
  }
}

const loadSystemStats = async () => {
  try {
    // Fetch real system statistics from backend
    const userBehaviorStats = await statisticsService.getUserBehaviorAnalysis().catch((error) => {
      console.error('Error loading user behavior stats:', error)
      return { totalUserCount: 0, registrationCount: 0, activeUserCount: 0 }
    })

    systemStats.value = {
      totalUsers: userBehaviorStats.totalUserCount || 0,
      newUsersThisMonth: userBehaviorStats.registrationCount || 0,
      activeLoans: statistics.value?.activeBorrows || 0,
      overdueBooks: statistics.value?.overdueBorrows || 0,
      totalRevenue: 0, // TODO: Calculate from fees when fee statistics endpoint is available
      pendingReturns: 0, // TODO: Add endpoint for pending returns count
    }
  } catch (error) {
    console.error('Error loading system stats:', error)
    toast.error(t('adminDashboard.messages.loadSystemStatsError'))
    // Fallback to minimal stats using already loaded statistics
    systemStats.value = {
      totalUsers: 0,
      newUsersThisMonth: 0,
      activeLoans: statistics.value?.activeBorrows || 0,
      overdueBooks: statistics.value?.overdueBorrows || 0,
      totalRevenue: 0,
      pendingReturns: 0,
    }
  }
}

const loadRecentBooks = async () => {
  try {
    const response = await booksService.getAllSummary({ page: 0, size: 5, sort: 'id,desc' })
    recentBooks.value = response.content || []
  } catch (error) {
    console.error('Error loading recent books:', error)
    recentBooks.value = []
  }
}

const loadRecentBorrows = async () => {
  try {
    // For admin, show overdue borrowings that need attention
    const response = await borrowService.getOverdueBorrows({ page: 0, size: 5 })
    recentBorrows.value = response.slice(0, 5) // Take first 5 items since API doesn't paginate
  } catch (error) {
    console.error('Error loading overdue borrows:', error)
    recentBorrows.value = [] // Ensure the ref stays as an array even on error
  }
}

const loadRecentNotices = async () => {
  try {
    const response = await noticesService.getAllForAdmin({ page: 0, size: 3 })
    recentNotices.value = response.content || []
  } catch (error) {
    console.error('Error loading recent notices:', error)
    recentNotices.value = []
  }
}

const loadInventoryStats = async () => {
  try {
    inventoryStats.value = await statisticsService.getBookInventoryStatistics()
  } catch (error) {
    console.error('Error loading inventory statistics:', error)
    inventoryStats.value = {}
  }
}

const loadDashboardData = async () => {
  isLoading.value = true
  try {
    await Promise.all([
      loadStatistics(),
      loadRecentBooks(),
      loadRecentBorrows(),
      loadRecentNotices(),
      loadInventoryStats(),
    ])
    await loadSystemStats() // Load after statistics to use the data
  } finally {
    isLoading.value = false
  }
}

const getBorrowStatusBadge = (borrow: Borrow) => {
  if (borrow.status === 'RETURNED') {
    return { variant: 'success' as const, text: t('adminDashboard.status.returned') }
  }

  if (borrow.status === 'BORROWED') {
    const dueDate = new Date(borrow.returnTime)
    const now = new Date()

    if (dueDate < now) {
      return { variant: 'destructive' as const, text: t('adminDashboard.status.overdue') }
    }

    const daysUntilDue = Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
    if (daysUntilDue <= 3) {
      return { variant: 'secondary' as const, text: t('adminDashboard.status.dueSoon') }
    }

    return { variant: 'default' as const, text: t('adminDashboard.status.active') }
  }

  return { variant: 'default' as const, text: borrow.status }
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
        <h1 class="text-3xl font-bold">{{ t('adminDashboard.title') }}</h1>
        <p class="text-muted-foreground">{{ t('adminDashboard.subtitle') }}</p>
      </div>
      <Badge variant="secondary" class="text-sm px-3 py-1">{{
        t('adminDashboard.administrator')
      }}</Badge>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-8">{{ t('adminDashboard.loading') }}</div>

    <template v-else>
      <!-- Admin Statistics Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card
          v-for="card in adminStats"
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
            <div class="text-2xl font-bold">
              {{
                typeof card.value === 'number' ? card.value.toLocaleString() : (card.value ?? '-')
              }}
            </div>
            <p class="text-xs text-muted-foreground">{{ card.description }}</p>
          </CardContent>
        </Card>
      </div>

      <!-- Admin Quick Actions -->
      <Card>
        <CardHeader>
          <CardTitle>{{ t('adminDashboard.actions.title') }}</CardTitle>
          <CardDescription>{{ t('adminDashboard.actions.description') }}</CardDescription>
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
              <CardTitle>{{ t('adminDashboard.sections.recentBooks.title') }}</CardTitle>
              <CardDescription>{{
                t('adminDashboard.sections.recentBooks.description')
              }}</CardDescription>
            </div>
            <Button variant="outline" size="sm" @click="router.push('/admin/books')">
              <Eye class="h-4 w-4 mr-2" />
              {{ t('adminDashboard.sections.recentBooks.manageAll') }}
            </Button>
          </CardHeader>
          <CardContent>
            <div
              v-if="!recentBooks || recentBooks.length === 0"
              class="text-center py-4 text-muted-foreground"
            >
              {{ t('adminDashboard.sections.recentBooks.noBooks') }}
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="book in recentBooks"
                :key="book.id"
                class="flex items-center space-x-3 p-2 rounded-lg hover:bg-muted/50 cursor-pointer"
                @click="router.push(`/books/${book.id}`)"
              >
                <!-- Book Cover -->
                <div
                  class="flex-shrink-0 w-12 h-16 bg-muted rounded overflow-hidden flex items-center justify-center"
                >
                  <img
                    v-if="book.coverURL"
                    :src="book.coverURL"
                    :alt="`Cover for ${book.title || 'Unknown book'}`"
                    class="w-full h-full object-cover"
                    @error="
                      (event) => {
                        if (event.target) (event.target as HTMLImageElement).style.display = 'none'
                      }
                    "
                  />
                  <BookOpen v-else class="h-6 w-6 text-muted-foreground" />
                </div>

                <!-- Book Info -->
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium truncate">{{ book.title || 'Unknown' }}</p>
                  <p class="text-xs text-muted-foreground truncate">
                    {{
                      Array.isArray(book.authors) && book.authors.length > 0
                        ? book.authors.join(', ')
                        : t('adminDashboard.sections.recentBooks.unknownAuthor')
                    }}
                  </p>
                </div>

                <!-- Availability Badge -->
                <div class="flex flex-col items-end gap-1">
                  <Badge
                    :variant="(book.availableQuantity || 0) > 0 ? 'success' : 'destructive'"
                    size="sm"
                  >
                    {{ book.availableQuantity || 0 }} / {{ book.totalQuantity || 0 }}
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
              <CardTitle>{{ t('adminDashboard.sections.overdueBorrows.title') }}</CardTitle>
              <CardDescription>{{
                t('adminDashboard.sections.overdueBorrows.description')
              }}</CardDescription>
            </div>
            <Button variant="outline" size="sm" @click="router.push('/admin/borrowing')">
              <Eye class="h-4 w-4 mr-2" />
              {{ t('adminDashboard.sections.overdueBorrows.manageAll') }}
            </Button>
          </CardHeader>
          <CardContent>
            <div
              v-if="!recentBorrows || recentBorrows.length === 0"
              class="text-center py-4 text-muted-foreground"
            >
              {{ t('adminDashboard.sections.overdueBorrows.noOverdue') }}
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="borrow in recentBorrows"
                :key="borrow.borrowId"
                class="flex items-center space-x-3 p-2 rounded-lg hover:bg-muted/50"
              >
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium truncate">
                    {{
                      borrow.bookTitle || t('adminDashboard.sections.overdueBorrows.unknownBook')
                    }}
                  </p>
                  <p class="text-xs text-muted-foreground">
                    {{ t('adminDashboard.sections.overdueBorrows.userPrefix')
                    }}{{
                      borrow.username || t('adminDashboard.sections.overdueBorrows.unknownUser')
                    }}
                    • {{ t('adminDashboard.sections.overdueBorrows.duePrefix')
                    }}{{ borrow.returnTime ? formatDate(borrow.returnTime) : 'N/A' }}
                  </p>
                </div>
                <Badge :variant="getBorrowStatusBadge(borrow).variant" size="sm">
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
              <CardTitle>{{ t('adminDashboard.sections.systemNotices.title') }}</CardTitle>
              <CardDescription>{{
                t('adminDashboard.sections.systemNotices.description')
              }}</CardDescription>
            </div>
            <div class="flex gap-2">
              <Button variant="outline" size="sm" @click="router.push('/notices')">
                <Plus class="h-4 w-4 mr-2" />
                {{ t('adminDashboard.sections.systemNotices.createNotice') }}
              </Button>
              <Button variant="outline" size="sm" @click="router.push('/notices')">
                <Eye class="h-4 w-4 mr-2" />
                {{ t('adminDashboard.sections.systemNotices.manageAll') }}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <div
              v-if="!recentNotices || recentNotices.length === 0"
              class="text-center py-4 text-muted-foreground"
            >
              {{ t('adminDashboard.sections.systemNotices.noNotices') }}
            </div>
            <div v-else class="space-y-4">
              <div
                v-for="notice in recentNotices"
                :key="notice.id"
                class="border-l-4 border-primary pl-4 py-2"
              >
                <div class="flex justify-between items-start">
                  <div class="flex-1">
                    <h4 class="font-medium">
                      {{ notice.title || t('adminDashboard.sections.systemNotices.untitled') }}
                    </h4>
                    <p class="text-sm text-muted-foreground mt-1 line-clamp-2">
                      {{ notice.content || t('adminDashboard.sections.systemNotices.noContent') }}
                    </p>
                    <p class="text-xs text-muted-foreground mt-2">
                      {{ t('adminDashboard.sections.systemNotices.publishedPrefix')
                      }}{{ notice.publishTime ? formatDate(notice.publishTime) : 'N/A' }}
                    </p>
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    @click="router.push(`/notices/${notice.id}/edit`)"
                  >
                    {{ t('adminDashboard.sections.systemNotices.edit') }}
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Book Inventory by Category -->
        <Card v-if="false" class="lg:col-span-2">
          <CardHeader class="flex flex-row items-center justify-between">
            <div>
              <CardTitle>{{ t('adminDashboard.sections.bookInventory.title') }}</CardTitle>
              <CardDescription>{{
                t('adminDashboard.sections.bookInventory.description')
              }}</CardDescription>
            </div>
            <Button variant="outline" size="sm" @click="router.push('/admin/books')">
              <Eye class="h-4 w-4 mr-2" />
              {{ t('adminDashboard.sections.bookInventory.manageInventory') }}
            </Button>
          </CardHeader>
          <CardContent>
            <div
              v-if="!formattedInventoryStats || formattedInventoryStats.length === 0"
              class="text-center py-4 text-muted-foreground"
            >
              {{ t('adminDashboard.sections.bookInventory.noData') }}
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="item in formattedInventoryStats"
                :key="item.category"
                class="flex items-center justify-between p-3 rounded-lg bg-muted/30 hover:bg-muted/50 transition-colors"
              >
                <div class="flex-1">
                  <h4 class="font-medium">{{ item.category }}</h4>
                  <div class="flex items-center gap-4 mt-1 text-sm text-muted-foreground">
                    <span
                      >{{ t('adminDashboard.sections.bookInventory.totalPrefix')
                      }}{{ item.total }}</span
                    >
                    <span
                      >{{ t('adminDashboard.sections.bookInventory.availablePrefix')
                      }}{{ item.available }}</span
                    >
                    <span
                      >{{ t('adminDashboard.sections.bookInventory.borrowedPrefix')
                      }}{{ item.borrowed }}</span
                    >
                  </div>
                </div>
                <div class="text-right">
                  <Badge
                    :variant="
                      item.availabilityRate >= 50
                        ? 'success'
                        : item.availabilityRate >= 25
                          ? 'secondary'
                          : 'destructive'
                    "
                    size="sm"
                  >
                    {{ item.availabilityRate
                    }}{{ t('adminDashboard.sections.bookInventory.availableSuffix') }}
                  </Badge>
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
