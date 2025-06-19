<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
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
  Clock,
  BookOpen,
  Calendar,
  AlertTriangle,
  CheckCircle,
  Search,
  ChevronDown,
  RefreshCw,
  CreditCard,
  ArrowLeft
} from 'lucide-vue-next'
import { borrowService, feesService } from '@/lib/api'
import type { Borrow, PagedResponse, FeeResponseDto } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()

// Data
const borrows = ref<Borrow[]>([])
const fees = ref<FeeResponseDto[]>([])
const isLoading = ref(false)
const isReturning = ref<number | null>(null)
const searchKeyword = ref('')
const statusFilter = ref('all')
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(10)

// Filter options
const statusOptions = [
  { value: 'all', label: 'All Borrows' },
  { value: 'active', label: 'Active' },
  { value: 'returned', label: 'Returned' },
  { value: 'overdue', label: 'Overdue' }
]

// Computed
const filteredBorrows = computed(() => {
  let filtered = borrows.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(borrow =>
      borrow.bookCopy.book.title.toLowerCase().includes(keyword) ||
      borrow.bookCopy.book.isbn.toLowerCase().includes(keyword) ||
      borrow.bookCopy.book.authors.some(author => 
        author.name.toLowerCase().includes(keyword)
      )
    )
  }

  if (statusFilter.value !== 'all') {
    filtered = filtered.filter(borrow => {
      switch (statusFilter.value) {
        case 'active':
          return !borrow.isReturned && !isOverdue(borrow)
        case 'returned':
          return borrow.isReturned
        case 'overdue':
          return !borrow.isReturned && isOverdue(borrow)
        default:
          return true
      }
    })
  }

  return filtered
})

const activeBorrows = computed(() => 
  borrows.value.filter(borrow => !borrow.isReturned)
)

const overdueBorrows = computed(() => 
  borrows.value.filter(borrow => !borrow.isReturned && isOverdue(borrow))
)

const totalFees = computed(() => 
  fees.value.reduce((sum, fee) => sum + (fee.paid ? 0 : fee.amount), 0)
)

// Methods
const isOverdue = (borrow: Borrow) => {
  return new Date(borrow.dueDate) < new Date()
}

const getDaysUntilDue = (borrow: Borrow) => {
  const dueDate = new Date(borrow.dueDate)
  const now = new Date()
  return Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
}

const getBorrowStatusBadge = (borrow: Borrow) => {
  if (borrow.isReturned) {
    return { variant: 'success' as const, text: 'Returned', icon: CheckCircle }
  }
  
  if (isOverdue(borrow)) {
    return { variant: 'destructive' as const, text: 'Overdue', icon: AlertTriangle }
  }
  
  const daysUntilDue = getDaysUntilDue(borrow)
  if (daysUntilDue <= 3) {
    return { variant: 'secondary' as const, text: 'Due Soon', icon: Clock }
  }
  
  return { variant: 'default' as const, text: 'Active', icon: BookOpen }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

const loadBorrows = async () => {
  try {
    isLoading.value = true
    const response: PagedResponse<Borrow> = await borrowService.getUserBorrows({
      page: currentPage.value,
      size: pageSize.value
    })
    borrows.value = response.content
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
  } catch (error) {
    console.error('Error loading borrows:', error)
    toast.error('Failed to load borrowing history')
  } finally {
    isLoading.value = false
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
    toast.success('Book returned successfully!')
    await loadBorrows()
    await loadFees()
  } catch (error) {
    console.error('Error returning book:', error)
    toast.error('Failed to return book')
  } finally {
    isReturning.value = null
  }
}

const handleRenewBook = async (borrowId: number) => {
  try {
    await borrowService.renewBorrow(borrowId)
    toast.success('Book renewed successfully!')
    await loadBorrows()
  } catch (error) {
    console.error('Error renewing book:', error)
    toast.error('Failed to renew book')
  }
}

const handlePayFee = async (feeId: number) => {
  try {
    await feesService.payFee(feeId)
    toast.success('Fee paid successfully!')
    await loadFees()
  } catch (error) {
    console.error('Error paying fee:', error)
    toast.error('Failed to pay fee')
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
  
  loadBorrows()
  loadFees()
})
</script>

<template>
  <div class="borrowing-management space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold">My Borrowing</h1>
        <p class="text-muted-foreground">Manage your borrowed books and fees</p>
      </div>
      <Button @click="router.push('/books')" variant="outline">
        <BookOpen class="h-4 w-4 mr-2" />
        Browse Books
      </Button>
    </div>

    <!-- Summary Cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">Active Borrows</CardTitle>
          <BookOpen class="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ activeBorrows.length }}</div>
          <p class="text-xs text-muted-foreground">Currently borrowed</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">Overdue Books</CardTitle>
          <AlertTriangle class="h-4 w-4 text-red-500" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-red-600">{{ overdueBorrows.length }}</div>
          <p class="text-xs text-muted-foreground">Need immediate attention</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">Outstanding Fees</CardTitle>
          <CreditCard class="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ totalFees > 0 ? `$${totalFees.toFixed(2)}` : '$0.00' }}</div>
          <p class="text-xs text-muted-foreground">Unpaid late fees</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filters -->
    <Card class="p-6">
      <div class="flex flex-wrap gap-4">
        <!-- Search -->
        <div class="flex flex-col gap-2 flex-1 min-w-[200px]">
          <Label for="search">Search Books</Label>
          <div class="relative">
            <Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              id="search"
              v-model="searchKeyword"
              placeholder="Search by title, author, or ISBN..."
              class="pl-10"
            />
          </div>
        </div>

        <!-- Status Filter -->
        <div class="flex flex-col gap-2">
          <Label for="status">Status</Label>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" class="w-48 justify-between">
                {{ statusOptions.find(opt => opt.value === statusFilter)?.label || 'All Borrows' }}
                <ChevronDown class="ml-2 h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem 
                v-for="option in statusOptions" 
                :key="option.value"
                @click="statusFilter = option.value"
              >
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
          Outstanding Fees
        </CardTitle>
        <CardDescription>Please pay your late fees to continue borrowing</CardDescription>
      </CardHeader>
      <CardContent>
        <div class="space-y-3">
          <div
            v-for="fee in fees.filter(f => !f.paid)"
            :key="fee.id"
            class="flex items-center justify-between p-3 border rounded-lg"
          >
            <div>
              <p class="font-medium">Borrow ID: {{ fee.borrowId }}</p>
              <p class="text-sm text-muted-foreground">
                Fee Date: {{ formatDate(fee.calculationDate) }}
              </p>
            </div>
            <div class="flex items-center gap-3">
              <span class="font-bold text-red-600">${{ fee.amount.toFixed(2) }}</span>
              <Button size="sm" @click="handlePayFee(fee.id)">
                Pay Now
              </Button>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Borrows List -->
    <Card>
      <CardHeader>
        <CardTitle>Borrowing History</CardTitle>
        <CardDescription>
          Showing {{ filteredBorrows.length }} of {{ totalElements }} borrows
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div v-if="isLoading" class="text-center py-8">
          Loading your borrowing history...
        </div>
        
        <div v-else-if="filteredBorrows.length === 0" class="text-center py-8 text-muted-foreground">
          No borrows found matching your criteria
        </div>
        
        <div v-else class="space-y-4">
          <div
            v-for="borrow in filteredBorrows"
            :key="borrow.id"
            class="border rounded-lg p-4 hover:bg-muted/50 transition-colors"
          >
            <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
              <!-- Book Info -->
              <div class="flex items-start gap-4 flex-1">
                <div class="w-16 h-20 bg-muted rounded flex items-center justify-center">
                  <BookOpen class="h-6 w-6 text-muted-foreground" />
                </div>
                
                <div class="flex-1 min-w-0">
                  <h3 
                    class="font-medium hover:text-primary cursor-pointer truncate"
                    @click="goToBookDetail(borrow.bookCopy.book.id)"
                  >
                    {{ borrow.bookCopy.book.title }}
                  </h3>
                  <p class="text-sm text-muted-foreground truncate">
                    {{ borrow.bookCopy.book.authors.map(a => a.name).join(', ') }}
                  </p>
                  <p class="text-xs text-muted-foreground">
                    ISBN: {{ borrow.bookCopy.book.isbn }}
                  </p>
                </div>
              </div>

              <!-- Dates and Status -->
              <div class="flex flex-col lg:items-end gap-2">
                <Badge 
                  :variant="getBorrowStatusBadge(borrow).variant"
                  class="w-fit"
                >
                  <component :is="getBorrowStatusBadge(borrow).icon" class="h-3 w-3 mr-1" />
                  {{ getBorrowStatusBadge(borrow).text }}
                </Badge>
                
                <div class="text-sm text-muted-foreground">
                  <div class="flex items-center gap-1">
                    <Calendar class="h-3 w-3" />
                    Borrowed: {{ formatDate(borrow.borrowDate) }}
                  </div>
                  <div class="flex items-center gap-1">
                    <Clock class="h-3 w-3" />
                    Due: {{ formatDate(borrow.dueDate) }}
                    <span v-if="!borrow.isReturned" class="ml-1">
                      ({{ getDaysUntilDue(borrow) > 0 ? `${getDaysUntilDue(borrow)} days left` : `${Math.abs(getDaysUntilDue(borrow))} days overdue` }})
                    </span>
                  </div>
                  <div v-if="borrow.returnDate" class="flex items-center gap-1">
                    <CheckCircle class="h-3 w-3" />
                    Returned: {{ formatDate(borrow.returnDate) }}
                  </div>
                </div>
              </div>

              <!-- Actions -->
              <div v-if="!borrow.isReturned" class="flex gap-2">
                <Button 
                  size="sm" 
                  variant="outline"
                  @click="handleRenewBook(borrow.id)"
                >
                  <RefreshCw class="h-3 w-3 mr-1" />
                  Renew
                </Button>
                <Button 
                  size="sm"
                  @click="handleReturnBook(borrow.id)"
                  :disabled="isReturning === borrow.id"
                >
                  <CheckCircle class="h-3 w-3 mr-1" />
                  {{ isReturning === borrow.id ? 'Returning...' : 'Return' }}
                </Button>
              </div>
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="flex justify-center items-center gap-2 mt-6">
          <Button 
            variant="outline" 
            size="sm"
            :disabled="currentPage === 0"
            @click="handlePageChange(currentPage - 1)"
          >
            <ArrowLeft class="h-4 w-4" />
            Previous
          </Button>
          
          <span class="text-sm text-muted-foreground px-4">
            Page {{ currentPage + 1 }} of {{ totalPages }}
          </span>
          
          <Button 
            variant="outline"
            size="sm"
            :disabled="currentPage === totalPages - 1"
            @click="handlePageChange(currentPage + 1)"
          >
            Next
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