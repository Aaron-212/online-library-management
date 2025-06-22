// User types
export interface User {
  id: number
  username: string
  email: string
  role: 'USER' | 'ADMIN'
  createdTime: string
  lastUpdateTime: string
}

export interface UserPublic {
  id: number
  username: string
  createdTime: string
}

export interface UserAdmin {
  id: number
  username: string
  email: string
  role: 'USER' | 'ADMIN'
  createdTime: string
  lastUpdateTime: string
}

export interface UserLoginDto {
  usernameOrEmail: string
  password: string
}

export interface UserRegisterDto {
  username: string
  email: string
  password: string
}

export interface UserUpdateDto {
  email?: string
  username?: string
}

export interface UserCreateDto {
  username: string
  email: string
  password: string
  role?: 'USER' | 'ADMIN'
}

// Book types
export interface Book {
  id: number
  isbn: string
  title: string
  language: string
  location: string
  availableQuantity: number
  totalQuantity: number
  description?: string
  coverURL?: string
  authors: Author[]
  publishers: Publisher[]
  indexCategory: IndexCategory
}

export interface BookSummaryDto {
  id: number
  title: string
  authors: string[]
  publishers: string[]
  coverURL?: string
  availableQuantity: number
  totalQuantity: number
}

export interface Author {
  id: number
  name: string
}

export interface Publisher {
  id: number
  name: string
}

export interface IndexCategory {
  id: number
  name: string
  description?: string
}

export interface BookCreateDto {
  isbn: string
  title: string
  language: string
  description?: string
  coverURL?: string
  location?: string
  authorNames: string[]
  publisherNames: string[]
  categoryName: string
  totalQuantity: number
}

export interface BookUpdateDto {
  title?: string
  language?: string
  description?: string
  coverURL?: string
  location?: string
  authorNames?: string[]
  publisherNames?: string[]
  categoryName?: string
}

export interface BookCopy {
  id: number
  bookId: number
  bookTitle: string
  bookIsbn: string
  barcode: string
  status: BookCopyStatus
  purchasePrice?: number
  purchaseTime?: string
  lastMaintenance?: string
  createTime: string
  updateTime: string
}

export type BookCopyStatus = 'AVAILABLE' | 'BORROWED' | 'MAINTENANCE' | 'SCRAPPED' | 'DISCARDED'

export interface BookCopyCreateDto {
  bookId: number
  barcode: string
  status: BookCopyStatus
  purchasePrice?: number
  purchaseTime?: string
}

export interface BookCopyUpdateDto {
  barcode?: string
  status?: BookCopyStatus
  purchasePrice?: number
  purchaseTime?: string
  lastMaintenance?: string
}

// Borrow types
export interface Borrow {
  borrowId: number
  userId: number
  username: string
  copyId: number
  bookTitle: string
  isbn: string
  borrowTime: string
  returnTime: string
  actualReturnTime?: string
  status: string
  fine?: number
}

export interface BorrowRequestDto {
  userId: number
  copyId: number
}

export interface BorrowResponseDto {
  borrowId: number
  userId: number
  username: string
  copyId: number
  bookTitle: string
  isbn: string
  borrowTime: string
  returnTime: string
  status: string
  message: string
}

// Comment types
export interface Comment {
  id: number
  user: UserPublic
  book: Book
  content: string
  rating: number
  commentDate: string
  lastUpdateTime?: string
}

export interface CommentCreateDto {
  bookId: number
  content: string
  rating: number
}

export interface CommentUpdateDto {
  content?: string
  rating?: number
}

// Notice types
export interface Notice {
  id: number
  title: string
  content: string
  creatorUsername: string
  publishTime: string
  expireTime?: string
  status: number
  createTime: string
  updateTime: string
}

export interface NoticeCreateDto {
  title: string
  content: string
  publishTime: string
  expireTime?: string
  status: number
}

export interface NoticeUpdateDto {
  title?: string
  content?: string
  publishTime?: string
  expireTime?: string
  status?: number
}

// Fee types
export interface FeeCalculationDto {
  borrowId: number
  daysLate: number
  feeAmount: number
}

export interface FeeResponseDto {
  id: number
  borrowId: number
  amount: number
  paid: boolean
  calculationDate: string
  paymentDate?: string
}

// Borrowing Rules types
export interface BorrowingRule {
  id: number
  name: string
  maxBorrowDays: number
  maxRenewCount: number
  lateFeePerDay: number
  description?: string
}

export interface BorrowingRuleDto {
  name: string
  maxBorrowDays: number
  maxRenewCount: number
  lateFeePerDay: number
  description?: string
}

export interface BorrowingRuleUpdateDto {
  name?: string
  maxBorrowDays?: number
  maxRenewCount?: number
  lateFeePerDay?: number
  description?: string
}

// Statistics types
export interface BookStatisticsDto {
  totalBooks: number
  availableBooks: number
  borrowedBooks: number
  totalBorrows: number
  activeBorrows: number
  overdueBorrows: number
}

export interface TopBooksRequestDto {
  limit: number
  startDate?: string
  endDate?: string
}

// Pagination types
export interface PageInfo {
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface PagedResponse<T> {
  content: T[]
  pageable: PageInfo
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
  numberOfElements: number
  size: number
  number: number
}

// API Response wrappers
export interface AuthResponse {
  token: string
  message: string
}

export interface MessageResponse {
  message: string
}

export interface ErrorResponse {
  error: string
  message?: string
}

// Search and filter types
export interface SearchParams {
  keyword?: string
  page?: number
  size?: number
  sort?: string
}

export interface BookSearchParams extends SearchParams {
  author?: string
  category?: string
  language?: string
  available?: boolean
}

// Reservation types (if implemented)
export interface ReserveRequestDto {
  bookId: number
}

export interface Reservation {
  id: number
  user: UserPublic
  book: Book
  reservationDate: string
  expirationDate: string
  isActive: boolean
}

// Favorites types
export interface FavoriteDto {
  id: number
  book: Book
  createTime: string
}

export interface FavoriteResponseDto {
  id: number
  bookId: number
  bookTitle: string
  bookIsbn: string
  bookCoverURL?: string
  createTime: string
}

export interface FavoriteCreateDto {
  bookId: number
}

export type BookDto = Book;
