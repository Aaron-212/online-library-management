// Export the main client
export { default as apiClient } from './client'

// Export all types
export type * from './types'

// Import services
import { authService } from './services/auth'
import { booksService } from './services/books'
import { bookCopiesService } from './services/book-copies'
import { usersService } from './services/users'
import { borrowService } from './services/borrow'
import { commentsService } from './services/comments'
import { noticesService } from './services/notices'
import { feesService } from './services/fees'
import { statisticsService } from './services/statistics'
import { borrowingRulesService } from './services/borrowing-rules'
import { reservationsService } from './services/reservations'
import { favoritesService } from './services/favorites'
import { categoriesService } from './services/categories'

// Export all services
export {
  authService,
  booksService,
  bookCopiesService,
  usersService,
  borrowService,
  commentsService,
  noticesService,
  feesService,
  statisticsService,
  borrowingRulesService,
  reservationsService,
  favoritesService,
  categoriesService,
}

// Create a unified API object
export const api = {
  auth: authService,
  books: booksService,
  bookCopies: bookCopiesService,
  users: usersService,
  borrow: borrowService,
  comments: commentsService,
  notices: noticesService,
  fees: feesService,
  statistics: statisticsService,
  borrowingRules: borrowingRulesService,
  reservations: reservationsService,
  favorites: favoritesService,
  categories: categoriesService,
}

// Export service classes for advanced usage
export { AuthService } from './services/auth'
export { BooksService } from './services/books'
export { BookCopiesService } from './services/book-copies'
export { UsersService } from './services/users'
export { BorrowService } from './services/borrow'
export { CommentsService } from './services/comments'
export { NoticesService } from './services/notices'
export { FeesService } from './services/fees'
export { StatisticsService } from './services/statistics'
export { BorrowingRulesService } from './services/borrowing-rules'
export { ReservationsService } from './services/reservations'
export { FavoritesService } from './services/favorites'
export { CategoriesService } from './services/categories'
