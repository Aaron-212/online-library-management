import { ref, computed } from 'vue'
import { api } from '@/lib/api'
import type { ApiError } from '@/lib/api/client'

export function useApi() {
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const data = ref<any>(null)

  const hasError = computed(() => error.value !== null)
  const hasData = computed(() => data.value !== null)

  const clearError = () => {
    error.value = null
  }

  const clearData = () => {
    data.value = null
  }

  const reset = () => {
    isLoading.value = false
    error.value = null
    data.value = null
  }

  const execute = async <T>(apiCall: () => Promise<T>): Promise<T | null> => {
    isLoading.value = true
    error.value = null
    
    try {
      const result = await apiCall()
      data.value = result
      return result
    } catch (err) {
      const apiError = err as ApiError
      error.value = apiError.error || apiError.message || 'An unexpected error occurred'
      console.error('API Error:', err)
      return null
    } finally {
      isLoading.value = false
    }
  }

  return {
    // State
    isLoading,
    error,
    data,
    
    // Computed
    hasError,
    hasData,
    
    // Actions
    execute,
    clearError,
    clearData,
    reset,
    
    // API instances
    api,
  }
}

// Specialized composables for common use cases
export function useBooks() {
  const { execute, isLoading, error, data, hasError, clearError, reset } = useApi()

  const books = computed(() => data.value?.content || [])
  const totalPages = computed(() => data.value?.totalPages || 0)
  const totalElements = computed(() => data.value?.totalElements || 0)

  const fetchBooks = (params?: { page?: number; size?: number; sort?: string }) => {
    return execute(() => api.books.getAll(params))
  }

  const searchBooks = (keyword: string, params?: { page?: number; size?: number }) => {
    return execute(() => api.books.search({ keyword, ...params }))
  }

  const fetchBookById = (id: number) => {
    return execute(() => api.books.getById(id))
  }

  return {
    books,
    totalPages,
    totalElements,
    isLoading,
    error,
    hasError,
    clearError,
    reset,
    fetchBooks,
    searchBooks,
    fetchBookById,
  }
}

export function useBorrow() {
  const { execute, isLoading, error, data, hasError, clearError, reset } = useApi()

  const borrows = computed(() => data.value?.content || [])
  const totalPages = computed(() => data.value?.totalPages || 0)

  const fetchUserBorrows = (params?: { page?: number; size?: number }) => {
    return execute(() => api.borrow.getUserBorrows(params))
  }

  const borrowBook = async (bookId: number) => {
    // Get current user data to get the user ID
    const currentUser = await api.users.getCurrentUser()
    
    // Get available copies for the book
    const copies = await api.books.getCopies(bookId)
    const availableCopy = copies.find(copy => copy.isAvailable)
    
    if (!availableCopy) {
      throw new Error('No available copies for this book')
    }
    
    return execute(() => api.borrow.borrowBook({ 
      userId: currentUser.id, 
      copyId: availableCopy.id 
    }))
  }

  const returnBook = (borrowId: number) => {
    return execute(() => api.borrow.returnBook(borrowId))
  }

  const renewBook = (borrowId: number) => {
    return execute(() => api.borrow.renewBook(borrowId))
  }

  return {
    borrows,
    totalPages,
    isLoading,
    error,
    hasError,
    clearError,
    reset,
    fetchUserBorrows,
    borrowBook,
    returnBook,
    renewBook,
  }
}

export function useComments() {
  const { execute, isLoading, error, data, hasError, clearError, reset } = useApi()

  const comments = computed(() => data.value?.content || [])
  const totalPages = computed(() => data.value?.totalPages || 0)

  const fetchCommentsForBook = (bookId: number, params?: { page?: number; size?: number }) => {
    return execute(() => api.comments.getByBookId(bookId, params))
  }

  const createComment = (bookId: number, content: string, rating: number) => {
    return execute(() => api.comments.create({ bookId, content, rating }))
  }

  const updateComment = (commentId: number, content?: string, rating?: number) => {
    return execute(() => api.comments.update(commentId, { content, rating }))
  }

  const deleteComment = (commentId: number) => {
    return execute(() => api.comments.delete(commentId))
  }

  return {
    comments,
    totalPages,
    isLoading,
    error,
    hasError,
    clearError,
    reset,
    fetchCommentsForBook,
    createComment,
    updateComment,
    deleteComment,
  }
}

export function useStatistics() {
  const { execute, isLoading, error, data, hasError, clearError, reset } = useApi()

  const bookStats = ref<any>(null)
  const userStats = ref<any>(null)
  const topBooks = ref<any[]>([])

  const fetchBookStatistics = async () => {
    const result = await execute(() => api.statistics.getBookStatistics())
    if (result) bookStats.value = result
    return result
  }

  const fetchUserStatistics = async () => {
    const result = await execute(() => api.statistics.getUserStatistics())
    if (result) userStats.value = result
    return result
  }

  const fetchTopBooks = async (limit: number, startDate?: string, endDate?: string) => {
    const result = await execute(() => api.statistics.getTopBooks({ limit, startDate, endDate }))
    if (result) topBooks.value = result
    return result
  }

  return {
    bookStats,
    userStats,
    topBooks,
    isLoading,
    error,
    hasError,
    clearError,
    reset,
    fetchBookStatistics,
    fetchUserStatistics,
    fetchTopBooks,
  }
}
