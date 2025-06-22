import { apiClient } from '../client'
import type {
  Borrow,
  BorrowRequestDto,
  BorrowResponseDto,
  MessageResponse,
  PagedResponse,
  Reservation,
  ReserveRequestDto,
} from '../types'

export class BorrowService {
  private basePath = '/borrow'

  async borrowBook(request: BorrowRequestDto): Promise<BorrowResponseDto> {
    return apiClient.post<BorrowResponseDto>(`${this.basePath}/borrow`, request)
  }

  async borrowBookByBookId(request: { userId: number; bookId: number }): Promise<BorrowResponseDto> {
    return apiClient.post<BorrowResponseDto>(`${this.basePath}/borrow-by-book`, request)
  }

  // Secure return method using borrow ID - RECOMMENDED
  async returnBookById(borrowId: number): Promise<{ message: string }> {
    return apiClient.put<{ message: string }>(`${this.basePath}/${borrowId}/return`)
  }

  // Secure renew method using borrow ID - RECOMMENDED  
  async renewBookById(borrowId: number): Promise<{ message: string }> {
    return apiClient.put<{ message: string }>(`${this.basePath}/${borrowId}/renew`)
  }

  // Legacy method for returning by copy ID (now with enhanced security validation)
  async returnBookDirect(request: BorrowRequestDto): Promise<BorrowResponseDto> {
    return apiClient.post<BorrowResponseDto>(`${this.basePath}/return`, request)
  }

  // Legacy method for renewing by copy ID (now with enhanced security validation)
  async renewBookDirect(request: BorrowRequestDto): Promise<BorrowResponseDto> {
    return apiClient.post<BorrowResponseDto>(`${this.basePath}/renew`, request)
  }

  // Convenience method for returning by borrow ID - SECURE
  async returnBook(borrowId: number): Promise<BorrowResponseDto> {
    // Use the secure endpoint that validates ownership
    const result = await this.returnBookById(borrowId)
    
    // Return a compatible response format for backward compatibility
    return {
      borrowId: borrowId,
      userId: 0, // Will be filled by backend
      username: '',
      copyId: 0,
      bookTitle: '',
      isbn: '',
      borrowTime: new Date().toISOString(),
      returnTime: new Date().toISOString(),
      status: 'RETURNED' as any,
      message: result.message
    }
  }

  // Convenience method for renewing by borrow ID - SECURE
  async renewBook(borrowId: number): Promise<BorrowResponseDto> {
    // Use the secure endpoint that validates ownership
    const result = await this.renewBookById(borrowId)
    
    // Return a compatible response format for backward compatibility
    return {
      borrowId: borrowId,
      userId: 0, // Will be filled by backend
      username: '',
      copyId: 0,
      bookTitle: '',
      isbn: '',
      borrowTime: new Date().toISOString(),
      returnTime: new Date().toISOString(),
      status: 'BORROWED' as any,
      message: result.message
    }
  }

  // Get current active borrowings for authenticated user
  async getMyCurrentBorrowings(): Promise<Borrow[]> {
    return apiClient.get<Borrow[]>(`${this.basePath}/my-current`)
  }

  // Get complete borrowing history for authenticated user
  async getMyBorrowHistory(): Promise<Borrow[]> {
    return apiClient.get<Borrow[]>(`${this.basePath}/my-history`)
  }

  // Legacy method for backward compatibility - returns current borrowings in paginated format
  async getUserBorrows(params?: { page?: number; size?: number }): Promise<PagedResponse<Borrow>> {
    // For backward compatibility, we'll simulate pagination from the current borrowings
    const currentBorrowings = await this.getMyCurrentBorrowings()
    const page = params?.page ?? 0
    const size = Math.max(1, params?.size ?? 10) // Prevent division by zero by ensuring size >= 1
    const startIndex = page * size
    const endIndex = startIndex + size
    const content = currentBorrowings.slice(startIndex, endIndex)
    
    const totalPages = Math.max(1, Math.ceil(currentBorrowings.length / size))
    // Fix isLast calculation: true when page is at or beyond the last available page
    const isLast = page >= totalPages - 1
    
    return {
      content,
      pageable: {
        page,
        size,
        totalElements: currentBorrowings.length,
        totalPages,
        first: page === 0,
        last: isLast
      },
      totalElements: currentBorrowings.length,
      totalPages,
      first: page === 0,
      last: isLast,
      numberOfElements: content.length,
      size,
      number: page
    }
  }

  async getBorrowById(id: number): Promise<Borrow> {
    return apiClient.get<Borrow>(`${this.basePath}/${id}`)
  }

  async getOverdueBorrows(params?: {
    page?: number
    size?: number
  }): Promise<Borrow[]> {
    return apiClient.get<Borrow[]>(`${this.basePath}/overdue`, params)
  }

  // Reservation methods (if implemented)
  async reserveBook(request: ReserveRequestDto): Promise<MessageResponse> {
    return apiClient.post<MessageResponse>(`${this.basePath}/reserve`, request)
  }

  async getUserReservations(params?: {
    page?: number
    size?: number
  }): Promise<PagedResponse<Reservation>> {
    return apiClient.get<PagedResponse<Reservation>>(`${this.basePath}/reservations/user`, params)
  }

  async cancelReservation(reservationId: number): Promise<MessageResponse> {
    return apiClient.delete<MessageResponse>(`${this.basePath}/reservations/${reservationId}`)
  }

  // Admin methods
  async adminBorrowBook(request: { userId: number; copyId: number }): Promise<BorrowResponseDto> {
    return apiClient.post<BorrowResponseDto>(`${this.basePath}/admin/borrow`, request)
  }

  async adminReturnBook(request: BorrowRequestDto): Promise<BorrowResponseDto> {
    return apiClient.post<BorrowResponseDto>(`${this.basePath}/admin/return`, request)
  }

  async adminRenewBook(request: BorrowRequestDto): Promise<BorrowResponseDto> {
    return apiClient.post<BorrowResponseDto>(`${this.basePath}/admin/renew`, request)
  }

  async adminGetAllBorrows(params?: { page?: number; size?: number }): Promise<Borrow[]> {
    return apiClient.get<Borrow[]>(`${this.basePath}/admin/all`, params)
  }
}

export const borrowService = new BorrowService()
