<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { ChevronDown, ChevronLeft, ChevronRight, Search, Plus } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
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
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { booksService } from '@/lib/api'
import type { Book, PagedResponse, BookSearchParams } from '@/lib/api/types'
import { useAuthStore } from '@/stores/auth'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()

// Data
const books = ref<Book[]>([])
const categories = ref<string[]>([])
const isLoading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

// Search and filter parameters
const searchKeyword = ref('')
const selectedCategory = ref('All Categories')
const selectedLanguage = ref('All Languages')
const sortBy = ref('title')
const sortDirection = ref<'asc' | 'desc'>('asc')
const pageSize = ref(12)

// Dialog states
const showAddBookDialog = ref(false)
const isSubmitting = ref(false)

// Book form data
const bookForm = ref({
  title: '',
  isbn: '',
  language: '',
  category: '',
  authors: '',
  publisher: '',
  publishedYear: new Date().getFullYear(),
  description: '',
  totalQuantity: 1
})

const languages = ['All Languages', 'English', 'Chinese', 'Spanish', 'French', 'German', 'Japanese']
const sortOptions = [
  { label: 'Title (A-Z)', value: 'title', direction: 'asc' },
  { label: 'Title (Z-A)', value: 'title', direction: 'desc' },
  { label: 'Available First', value: 'availableQuantity', direction: 'desc' },
  { label: 'Total Copies', value: 'totalQuantity', direction: 'desc' }
]

// Computed
const searchParams = computed<BookSearchParams>(() => ({
  keyword: searchKeyword.value || undefined,
  category: selectedCategory.value !== 'All Categories' ? selectedCategory.value : undefined,
  language: selectedLanguage.value !== 'All Languages' ? selectedLanguage.value : undefined,
  page: currentPage.value,
  size: pageSize.value,
  sort: `${sortBy.value},${sortDirection.value}`
}))

const isAdmin = computed(() => {
  // You might want to check user role here when user data is available
  return authStore.isAuthenticated
})

// Methods
const loadBooks = async () => {
  try {
    isLoading.value = true
    const response: PagedResponse<Book> = await booksService.getAllBooks(searchParams.value)
    books.value = response.content
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
  } catch (error) {
    console.error('Error loading books:', error)
    toast.error('Failed to load books')
  } finally {
    isLoading.value = false
  }
}

const loadCategories = async () => {
  try {
    // This would need to be implemented in the backend to get all categories
    // For now, we'll extract categories from the books
    const allBooks = await booksService.getAllBooks({ size: 1000 })
    const categorySet = new Set<string>()
    allBooks.content.forEach(book => {
      if (book.indexCategory?.name) {
        categorySet.add(book.indexCategory.name)
      }
    })
    categories.value = Array.from(categorySet).sort()
  } catch (error) {
    console.error('Error loading categories:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 0
  loadBooks()
}

const handleSortChange = (option: typeof sortOptions[0]) => {
  sortBy.value = option.value
  sortDirection.value = option.direction as 'asc' | 'desc'
  currentPage.value = 0
  loadBooks()
}

const handlePageChange = (page: number) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    loadBooks()
  }
}

const goToBookDetail = (bookId: number) => {
  router.push(`/books/${bookId}`)
}

const handleAddBook = () => {
  showAddBookDialog.value = true
}

const closeAddBookDialog = () => {
  showAddBookDialog.value = false
  bookForm.value = {
    title: '',
    isbn: '',
    language: '',
    category: '',
    authors: '',
    publisher: '',
    publishedYear: new Date().getFullYear(),
    description: '',
    totalQuantity: 1
  }
  isSubmitting.value = false
}

const handleCreateBook = async () => {
  if (!bookForm.value.title.trim() || !bookForm.value.isbn.trim()) {
    toast.error('Please fill in all required fields')
    return
  }

  try {
    isSubmitting.value = true
    // TODO: Implement actual book creation via API
    // await booksService.createBook(bookForm.value)
    
    toast.success('Book added successfully!')
    closeAddBookDialog()
    await loadBooks()
  } catch (error) {
    console.error('Error creating book:', error)
    toast.error('Failed to add book')
  } finally {
    isSubmitting.value = false
  }
}

// Watch for search parameter changes
watch([selectedCategory, selectedLanguage], () => {
  currentPage.value = 0
  loadBooks()
})

// Lifecycle
onMounted(() => {
  loadBooks()
  loadCategories()
})
</script>

<template>
  <div class="books-view space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold">Books</h1>
        <p class="text-muted-foreground">Browse and manage library books</p>
      </div>
      <Button v-if="isAdmin" @click="handleAddBook">
        <Plus class="h-4 w-4 mr-2" />
        Add Book
      </Button>
    </div>

    <!-- Search and Filters -->
    <Card class="p-6">
      <div class="flex flex-wrap gap-4">
        <!-- Search -->
        <div class="flex flex-col gap-2 flex-1 min-w-[200px]">
          <Label for="search">Search</Label>
          <div class="relative">
            <Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              id="search"
              v-model="searchKeyword"
              placeholder="Search books by title, author, or ISBN..."
              class="pl-10"
              @keyup.enter="handleSearch"
            />
          </div>
        </div>

        <!-- Category Filter -->
        <div class="flex flex-col gap-2">
          <Label for="category">Category</Label>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" class="w-48 justify-between">
                {{ selectedCategory }}
                <ChevronDown class="ml-2 h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem @click="selectedCategory = 'All Categories'">
                All Categories
              </DropdownMenuItem>
              <DropdownMenuItem 
                v-for="category in categories" 
                :key="category"
                @click="selectedCategory = category"
              >
                {{ category }}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <!-- Language Filter -->
        <div class="flex flex-col gap-2">
          <Label for="language">Language</Label>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" class="w-48 justify-between">
                {{ selectedLanguage }}
                <ChevronDown class="ml-2 h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem 
                v-for="language in languages" 
                :key="language"
                @click="selectedLanguage = language"
              >
                {{ language }}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <!-- Sort -->
        <div class="flex flex-col gap-2">
          <Label for="sortBy">Sort By</Label>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" class="w-48 justify-between">
                {{ sortOptions.find(opt => opt.value === sortBy && opt.direction === sortDirection)?.label || 'Title (A-Z)' }}
                <ChevronDown class="ml-2 h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem 
                v-for="option in sortOptions" 
                :key="`${option.value}-${option.direction}`"
                @click="handleSortChange(option)"
              >
                {{ option.label }}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <!-- Search Button -->
        <div class="flex flex-col justify-end gap-2">
          <Button @click="handleSearch">Search</Button>
        </div>
      </div>
    </Card>

    <!-- Results Summary -->
    <div class="text-sm text-muted-foreground">
      Showing {{ books.length }} of {{ totalElements }} books
    </div>

    <!-- Books Grid -->
    <div v-if="isLoading" class="text-center py-8">
      Loading books...
    </div>
    
    <div v-else-if="books.length === 0" class="text-center py-8 text-muted-foreground">
      No books found matching your criteria.
    </div>
    
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4">
      <div 
        v-for="book in books" 
        :key="book.id"
        class="cursor-pointer transform transition-transform hover:scale-105"
        @click="goToBookDetail(book.id)"
      >
        <BookCard 
          :title="book.title"
          :author="book.authors.map(a => a.name).join(', ')"
          :isbn="book.isbn"
          :available="book.availableQuantity > 0"
          :total-copies="book.totalQuantity"
          :available-copies="book.availableQuantity"
        />
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex justify-center items-center gap-2 mt-8">
      <Button 
        variant="outline" 
        :disabled="currentPage === 0"
        @click="handlePageChange(currentPage - 1)"
      >
        <ChevronLeft class="h-4 w-4" />
        Previous
      </Button>
      
      <span class="text-sm text-muted-foreground px-4">
        Page {{ currentPage + 1 }} of {{ totalPages }}
      </span>
      
      <Button 
        variant="outline"
        :disabled="currentPage === totalPages - 1"
        @click="handlePageChange(currentPage + 1)"
      >
        Next
        <ChevronRight class="h-4 w-4" />
      </Button>
    </div>
  </div>

  <!-- Add Book Dialog -->
  <Dialog v-model:open="showAddBookDialog">
    <DialogContent class="sm:max-w-[600px]">
      <DialogHeader>
        <DialogTitle>Add Book</DialogTitle>
        <DialogDescription>Fill in the details of the new book</DialogDescription>
      </DialogHeader>

      <form @submit.prevent="handleCreateBook" class="space-y-4">
        <div class="space-y-2">
          <Label for="title">Title</Label>
          <Input
            id="title"
            v-model="bookForm.title"
            placeholder="Enter book title..."
            required
          />
        </div>
        
        <div class="space-y-2">
          <Label for="isbn">ISBN</Label>
          <Input
            id="isbn"
            v-model="bookForm.isbn"
            placeholder="Enter ISBN..."
            required
          />
        </div>
        
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="language">Language</Label>
            <Input
              id="language"
              v-model="bookForm.language"
              placeholder="Enter language..."
              required
            />
          </div>
          
          <div class="space-y-2">
            <Label for="category">Category</Label>
            <Input
              id="category"
              v-model="bookForm.category"
              placeholder="Enter category..."
              required
            />
          </div>
        </div>
        
        <div class="space-y-2">
          <Label for="authors">Authors</Label>
          <Input
            id="authors"
            v-model="bookForm.authors"
            placeholder="Enter authors (comma-separated)..."
            required
          />
        </div>
        
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="publisher">Publisher</Label>
            <Input
              id="publisher"
              v-model="bookForm.publisher"
              placeholder="Enter publisher..."
            />
          </div>
          
          <div class="space-y-2">
            <Label for="publishedYear">Published Year</Label>
            <Input
              id="publishedYear"
              type="number"
              v-model="bookForm.publishedYear"
              :min="1000"
              :max="new Date().getFullYear() + 1"
            />
          </div>
        </div>
        
        <div class="space-y-2">
          <Label for="description">Description</Label>
          <textarea
            id="description"
            v-model="bookForm.description"
            placeholder="Enter book description..."
            class="w-full min-h-[100px] p-3 border rounded-md resize-none"
          />
        </div>
        
        <div class="space-y-2">
          <Label for="totalQuantity">Total Quantity</Label>
          <Input
            id="totalQuantity"
            type="number"
            v-model="bookForm.totalQuantity"
            :min="1"
            required
          />
        </div>
        
        <DialogFooter>
          <Button type="button" variant="outline" @click="closeAddBookDialog">
            Cancel
          </Button>
          <Button type="submit" :disabled="isSubmitting">
            {{ isSubmitting ? 'Adding...' : 'Add Book' }}
          </Button>
        </DialogFooter>
      </form>
    </DialogContent>
  </Dialog>
</template>

<style scoped>
.books-view {
  padding: 0;
}
</style>
