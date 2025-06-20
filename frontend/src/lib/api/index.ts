// Export the main client
export { default as apiClient } from './client'

// Export all types
export type * from './types'

// Import services
import { authService } from './services/auth'
import { booksService } from './services/books'
import { usersService } from './services/users'
import { borrowService } from './services/borrow'
import { commentsService } from './services/comments'
import { noticesService } from './services/notices'
import { feesService } from './services/fees'
import { statisticsService } from './services/statistics'
import { borrowingRulesService } from './services/borrowing-rules'
import { reservationsService } from './services/reservations'
import { favoritesService } from './services/favorites'

// Export all services
export { authService, booksService, usersService, borrowService, commentsService, noticesService, feesService, statisticsService, borrowingRulesService, reservationsService, favoritesService }

// Create a unified API object
export const api = {
  auth: authService,
  books: booksService,
  users: usersService,
  borrow: borrowService,
  comments: commentsService,
  notices: noticesService,
  fees: feesService,
  statistics: statisticsService,
  borrowingRules: borrowingRulesService,
  reservations: reservationsService,
  favorites: favoritesService,
}

// Export service classes for advanced usage
export { AuthService } from './services/auth'
export { BooksService } from './services/books'
export { UsersService } from './services/users'
export { BorrowService } from './services/borrow'
export { CommentsService } from './services/comments'
export { NoticesService } from './services/notices'
export { FeesService } from './services/fees'
export { StatisticsService } from './services/statistics'
export { BorrowingRulesService } from './services/borrowing-rules'
export { ReservationsService } from './services/reservations'
export { FavoritesService } from './services/favorites'