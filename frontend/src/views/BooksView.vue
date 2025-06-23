<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { ChevronDown, ChevronLeft, ChevronRight, Plus, Search } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
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
import { booksService, categoriesService } from '@/lib/api'
import type {
  Book,
  BookSearchParams,
  BookSummaryDto,
  PagedResponse,
  IndexCategory,
  BookDto,
} from '@/lib/api/types'
import { useAuthStore } from '@/stores/auth'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

// Data
const books = ref<BookSummaryDto[]>([])
const categories = ref<string[]>([])
const isLoading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

// Search and filter parameters
const searchKeyword = ref('')
const selectedCategory = ref('')
const selectedLanguage = ref('')
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
  coverURL: '',
  totalQuantity: 1,
})

// Computed values that depend on translations
const languages = computed(() => [
  t('books.filters.language.all'),
  'English',
  'Chinese',
  'Spanish',
  'French',
  'German',
  'Japanese'
])

const sortOptions = computed(() => [
  { label: t('books.filters.sort.titleAsc'), value: 'title', direction: 'asc' },
  { label: t('books.filters.sort.titleDesc'), value: 'title', direction: 'desc' },
  { label: t('books.filters.sort.availableFirst'), value: 'availableQuantity', direction: 'desc' },
  { label: t('books.filters.sort.totalCopies'), value: 'totalQuantity', direction: 'desc' },
])

// Computed
const searchParams = computed<BookSearchParams>(() => ({
  keyword: searchKeyword.value || undefined,
  category: selectedCategory.value !== t('books.filters.category.all') ? selectedCategory.value : undefined,
  language: selectedLanguage.value !== t('books.filters.language.all') ? selectedLanguage.value : undefined,
  page: currentPage.value,
  size: pageSize.value,
  sort: `${sortBy.value},${sortDirection.value}`,
}))

const isAdmin = computed(() => {
  return authStore.isAdmin()
})

// Methods
const loadBooks = async () => {
  try {
    isLoading.value = true
    let response: PagedResponse<BookSummaryDto> | PagedResponse<BookDto>

    if (searchKeyword.value.trim()) {
      // Perform search when keyword is provided
      const searchResp: PagedResponse<BookDto> = await booksService.search(searchParams.value)
      // Map detailed BookDto list to BookSummaryDto-like structure expected by the UI
      books.value = (searchResp.content as BookDto[]).map((book) => ({
        id: book.id,
        title: book.title,
        authors: book.authors?.map((author) => author.name) || [],
        publishers: book.publishers?.map((publisher) => publisher.name) || [],
        coverURL: (book as any).coverURL, // coverURL is optional
        availableQuantity: book.availableQuantity,
        totalQuantity: book.totalQuantity,
      }))
      totalPages.value = searchResp.totalPages
      totalElements.value = searchResp.totalElements
    } else {
      // Default to all books summary when no keyword is specified
      const summaryResp: PagedResponse<BookSummaryDto> = await booksService.getAllSummary(
        searchParams.value,
      )
      books.value = summaryResp.content as BookSummaryDto[]
      totalPages.value = summaryResp.totalPages
      totalElements.value = summaryResp.totalElements
    }
  } catch (error) {
    console.error('Error loading books:', error)
    toast.error(t('books.errors.loadFailed'))
  } finally {
    isLoading.value = false
  }
}

const loadCategories = async () => {
  try {
    const allCategories = await categoriesService.getAll()
    categories.value = allCategories.map((category: IndexCategory) => category.name).sort()
  } catch (error) {
    console.error('Error loading categories:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 0
  loadBooks()
}

const handleSortChange = (option: { label: string; value: string; direction: string }) => {
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
    coverURL: '',
    totalQuantity: 1,
  }
  isSubmitting.value = false
}

const handleCreateBook = async () => {
  if (!bookForm.value.title.trim() || !bookForm.value.isbn.trim()) {
    toast.error(t('books.form.validation.requiredFields'))
    return
  }

  try {
    isSubmitting.value = true

    // Create the book data object matching the BookCreateDto
    const bookData = {
      isbn: bookForm.value.isbn,
      title: bookForm.value.title,
      language: bookForm.value.language,
      description: bookForm.value.description,
      coverURL: bookForm.value.coverURL || undefined,
      authorNames: bookForm.value.authors
        .split(',')
        .map((author) => author.trim())
        .filter(Boolean),
      publisherNames: bookForm.value.publisher ? [bookForm.value.publisher.trim()] : [],
      categoryName: bookForm.value.category,
      totalQuantity: bookForm.value.totalQuantity,
    }

    await booksService.create(bookData)

    toast.success(t('books.form.messages.success'))
    closeAddBookDialog()
    await loadBooks()
  } catch (error) {
    console.error('Error creating book:', error)
    toast.error(t('books.form.messages.error'))
  } finally {
    isSubmitting.value = false
  }
}

const handleFavoriteChanged = (bookId: number, isFavorite: boolean) => {
  // Provide user feedback without needing to reload the entire books list
  // The FavoriteButton component already shows toast notifications
  console.log(`Book ${bookId} favorite status changed to: ${isFavorite}`)
}

// Watch for search parameter changes
watch([selectedCategory, selectedLanguage], () => {
  currentPage.value = 0
  loadBooks()
})

// Lifecycle
onMounted(() => {
  // Initialize translated values
  selectedCategory.value = t('books.filters.category.all')
  selectedLanguage.value = t('books.filters.language.all')

  loadBooks()
  loadCategories()
})
</script>

<template>
  <div class="books-view space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold">{{ t('books.title') }}</h1>
        <p class="text-muted-foreground">{{ t('books.description') }}</p>
      </div>
      <Button v-if="isAdmin" @click="handleAddBook">
        <Plus class="h-4 w-4 mr-2" />
        {{ t('books.addBook') }}
      </Button>
    </div>

    <!-- Search and Filters -->
    <Card class="p-6">
      <div class="flex flex-wrap gap-4">
        <!-- Search -->
        <div class="flex flex-col gap-2 flex-1 min-w-[200px]">
          <Label for="search">{{ t('books.search.label') }}</Label>
          <div class="relative">
            <Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input id="search" v-model="searchKeyword" :placeholder="t('books.search.placeholder')" class="pl-10"
              @keyup.enter="handleSearch" />
          </div>
        </div>

        <!-- Category Filter -->
        <div class="flex flex-col gap-2">
          <Label for="category">{{ t('books.filters.category.label') }}</Label>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" class="w-48 justify-between">
                {{ selectedCategory === t('books.filters.category.all') ? t('books.filters.category.all') :
                  selectedCategory }}
                <ChevronDown class="ml-2 h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem @click="selectedCategory = t('books.filters.category.all')">
                {{ t('books.filters.category.all') }}
              </DropdownMenuItem>
              <DropdownMenuItem v-for="category in categories" :key="category" @click="selectedCategory = category">
                {{ category }}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <!-- Language Filter -->
        <div class="flex flex-col gap-2">
          <Label for="language">{{ t('books.filters.language.label') }}</Label>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" class="w-48 justify-between">
                {{ selectedLanguage === t('books.filters.language.all') ? t('books.filters.language.all') :
                  selectedLanguage }}
                <ChevronDown class="ml-2 h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem v-for="language in languages" :key="language" @click="selectedLanguage = language">
                {{ language }}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <!-- Sort -->
        <div class="flex flex-col gap-2">
          <Label for="sortBy">{{ t('books.filters.sort.label') }}</Label>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" class="w-48 justify-between">
                {{
                  sortOptions.find(opt => opt.value === sortBy && opt.direction === sortDirection)
                    ?.label || t('books.filters.sort.titleAsc')
                }}
                <ChevronDown class="ml-2 h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuItem v-for="option in sortOptions" :key="`${option.value}-${option.direction}`"
                @click="handleSortChange(option)">
                {{ option.label }}
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <!-- Search Button -->
        <div class="flex flex-col justify-end gap-2">
          <Button @click="handleSearch">{{ t('books.search.button') }}</Button>
        </div>
      </div>
    </Card>

    <!-- Results Summary -->
    <div class="text-sm text-muted-foreground">
      {{ t('books.results.showing', { count: books.length, total: totalElements }) }}
    </div>

    <!-- Books Grid -->
    <div v-if="isLoading" class="text-center py-8">{{ t('books.results.loading') }}</div>

    <div v-else-if="books.length === 0" class="text-center py-8 text-muted-foreground">
      {{ t('books.results.noResults') }}
    </div>

    <div v-else
      class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-6">
      <div v-for="book in books" :key="book.id" class="cursor-pointer transform transition-transform hover:scale-105"
        @click="goToBookDetail(book.id)">
        <BookCard :title="book.title" :author="book.authors.join(', ')" :cover-image-url="book.coverURL"
          :available-copies="book.availableQuantity" :total-copies="book.totalQuantity" :book-id="book.id"
          :show-favorite-button="authStore.isAuthenticated" @favorite-changed="handleFavoriteChanged" />
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex justify-center items-center gap-2 mt-8">
      <Button variant="outline" :disabled="currentPage === 0" @click="handlePageChange(currentPage - 1)">
        <ChevronLeft class="h-4 w-4" />
        {{ t('books.pagination.previous') }}
      </Button>

      <span class="text-sm text-muted-foreground px-4">
        {{ t('books.pagination.page', { current: currentPage + 1, total: totalPages }) }}
      </span>

      <Button variant="outline" :disabled="currentPage === totalPages - 1" @click="handlePageChange(currentPage + 1)">
        {{ t('books.pagination.next') }}
        <ChevronRight class="h-4 w-4" />
      </Button>
    </div>
  </div>

  <!-- Add Book Dialog -->
  <Dialog v-model:open="showAddBookDialog">
    <DialogContent class="sm:max-w-[600px]">
      <DialogHeader>
        <DialogTitle>{{ t('books.form.title') }}</DialogTitle>
        <DialogDescription>{{ t('books.form.description') }}</DialogDescription>
      </DialogHeader>

      <form @submit.prevent="handleCreateBook" class="space-y-4">
        <div class="space-y-2">
          <Label for="title">{{ t('books.form.fields.title.label') }}</Label>
          <Input id="title" v-model="bookForm.title" :placeholder="t('books.form.fields.title.placeholder')" required />
        </div>

        <div class="space-y-2">
          <Label for="isbn">{{ t('books.form.fields.isbn.label') }}</Label>
          <Input id="isbn" v-model="bookForm.isbn" :placeholder="t('books.form.fields.isbn.placeholder')" required />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="language">{{ t('books.form.fields.language.label') }}</Label>
            <Input id="language" v-model="bookForm.language" :placeholder="t('books.form.fields.language.placeholder')"
              required />
          </div>

          <div class="space-y-2">
            <Label for="category">{{ t('books.form.fields.category.label') }}</Label>
            <Input id="category" v-model="bookForm.category" :placeholder="t('books.form.fields.category.placeholder')"
              required />
          </div>
        </div>

        <div class="space-y-2">
          <Label for="authors">{{ t('books.form.fields.authors.label') }}</Label>
          <Input id="authors" v-model="bookForm.authors" :placeholder="t('books.form.fields.authors.placeholder')"
            required />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <Label for="publisher">{{ t('books.form.fields.publisher.label') }}</Label>
            <Input id="publisher" v-model="bookForm.publisher"
              :placeholder="t('books.form.fields.publisher.placeholder')" />
          </div>

          <div class="space-y-2">
            <Label for="publishedYear">{{ t('books.form.fields.publishedYear.label') }}</Label>
            <Input id="publishedYear" type="number" v-model="bookForm.publishedYear" :min="1000"
              :max="new Date().getFullYear() + 1" />
          </div>
        </div>

        <div class="space-y-2">
          <Label for="description">{{ t('books.form.fields.description.label') }}</Label>
          <textarea id="description" v-model="bookForm.description"
            :placeholder="t('books.form.fields.description.placeholder')"
            class="w-full min-h-[100px] p-3 border rounded-md resize-none" />
        </div>

        <div class="space-y-2">
          <Label for="coverURL">{{ t('books.form.fields.coverURL.label') }}</Label>
          <Input id="coverURL" type="url" v-model="bookForm.coverURL"
            :placeholder="t('books.form.fields.coverURL.placeholder')" />
        </div>

        <div class="space-y-2">
          <Label for="totalQuantity">{{ t('books.form.fields.totalQuantity.label') }}</Label>
          <Input id="totalQuantity" type="number" v-model="bookForm.totalQuantity" :min="1" required />
        </div>

        <DialogFooter>
          <Button type="button" variant="outline" @click="closeAddBookDialog">{{ t('books.form.buttons.cancel')
            }}</Button>
          <Button type="submit" :disabled="isSubmitting">
            {{ isSubmitting ? t('books.form.buttons.adding') : t('books.form.buttons.add') }}
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
