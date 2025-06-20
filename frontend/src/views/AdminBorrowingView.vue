<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  AlertTriangle,
  Book,
  BookOpen,
  Calendar,
  CheckCircle,
  ChevronDown,
  Clock,
  Plus,
  RefreshCw,
  Search,
  User,
  Users,
} from 'lucide-vue-next'
import { booksService, borrowService, usersService } from '@/lib/api'
import type {
  Book as BookType,
  BookCopy,
  Borrow,
  BorrowResponseDto,
  UserPublic,
} from '@/lib/api/types'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()

// Data
const borrows = ref<Borrow[]>([])
const users = ref<UserPublic[]>([])
const books = ref<BookType[]>([])
const bookCopies = ref<BookCopy[]>([])
const isLoading = ref(false)
const isSubmitting = ref(false)

// Dialog state
const isCreateDialogOpen = ref(false)

// Form data
const selectedUser = ref<UserPublic | null>(null)
const selectedBook = ref<BookType | null>(null)
const selectedCopy = ref<BookCopy | null>(null)
const userSearchKeyword = ref('')
const bookSearchKeyword = ref('')

// Filters
const searchKeyword = ref('')
const statusFilter = ref('all')

// Filter options
const statusOptions = [
  { value: 'all', label: 'All Borrows' },
  { value: 'active', label: 'Active' },
  { value: 'returned', label: 'Returned' },
  { value: 'overdue', label: 'Overdue' },
]

// Computed
const filteredUsers = computed(() => {
  if (!userSearchKeyword.value) return users.value.slice(0, 10)

  const keyword = userSearchKeyword.value.toLowerCase()
  return users.value.filter((user) => user.username.toLowerCase().includes(keyword)).slice(0, 10)
})

const filteredBooks = computed(() => {
  if (!bookSearchKeyword.value) return books.value.slice(0, 10)

  const keyword = bookSearchKeyword.value.toLowerCase()
  return books.value
    .filter(
      (book) =>
        book.title.toLowerCase().includes(keyword) ||
        book.isbn.toLowerCase().includes(keyword) ||
        book.authors.some((author) => author.name.toLowerCase().includes(keyword)),
    )
    .slice(0, 10)
})

const availableCopies = computed(() => {
  if (!selectedBook.value) return []
  return bookCopies.value.filter(
    (copy) => copy.book.id === selectedBook.value?.id && copy.status === 'AVAILABLE',
  )
})

const filteredBorrows = computed(() => {
  let filtered = borrows.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(
      (borrow) =>
        borrow.user.username.toLowerCase().includes(keyword) ||
        borrow.bookCopy.book.title.toLowerCase().includes(keyword) ||
        borrow.bookCopy.book.isbn.toLowerCase().includes(keyword),
    )
  }

  if (statusFilter.value !== 'all') {
    filtered = filtered.filter((borrow) => {
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

const activeBorrows = computed(() => borrows.value.filter((borrow) => !borrow.isReturned))

const overdueBorrows = computed(() =>
  borrows.value.filter((borrow) => !borrow.isReturned && isOverdue(borrow)),
)

// Form validation
const isFormValid = computed(() => {
  return selectedUser.value && selectedCopy.value
})

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
    const allBorrows = await borrowService.adminGetAllBorrows()
    borrows.value = allBorrows
  } catch (error) {
    console.error('Error loading borrows:', error)
    toast.error('Failed to load borrowing records')
  } finally {
    isLoading.value = false
  }
}

const loadUsers = async () => {
  try {
    const response = await usersService.getAllUsers({ page: 0, size: 100 })
    users.value = response.content
  } catch (error) {
    console.error('Error loading users:', error)
    toast.error('Failed to load users')
  }
}

const loadBooks = async () => {
  try {
    const response = await booksService.getAll({ page: 0, size: 100 })
    books.value = response.content
  } catch (error) {
    console.error('Error loading books:', error)
    toast.error('Failed to load books')
  }
}

const loadBookCopies = async (bookId: number) => {
  try {
    const copies = await booksService.getCopies(bookId)
    bookCopies.value = copies
  } catch (error) {
    console.error('Error loading book copies:', error)
    toast.error('Failed to load book copies')
  }
}

const selectUser = (user: UserPublic) => {
  selectedUser.value = user
  userSearchKeyword.value = user.username
}

const selectBook = async (book: BookType) => {
  selectedBook.value = book
  bookSearchKeyword.value = book.title
  selectedCopy.value = null
  await loadBookCopies(book.id)
}

const selectCopy = (copy: BookCopy) => {
  selectedCopy.value = copy
}

const resetForm = () => {
  selectedUser.value = null
  selectedBook.value = null
  selectedCopy.value = null
  userSearchKeyword.value = ''
  bookSearchKeyword.value = ''
  bookCopies.value = []
}

const handleCreateBorrow = async () => {
  if (!isFormValid.value) {
    toast.error('Please select both a user and a book copy')
    return
  }

  try {
    isSubmitting.value = true
    const response: BorrowResponseDto = await borrowService.adminBorrowBook({
      userId: selectedUser.value!.id,
      copyId: selectedCopy.value!.id,
    })

    toast.success(response.message || 'Book borrowed successfully!')
    isCreateDialogOpen.value = false
    resetForm()
    await loadBorrows()
  } catch (error: any) {
    console.error('Error creating borrow:', error)
    toast.error(error.response?.data?.error || 'Failed to create borrowing record')
  } finally {
    isSubmitting.value = false
  }
}

const goToBookDetail = (bookId: number) => {
  router.push(`/books/${bookId}`)
}

const goToUserProfile = (userId: number) => {
  router.push(`/users/${userId}`)
}

// Watch for search changes
watch(userSearchKeyword, (newValue) => {
  if (!newValue) {
    selectedUser.value = null
  }
})

watch(bookSearchKeyword, (newValue) => {
  if (!newValue) {
    selectedBook.value = null
    selectedCopy.value = null
    bookCopies.value = []
  }
})

// Lifecycle
onMounted(() => {
  if (!authStore.isAuthenticated || authStore.user?.role !== 'ADMIN') {
    router.push('/login')
    return
  }

  loadBorrows()
  loadUsers()
  loadBooks()
})
</script>

<template>
  <div class="admin-borrowing space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold">Admin Borrowing Management</h1>
        <p class="text-muted-foreground">Register and manage book borrowing for users</p>
      </div>
      <div class="flex gap-2">
        <Dialog v-model:open="isCreateDialogOpen">
          <DialogTrigger asChild>
            <Button>
              <Plus class="h-4 w-4 mr-2" />
              Register Borrow
            </Button>
          </DialogTrigger>
          <DialogContent class="max-w-2xl">
            <DialogHeader>
              <DialogTitle>Register Book Borrowing</DialogTitle>
              <DialogDescription>
                Select a user and book copy to register a new borrowing record
              </DialogDescription>
            </DialogHeader>

            <div class="space-y-6">
              <!-- User Selection -->
              <div class="space-y-2">
                <Label for="user-search">Select User</Label>
                <div class="relative">
                  <Search
                    class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
                  />
                  <Input
                    id="user-search"
                    v-model="userSearchKeyword"
                    placeholder="Search users by username..."
                    class="pl-10"
                  />
                </div>

                <div
                  v-if="userSearchKeyword && !selectedUser"
                  class="border rounded-lg max-h-40 overflow-y-auto"
                >
                  <div
                    v-for="user in filteredUsers"
                    :key="user.id"
                    class="p-3 hover:bg-muted cursor-pointer border-b last:border-b-0"
                    @click="selectUser(user)"
                  >
                    <div class="flex items-center gap-2">
                      <User class="h-4 w-4" />
                      <span class="font-medium">{{ user.username }}</span>
                      <span class="text-sm text-muted-foreground">ID: {{ user.id }}</span>
                    </div>
                  </div>
                </div>

                <div v-if="selectedUser" class="p-3 border rounded-lg bg-muted/50">
                  <div class="flex items-center justify-between">
                    <div class="flex items-center gap-2">
                      <User class="h-4 w-4" />
                      <span class="font-medium">{{ selectedUser.username }}</span>
                      <Badge variant="outline">ID: {{ selectedUser.id }}</Badge>
                    </div>
                    <Button
                      size="sm"
                      variant="ghost"
                      @click="
                        selectedUser = null;
                        userSearchKeyword = '';
                      "
                    >
                      ×
                    </Button>
                  </div>
                </div>
              </div>

              <!-- Book Selection -->
              <div class="space-y-2">
                <Label for="book-search">Select Book</Label>
                <div class="relative">
                  <Search
                    class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
                  />
                  <Input
                    id="book-search"
                    v-model="bookSearchKeyword"
                    placeholder="Search books by title, author, or ISBN..."
                    class="pl-10"
                  />
                </div>

                <div
                  v-if="bookSearchKeyword && !selectedBook"
                  class="border rounded-lg max-h-40 overflow-y-auto"
                >
                  <div
                    v-for="book in filteredBooks"
                    :key="book.id"
                    class="p-3 hover:bg-muted cursor-pointer border-b last:border-b-0"
                    @click="selectBook(book)"
                  >
                    <div class="space-y-1">
                      <div class="flex items-center gap-2">
                        <Book class="h-4 w-4" />
                        <span class="font-medium">{{ book.title }}</span>
                      </div>
                      <div class="text-sm text-muted-foreground">
                        {{ book.authors.map((a) => a.name).join(', ') }} | ISBN: {{ book.isbn }}
                      </div>
                      <div class="text-xs text-muted-foreground">
                        Available: {{ book.availableQuantity }} / {{ book.totalQuantity }}
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="selectedBook" class="p-3 border rounded-lg bg-muted/50">
                  <div class="flex items-center justify-between">
                    <div class="space-y-1">
                      <div class="flex items-center gap-2">
                        <Book class="h-4 w-4" />
                        <span class="font-medium">{{ selectedBook.title }}</span>
                      </div>
                      <div class="text-sm text-muted-foreground">
                        {{ selectedBook.authors.map((a) => a.name).join(', ') }}
                      </div>
                    </div>
                    <Button
                      size="sm"
                      variant="ghost"
                      @click="
                        selectedBook = null;
                        bookSearchKeyword = '';
                      "
                    >
                      ×
                    </Button>
                  </div>
                </div>
              </div>

              <!-- Copy Selection -->
              <div v-if="selectedBook && availableCopies.length > 0" class="space-y-2">
                <Label>Select Available Copy</Label>
                <div class="grid grid-cols-2 gap-2">
                  <button
                    v-for="copy in availableCopies"
                    :key="copy.id"
                    class="p-3 border rounded-lg text-left hover:bg-muted transition-colors"
                    :class="{ 'border-primary bg-primary/10': selectedCopy?.id === copy.id }"
                    @click="selectCopy(copy)"
                  >
                    <div class="text-sm font-medium">Copy #{{ copy.id }}</div>
                    <div class="text-xs text-muted-foreground">Available</div>
                  </button>
                </div>
              </div>

              <div
                v-else-if="selectedBook && availableCopies.length === 0"
                class="text-center py-4 text-muted-foreground"
              >
                No available copies for this book
              </div>
            </div>

            <DialogFooter>
              <Button variant="outline" @click="isCreateDialogOpen = false"> Cancel </Button>
              <Button @click="handleCreateBorrow" :disabled="!isFormValid || isSubmitting">
                {{ isSubmitting ? 'Creating...' : 'Register Borrow' }}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">Total Borrows</CardTitle>
          <BookOpen class="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">{{ borrows.length }}</div>
          <p class="text-xs text-muted-foreground">All borrowing records</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">Active Borrows</CardTitle>
          <Users class="h-4 w-4 text-muted-foreground" />
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
          <p class="text-xs text-muted-foreground">Need attention</p>
        </CardContent>
      </Card>
    </div>

    <!-- Filters -->
    <Card class="p-6">
      <div class="flex flex-wrap gap-4">
        <!-- Search -->
        <div class="flex flex-col gap-2 flex-1 min-w-[200px]">
          <Label for="search">Search Records</Label>
          <div class="relative">
            <Search
              class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
            />
            <Input
              id="search"
              v-model="searchKeyword"
              placeholder="Search by user, book title, or ISBN..."
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
                {{
                  statusOptions.find((opt) => opt.value === statusFilter)?.label || 'All Borrows'
                }}
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

    <!-- Borrows List -->
    <Card>
      <CardHeader>
        <CardTitle>All Borrowing Records</CardTitle>
        <CardDescription>
          Showing {{ filteredBorrows.length }} of {{ borrows.length }} records
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div v-if="isLoading" class="text-center py-8">Loading borrowing records...</div>

        <div
          v-else-if="filteredBorrows.length === 0"
          class="text-center py-8 text-muted-foreground"
        >
          No borrowing records found
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="borrow in filteredBorrows"
            :key="borrow.id"
            class="border rounded-lg p-4 hover:bg-muted/50 transition-colors"
          >
            <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
              <!-- User and Book Info -->
              <div class="flex items-start gap-4 flex-1">
                <div class="w-16 h-20 bg-muted rounded flex items-center justify-center">
                  <BookOpen class="h-6 w-6 text-muted-foreground" />
                </div>

                <div class="flex-1 min-w-0 space-y-2">
                  <div class="flex items-center gap-2">
                    <User class="h-4 w-4" />
                    <span
                      class="font-medium hover:text-primary cursor-pointer"
                      @click="goToUserProfile(borrow.user.id)"
                    >
                      {{ borrow.user.username }}
                    </span>
                  </div>

                  <h3
                    class="font-medium hover:text-primary cursor-pointer truncate"
                    @click="goToBookDetail(borrow.bookCopy.book.id)"
                  >
                    {{ borrow.bookCopy.book.title }}
                  </h3>
                  <p class="text-sm text-muted-foreground truncate">
                    {{ borrow.bookCopy.book.authors.map((a) => a.name).join(', ') }}
                  </p>
                  <p class="text-xs text-muted-foreground">
                    ISBN: {{ borrow.bookCopy.book.isbn }} | Copy #{{ borrow.bookCopy.id }}
                  </p>
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
                    Borrowed: {{ formatDate(borrow.borrowDate) }}
                  </div>
                  <div class="flex items-center gap-1">
                    <Clock class="h-3 w-3" />
                    Due: {{ formatDate(borrow.dueDate) }}
                    <span v-if="!borrow.isReturned" class="ml-1">
                      ({{
                        getDaysUntilDue(borrow) > 0
                          ? `${getDaysUntilDue(borrow)} days left`
                          : `${Math.abs(getDaysUntilDue(borrow))} days overdue`
                      }})
                    </span>
                  </div>
                  <div v-if="borrow.returnDate" class="flex items-center gap-1">
                    <CheckCircle class="h-3 w-3" />
                    Returned: {{ formatDate(borrow.returnDate) }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<style scoped>
.admin-borrowing {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
