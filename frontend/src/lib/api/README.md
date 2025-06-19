# Frontend API Documentation

This is a comprehensive, type-safe API client for the Online Library Management System frontend. It provides a clean, modular interface to connect with the backend API.

## Architecture

The API system is built with the following structure:

```
src/lib/api/
├── client.ts              # Base API client with authentication and error handling
├── types.ts               # TypeScript interfaces for all DTOs and responses
├── services/              # Domain-specific API services
│   ├── auth.ts           # Authentication endpoints
│   ├── books.ts          # Book management
│   ├── users.ts          # User management
│   ├── borrow.ts         # Borrowing operations
│   ├── comments.ts       # Comment system
│   ├── notices.ts        # Notice management
│   ├── fees.ts           # Fee calculations and payments
│   ├── statistics.ts     # Statistics and analytics
│   └── borrowing-rules.ts # Borrowing rules management
└── index.ts              # Main export file
```

## Features

- ✅ **Type Safety**: Full TypeScript support with comprehensive interfaces
- ✅ **Authentication**: Automatic JWT token handling via auth store
- ✅ **Error Handling**: Structured error responses with proper error types
- ✅ **Interceptors**: Request/response interceptors for auth and error handling
- ✅ **Modular Design**: Domain-specific services for better organization
- ✅ **Pagination Support**: Built-in support for paginated responses
- ✅ **Reactive Integration**: Works seamlessly with Vue composables

## Quick Start

### Basic Usage

```typescript
import { api } from '@/lib/api'

// Get all books
const books = await api.books.getAll({ page: 0, size: 10 })

// Search books
const searchResults = await api.books.search({ 
  keyword: 'javascript', 
  page: 0, 
  size: 20 
})

// Borrow a book
const borrowResponse = await api.borrow.borrowBook({ bookId: 123 })

// Get user's current borrows
const userBorrows = await api.borrow.getUserBorrows()
```

### Using with Vue Composables

```vue
<script setup lang="ts">
import { useBooks, useBorrow } from '@/composables/useApi'

const booksApi = useBooks()
const borrowApi = useBorrow()

// Reactive data
const books = booksApi.books
const isLoading = booksApi.isLoading
const error = booksApi.error

// Methods
const searchBooks = (query: string) => {
  booksApi.searchBooks(query, { page: 0, size: 10 })
}

const borrowBook = async (bookId: number) => {
  const result = await borrowApi.borrowBook(bookId)
  if (result) {
    // Success handling
  } else if (borrowApi.error.value) {
    // Error handling
  }
}
</script>
```

## API Services

### Authentication Service

```typescript
import { authService } from '@/lib/api'

// Login
const response = await authService.login({
  usernameOrEmail: 'user@example.com',
  password: 'hashedPassword'
})

// Register
await authService.register({
  username: 'newuser',
  email: 'user@example.com',
  password: 'hashedPassword'
})

// Change password
await authService.changePassword('oldPassword', 'newPassword')

// Verify token
await authService.verifyToken()
```

### Books Service

```typescript
import { booksService } from '@/lib/api'

// Get all books (paginated)
const books = await booksService.getAll({ page: 0, size: 20 })

// Get book by ID
const book = await booksService.getById(123)

// Search books
const results = await booksService.search({
  keyword: 'programming',
  page: 0,
  size: 10
})

// Create book (admin only)
await booksService.create({
  isbn: '978-1234567890',
  title: 'New Book',
  language: 'English',
  authorNames: ['Author Name'],
  publisherNames: ['Publisher'],
  categoryName: 'Programming',
  totalQuantity: 5
})

// Update book
await booksService.update(123, {
  title: 'Updated Title'
})

// Check if book exists by ISBN
const exists = await booksService.checkExistsByIsbn('978-1234567890')
```

### Borrow Service

```typescript
import { borrowService } from '@/lib/api'

// Borrow a book
const borrow = await borrowService.borrowBook({ bookId: 123 })

// Return a book
await borrowService.returnBook(456)

// Renew a book
await borrowService.renewBook(456)

// Get user's borrows
const userBorrows = await borrowService.getUserBorrows({ page: 0, size: 10 })

// Get all borrows (admin)
const allBorrows = await borrowService.getAllBorrows({ page: 0, size: 20 })

// Get overdue borrows
const overdue = await borrowService.getOverdueBorrows()
```

### Comments Service

```typescript
import { commentsService } from '@/lib/api'

// Get comments for a book
const comments = await commentsService.getByBookId(123, { page: 0, size: 10 })

// Create a comment
await commentsService.create({
  bookId: 123,
  content: 'Great book!',
  rating: 5
})

// Update a comment
await commentsService.update(456, {
  content: 'Updated review',
  rating: 4
})

// Delete a comment
await commentsService.delete(456)
```

### Statistics Service

```typescript
import { statisticsService } from '@/lib/api'

// Get book statistics
const bookStats = await statisticsService.getBookStatistics()
// Returns: { totalBooks, availableBooks, borrowedBooks, ... }

// Get top books
const topBooks = await statisticsService.getTopBooks({
  limit: 10,
  startDate: '2024-01-01',
  endDate: '2024-12-31'
})

// Get user statistics
const userStats = await statisticsService.getUserStatistics()
```

## Error Handling

The API client provides structured error handling:

```typescript
try {
  const books = await api.books.getAll()
} catch (error) {
  const apiError = error as ApiError
  console.error('Status:', apiError.status)
  console.error('Message:', apiError.error)
  
  // Handle specific error codes
  if (apiError.status === 401) {
    // Unauthorized - redirect to login
  } else if (apiError.status === 403) {
    // Forbidden - show permission error
  }
}
```

## Composables

The API system includes Vue composables for reactive state management:

### useApi()
Base composable for any API call with loading, error, and data state.

### useBooks()
Specialized composable for book operations with pagination support.

### useBorrow()
Handles borrowing operations with reactive state.

### useComments()
Manages comments with CRUD operations.

### useStatistics()
Provides statistics data with reactive updates.

## Configuration

### Base URL
The API base URL is configured in `client.ts`:
```typescript
const API_BASE_URL = 'http://localhost:8090/api/v1'
```

### Authentication
Authentication is handled automatically via the auth store. The client will:
- Automatically include JWT tokens in requests
- Handle token refresh (if implemented)
- Clear auth state on 401 responses

## Type Safety

All API responses and requests are fully typed:

```typescript
// Book interface
interface Book {
  id: number
  isbn: string
  title: string
  language: string
  availableQuantity: number
  totalQuantity: number
  description?: string
  authors: Author[]
  publishers: Publisher[]
  indexCategory: IndexCategory
}

// Paginated response
interface PagedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}
```

## Integration with Auth Store

The API client is integrated with the Pinia auth store:

```typescript
// The client automatically:
// - Includes auth headers when user is logged in
// - Handles auth errors by triggering logout
// - Provides methods for password changes and token verification

const authStore = useAuthStore()

// Updated auth store methods
await authStore.login(username, password)
await authStore.register(username, email, password)
await authStore.changePassword(oldPassword, newPassword)
await authStore.verifyToken()
```

## Best Practices

1. **Use Composables**: Prefer using the provided composables over direct service calls
2. **Error Handling**: Always handle errors appropriately in your components
3. **Type Safety**: Leverage TypeScript interfaces for better development experience
4. **Reactive Updates**: Use the reactive properties from composables for UI updates
5. **Loading States**: Show loading indicators using the reactive `isLoading` properties

## Examples

See `components/ApiExample.vue` for a comprehensive example showing how to use the API system in a real Vue component with:
- Book searching and pagination
- Borrowing operations
- Error handling and user feedback
- Reactive state management
- Statistics display

This API system provides a robust, type-safe, and developer-friendly interface for all backend interactions in the Online Library Management System frontend.