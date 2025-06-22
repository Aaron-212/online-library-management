<template>
  <div class="p-6 space-y-6">
    <h1 class="text-2xl font-bold">API Usage Examples</h1>

    <!-- Books Section -->
    <div class="border rounded-lg p-4">
      <h2 class="text-xl font-semibold mb-4">Books</h2>

      <div class="space-y-4">
        <!-- Search Books -->
        <div>
          <input
            v-model="searchQuery"
            class="border rounded px-3 py-2 mr-2"
            placeholder="Search books..."
          />
          <button
            :disabled="booksApi.isLoading.value"
            class="bg-blue-500 text-white px-4 py-2 rounded disabled:opacity-50"
            @click="handleSearchBooks"
          >
            {{ booksApi.isLoading.value ? 'Searching...' : 'Search' }}
          </button>
        </div>

        <!-- Error Display -->
        <div v-if="booksApi.hasError.value" class="text-red-600 bg-red-100 p-3 rounded">
          {{ booksApi.error.value }}
          <button class="ml-2 underline" @click="booksApi.clearError">Dismiss</button>
        </div>

        <!-- Books List -->
        <div v-if="booksApi.books.value.length > 0" class="space-y-2">
          <div v-for="book in booksApi.books.value" :key="book.id" class="border p-3 rounded">
            <h3 class="font-semibold">{{ book.title }}</h3>
            <p class="text-sm text-gray-600">
              {{ book.authors.map((a: any) => a.name).join(', ') }}
            </p>
            <p class="text-sm">Available: {{ book.availableQuantity }}/{{ book.totalQuantity }}</p>

            <button
              :disabled="borrowApi.isLoading.value || book.availableQuantity === 0"
              class="mt-2 bg-green-500 text-white px-3 py-1 rounded text-sm disabled:opacity-50"
              @click="handleBorrowBook(book.id)"
            >
              {{ borrowApi.isLoading.value ? 'Processing...' : 'Borrow' }}
            </button>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="booksApi.totalPages.value > 1" class="flex space-x-2">
          <button
            v-for="page in Math.min(booksApi.totalPages.value, 5)"
            :key="page"
            :class="[
              'px-3 py-1 rounded',
              currentPage === page - 1 ? 'bg-blue-500 text-white' : 'bg-gray-200',
            ]"
            @click="handlePageChange(page - 1)"
          >
            {{ page }}
          </button>
        </div>
      </div>
    </div>

    <!-- User Borrows Section -->
    <div class="border rounded-lg p-4">
      <h2 class="text-xl font-semibold mb-4">My Borrows</h2>

      <button
        :disabled="borrowApi.isLoading.value"
        class="bg-blue-500 text-white px-4 py-2 rounded disabled:opacity-50 mb-4"
        @click="borrowApi.fetchUserBorrows()"
      >
        {{ borrowApi.isLoading.value ? 'Loading...' : 'Refresh Borrows' }}
      </button>

      <div v-if="borrowApi.hasError.value" class="text-red-600 bg-red-100 p-3 rounded mb-4">
        {{ borrowApi.error.value }}
      </div>

      <div v-if="borrowApi.borrows.value.length > 0" class="space-y-2">
        <div v-for="borrow in borrowApi.borrows.value" :key="borrow.borrowId" class="border p-3 rounded">
          <h3 class="font-semibold">{{ borrow.bookTitle }}</h3>
          <p class="text-sm">Due: {{ new Date(borrow.returnTime).toLocaleDateString() }}</p>
          <p :class="borrow.status === 'RETURNED' ? 'text-green-600' : 'text-orange-600'" class="text-sm">
            Status: {{ borrow.status === 'RETURNED' ? 'Returned' : 'Borrowed' }}
          </p>

          <div v-if="borrow.status === 'BORROWED'" class="mt-2 space-x-2">
            <button
              class="bg-red-500 text-white px-3 py-1 rounded text-sm"
              @click="handleReturnBook(borrow.borrowId)"
            >
              Return
            </button>
            <button
              class="bg-yellow-500 text-white px-3 py-1 rounded text-sm"
              @click="handleRenewBook(borrow.borrowId)"
            >
              Renew
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Statistics Section -->
    <div class="border rounded-lg p-4">
      <h2 class="text-xl font-semibold mb-4">Statistics</h2>

      <button
        :disabled="statsApi.isLoading.value"
        class="bg-blue-500 text-white px-4 py-2 rounded disabled:opacity-50 mb-4"
        @click="statsApi.fetchBookStatistics()"
      >
        {{ statsApi.isLoading.value ? 'Loading...' : 'Load Book Stats' }}
      </button>

      <div v-if="statsApi.bookStats.value" class="grid grid-cols-2 md:grid-cols-3 gap-4">
        <div class="bg-blue-100 p-3 rounded">
          <div class="text-2xl font-bold">{{ statsApi.bookStats.value.totalBooks }}</div>
          <div class="text-sm text-gray-600">Total Books</div>
        </div>
        <div class="bg-green-100 p-3 rounded">
          <div class="text-2xl font-bold">{{ statsApi.bookStats.value.availableBooks }}</div>
          <div class="text-sm text-gray-600">Available</div>
        </div>
        <div class="bg-orange-100 p-3 rounded">
          <div class="text-2xl font-bold">{{ statsApi.bookStats.value.borrowedBooks }}</div>
          <div class="text-sm text-gray-600">Borrowed</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { useBooks, useBorrow, useStatistics } from '@/composables/useApi'
import { toast } from 'vue-sonner'

// Reactive state
const searchQuery = ref('')
const currentPage = ref(0)

// API composables
const booksApi = useBooks()
const borrowApi = useBorrow()
const statsApi = useStatistics()

// Methods
const handleSearchBooks = async () => {
  if (searchQuery.value.trim()) {
    await booksApi.searchBooks(searchQuery.value.trim(), { page: currentPage.value, size: 10 })
  } else {
    await booksApi.fetchBooks({ page: currentPage.value, size: 10 })
  }
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  handleSearchBooks()
}

const handleBorrowBook = async (bookId: number) => {
  const result = await borrowApi.borrowBook(bookId)
  if (result) {
    toast.success('Book borrowed successfully!')
    // Refresh the books list to update availability
    handleSearchBooks()
  } else if (borrowApi.error.value) {
    toast.error(borrowApi.error.value)
  }
}

const handleReturnBook = async (borrowId: number) => {
  const result = await borrowApi.returnBook(borrowId)
  if (result) {
    toast.success('Book returned successfully!')
    borrowApi.fetchUserBorrows()
  } else if (borrowApi.error.value) {
    toast.error(borrowApi.error.value)
  }
}

const handleRenewBook = async (borrowId: number) => {
  const result = await borrowApi.renewBook(borrowId)
  if (result) {
    toast.success('Book renewed successfully!')
    borrowApi.fetchUserBorrows()
  } else if (borrowApi.error.value) {
    toast.error(borrowApi.error.value)
  }
}

// Initialize
onMounted(() => {
  booksApi.fetchBooks({ page: 0, size: 10 })
  borrowApi.fetchUserBorrows()
  statsApi.fetchBookStatistics()
})
</script>
