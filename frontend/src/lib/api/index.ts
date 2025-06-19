// Export the main client
export { apiClient, default as client } from './client'

// Export all types
export * from './types'

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

// Export all services
export { authService, booksService, usersService, borrowService, commentsService, noticesService, feesService, statisticsService, borrowingRulesService }

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