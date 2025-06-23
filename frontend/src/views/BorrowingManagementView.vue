<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useLanguage } from '@/composables/useLanguage'
import { useI18n } from 'vue-i18n'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  AlertTriangle,
  ArrowLeft,
  BookOpen,
  Calendar,
  CheckCircle,
  ChevronDown,
  Clock,
  CreditCard,
  RefreshCw,
  Search,
} from 'lucide-vue-next'
import { booksService, borrowService, feesService } from '@/lib/api'
import type { Book as BookType, Borrow, FeeResponseDto, PagedResponse } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

// Data
const borrows = ref<Borrow[]>([])
const fees = ref<FeeResponseDto[]>([])
const books = ref<BookType[]>([])
const isLoading = ref(false)
const isReturning = ref<number | null>(null)
const searchKeyword = ref('')
const statusFilter = ref('all')
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(10)

// Filter options - computed to make them reactive to language changes
const statusOptions = computed(() => [
  { value: 'all', label: t('borrowing.filters.status.all') },
  { value: 'active', label: t('borrowing.filters.status.active') },
  { value: 'returned', label: t('borrowing.filters.status.returned') },
  { value: 'overdue', label: t('borrowing.filters.status.overdue') },
])

// Computed
const filteredBorrows = computed(() => {
  let filtered = borrows.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(
      (borrow) =>
        borrow.bookTitle.toLowerCase().includes(keyword) ||
        borrow.isbn.toLowerCase().includes(keyword),
    )
  }

  if (statusFilter.value !== 'all') {
    filtered = filtered.filter((borrow) => {
      switch (statusFilter.value) {
        case 'active':
          return borrow.status === 'BORROWED' && !isOverdue(borrow)
        case 'returned':
          return borrow.status === 'RETURNED'
        case 'overdue':
          return borrow.status === 'BORROWED' && isOverdue(borrow)
        default:
          return true
      }
    })
  }

  return filtered
})

const activeBorrows = computed(() => borrows.value.filter((borrow) => borrow.status === 'BORROWED'))

const overdueBorrows = computed(() =>
  borrows.value.filter((borrow) => borrow.status === 'BORROWED' && isOverdue(borrow)),
)

const totalFees = computed(() =>
  fees.value.reduce((sum, fee) => sum + (fee.paid ? 0 : fee.amount), 0),
)

// Map ISBN to cover URL for displaying book covers
const bookCoverMap = computed(() => {
  const map = new Map<string, string>()
  books.value.forEach((book) => {
    if (book.coverURL) {
      map.set(book.isbn, book.coverURL)
    }
  })
  return map
})

// Cache for on-demand loaded book covers
const coverCache = ref(new Map<string, string>())

// Get book cover URL by ISBN with on-demand loading
const getBookCoverUrl = async (isbn: string): Promise<string | undefined> => {
  // Check if we already have it in the main book map
  if (bookCoverMap.value.has(isbn)) {
    return bookCoverMap.value.get(isbn)
  }

  // Check if we already cached it
  if (coverCache.value.has(isbn)) {
    return coverCache.value.get(isbn)
  }

  // Try to find the book by ISBN and load its cover
  try {
    const book = await booksService.getByIsbn(isbn)
    if (book?.coverURL) {
      coverCache.value.set(isbn, book.coverURL)
      return book.coverURL
    }
  } catch (error) {
    console.error('Error loading book cover for ISBN:', isbn, error)
  }

  return undefined
}

// Synchronous version for template usage (uses cached values only)
const getBookCoverUrlSync = (isbn: string): string | undefined => {
  return bookCoverMap.value.get(isbn) || coverCache.value.get(isbn)
}

// Methods
const isOverdue = (borrow: Borrow) => {
  return new Date(borrow.returnTime) < new Date()
}

const getDaysUntilDue = (borrow: Borrow) => {
  const dueDate = new Date(borrow.returnTime)
  const now = new Date()
  return Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
}

const getBorrowStatusBadge = (borrow: Borrow) => {
  if (borrow.status === 'RETURNED') {
    return { variant: 'success' as const, text: t('borrowing.status.returned'), icon: CheckCircle }
  }

  if (borrow.status === 'OVERDUE' || (borrow.status === 'BORROWED' && isOverdue(borrow))) {
    return { variant: 'destructive' as const, text: t('borrowing.status.overdue'), icon: AlertTriangle }
  }

  if (borrow.status === 'BORROWED') {
    const daysUntilDue = getDaysUntilDue(borrow)
    if (daysUntilDue <= 3) {
      return { variant: 'secondary' as const, text: t('borrowing.status.dueSoon'), icon: Clock }
    }
    return { variant: 'default' as const, text: t('borrowing.status.active'), icon: BookOpen }
  }

  return { variant: 'default' as const, text: borrow.status, icon: BookOpen }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

// Function to preload missing book covers
const preloadMissingCovers = async (borrowList: Borrow[]) => {
  const missingIsbns = borrowList
    .map((borrow) => borrow.isbn)
    .filter((isbn) => !bookCoverMap.value.has(isbn) && !coverCache.value.has(isbn))

  // Remove duplicates
  const uniqueIsbns = [...new Set(missingIsbns)]

  // Load covers for missing ISBNs in parallel
  await Promise.allSettled(uniqueIsbns.map((isbn) => getBookCoverUrl(isbn)))
}

const loadBorrows = async () => {
  try {
    isLoading.value = true
    const response: PagedResponse<Borrow> = await borrowService.getUserBorrows({
      page: currentPage.value,
      size: pageSize.value,
    })
    borrows.value = response.content
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements

    // Preload any missing book covers
    await preloadMissingCovers(response.content)
  } catch (error) {
    console.error('Error loading borrows:', error)
    toast.error(t('borrowing.messages.loadError'))
  } finally {
    isLoading.value = false
  }
}

const loadBooks = async () => {
  try {
    // Load books to ensure we have cover URLs for borrow records
    let allBooks: BookType[] = []
    let page = 0
    const size = 100
    let hasMore = true

    while (hasMore) {
      const response = await booksService.getAll({ page, size })
      allBooks = [...allBooks, ...response.content]
      hasMore = response.content.length === size && !response.last
      page++
    }

    books.value = allBooks
  } catch (error) {
    console.error('Error loading books:', error)
    // Don't show toast error for this as it's not critical for the user
  }
}

const loadFees = async () => {
  try {
    // This would need to be implemented in the backend
    // For now, we'll use a mock implementation
    fees.value = []
  } catch (error) {
    console.error('Error loading fees:', error)
  }
}

const handleReturnBook = async (borrowId: number) => {
  try {
    isReturning.value = borrowId
    await borrowService.returnBook(borrowId)
    toast.success(t('borrowing.messages.returnSuccess'))
    await loadBorrows()
    await loadFees()
  } catch (error) {
    console.error('Error returning book:', error)
    toast.error(t('borrowing.messages.returnError'))
  } finally {
    isReturning.value = null
  }
}

const handleRenewBook = async (borrowId: number) => {
  try {
    await borrowService.renewBook(borrowId)
    toast.success(t('borrowing.messages.renewSuccess'))
    await loadBorrows()
  } catch (error) {
    console.error('Error renewing book:', error)
    toast.error(t('borrowing.messages.renewError'))
  }
}

const handlePayFee = async (feeId: number) => {
  try {
    await feesService.payFee(feeId)
    toast.success(t('borrowing.messages.paySuccess'))
    await loadFees()
  } catch (error) {
    console.error('Error paying fee:', error)
    toast.error(t('borrowing.messages.payError'))
  }
}

const goToBookDetail = (bookId: number) => {
  router.push(`/books/${bookId}`)
}

const handlePageChange = (page: number) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    loadBorrows()
  }
}

// Lifecycle
onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  loadBooks()
  loadBorrows()
  loadFees()
})
</script>

<template>
  <div class="borrowing-management space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold">{{ t('borrowing.title') }}</h1>
        <p class="text-muted-foreground">{{ t('borrowing.description') }}</p>
      </div>
      <Button @click="router.push('/books')" variant="outline">
        <BookOpen class="h-4 w-4 mr-2" />
        {{ t('borrowing.buttons.browseBooks') }}
      </Button>
    </div>

    <!-- Summary Cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">{{ t('borrowing.summary.activeBorrows.title') }}</CardTitle>
          <BookOpen class="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ activeBorrows.length }}</div>
          <p class="text-xs text-muted-foreground">{{ t('borrowing.summary.activeBorrows.description') }}</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">{{ t('borrowing.summary.overdueBooks.title') }}</CardTitle>
          <AlertTriangle class="h-4 w-4 text-red-500" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-red-600">{{ overdueBorrows.length }}</div>
          <p class="text-xs text-muted-foreground">{{ t('borrowing.summary.overdueBooks.description') }}</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">{{ t('borrowing.summary.outstandingFees.title') }}</CardTitle>
          <CreditCard class="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">
            {{ totalFees > 0 ? `$${totalFees.toFixed(2)}` : '$0.00' }}
          </div>
          <p class="text-xs text-muted-foreground">{{ t('borrowing.summary.outstandingFees.description') }}</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filters -->
    <Card class="p-6">
      <div class="flex flex-wrap gap-4">
        <!-- Search -->
        <div class="flex flex-col gap-2 flex-1 min-w-[200px]">
          <Label for="search">{{ t('borrowing.filters.search.label') }}</Label>
          <div class="relative">
            <Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input id="search" v-model="searchKeyword" :placeholder="t('borrowing.filters.search.placeholder')"
              class="pl-10" />
          </div>
        </div>

        <!-- Status Filter -->
        <div class="flex flex-col gap-2">
          <Label for="status">{{ t('borrowing.filters.status.label') }}</Label>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" class="w-48 justify-between">
                {{
                  statusOptions.find((opt) => opt.value === statusFilter)?.label || t('borrowing.filters.status.all')
                }}
                <ChevronDown class="ml-2 h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem v-for="option in statusOptions" :key="option.value"
                @click="statusFilter = option.value">
                {{ option.label }}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <!-- Refresh Button -->
        <div class="flex flex-col justify-end gap-2">
          <Button @click="loadBorrows" variant="outline">
            <RefreshCw class="h-4 w-4" />
          </Button>
        </div>
      </div>
    </Card>

    <!-- Outstanding Fees (if any) -->
    <Card v-if="fees.length > 0 && totalFees > 0">
      <CardHeader>
        <CardTitle class="flex items-center gap-2 text-red-600">
          <AlertTriangle class="h-5 w-5" />
          {{ t('borrowing.fees.title') }}
        </CardTitle>
        <CardDescription>{{ t('borrowing.fees.description') }}</CardDescription>
      </CardHeader>
      <CardContent>
        <div class="space-y-3">
          <div v-for="fee in fees.filter((f) => !f.paid)" :key="fee.id"
            class="flex items-center justify-between p-3 border rounded-lg">
            <div>
              <p class="font-medium">{{ t('borrowing.fees.borrowId', { id: fee.borrowId }) }}</p>
              <p class="text-sm text-muted-foreground">
                {{ t('borrowing.fees.feeDate', { date: formatDate(fee.calculationDate) }) }}
              </p>
            </div>
            <div class="flex items-center gap-3">
              <span class="font-bold text-red-600">${{ fee.amount.toFixed(2) }}</span>
              <Button size="sm" @click="handlePayFee(fee.id)">{{ t('borrowing.buttons.payNow') }}</Button>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Borrows List -->
    <Card>
      <CardHeader>
        <CardTitle>{{ t('borrowing.history.title') }}</CardTitle>
        <CardDescription>
          {{ t('borrowing.history.showing', { count: filteredBorrows.length, total: totalElements }) }}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div v-if="isLoading" class="text-center py-8">{{ t('borrowing.loading') }}</div>

        <div v-else-if="filteredBorrows.length === 0" class="text-center py-8 text-muted-foreground">
          {{ t('borrowing.empty.noBorrows') }}
        </div>

        <div v-else class="space-y-4">
          <div v-for="borrow in filteredBorrows" :key="borrow.borrowId"
            class="border rounded-lg p-4 hover:bg-muted/50 transition-colors">
            <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
              <!-- Book Info -->
              <div class="flex items-start gap-4 flex-1">
                <div class="w-16 h-20 bg-muted rounded flex items-center justify-center overflow-hidden">
                  <img v-if="getBookCoverUrlSync(borrow.isbn)" :src="getBookCoverUrlSync(borrow.isbn)"
                    :alt="borrow.bookTitle" class="w-full h-full object-cover"
                    @error="($event.target as HTMLImageElement).style.display = 'none'" />
                  <BookOpen v-else class="h-6 w-6 text-muted-foreground" />
                </div>

                <div class="flex-1 min-w-0">
                  <h3 class="font-medium truncate">
                    {{ borrow.bookTitle }}
                  </h3>
                  <p class="text-xs text-muted-foreground">{{ t('borrowing.history.isbn', { isbn: borrow.isbn }) }}</p>
                </div>
              </div>

              <!-- Dates and Status -->
              <div class="flex flex-col lg:items-end gap-2">
                <Badge :variant="getBorrowStatusBadge(borrow).variant" class="w-fit">
                  <component :is="getBorrowStatusBadge(borrow).icon" class="h-3 w-3 mr-1" />
                  {{ getBorrowStatusBadge(borrow).text }}
                </Badge>

                <div class="text-sm text-muted-foreground">
                  <div class="flex items-center gap-1">
                    <Calendar class="h-3 w-3" />
                    {{ t('borrowing.history.borrowed', { date: formatDate(borrow.borrowTime) }) }}
                  </div>
                  <div class="flex items-center gap-1">
                    <Clock class="h-3 w-3" />
                    {{ t('borrowing.history.due', { date: formatDate(borrow.returnTime) }) }}
                    <span v-if="borrow.status === 'BORROWED'" class="ml-1">
                      ({{
                        getDaysUntilDue(borrow) > 0
                          ? t('borrowing.history.daysLeft', { days: getDaysUntilDue(borrow) })
                          : t('borrowing.history.daysOverdue', { days: Math.abs(getDaysUntilDue(borrow)) })
                      }})
                    </span>
                  </div>
                  <div v-if="borrow.actualReturnTime" class="flex items-center gap-1">
                    <CheckCircle class="h-3 w-3" />
                    {{ t('borrowing.history.returned', { date: formatDate(borrow.actualReturnTime) }) }}
                  </div>
                </div>
              </div>

              <!-- Actions -->
              <div v-if="borrow.status === 'BORROWED'" class="flex gap-2">
                <Button size="sm" variant="outline" @click="handleRenewBook(borrow.borrowId)">
                  <RefreshCw class="h-3 w-3 mr-1" />
                  {{ t('borrowing.buttons.renew') }}
                </Button>
                <Button size="sm" @click="handleReturnBook(borrow.borrowId)"
                  :disabled="isReturning === borrow.borrowId">
                  <CheckCircle class="h-3 w-3 mr-1" />
                  {{ isReturning === borrow.borrowId ? t('borrowing.buttons.returning') : t('borrowing.buttons.return')
                  }}
                </Button>
              </div>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="flex justify-center items-center gap-2 mt-6">
          <Button variant="outline" size="sm" :disabled="currentPage === 0" @click="handlePageChange(currentPage - 1)">
            <ArrowLeft class="h-4 w-4" />
            {{ t('borrowing.pagination.previous') }}
          </Button>

          <span class="text-sm text-muted-foreground px-4">
            {{ t('borrowing.pagination.page', { current: currentPage + 1, total: totalPages }) }}
          </span>

          <Button variant="outline" size="sm" :disabled="currentPage === totalPages - 1"
            @click="handlePageChange(currentPage + 1)">
            {{ t('borrowing.pagination.next') }}
            <ArrowLeft class="h-4 w-4 rotate-180" />
          </Button>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<style scoped>
.borrowing-management {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
