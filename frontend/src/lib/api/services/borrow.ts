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

  async borrowBookByBookId(request: {
    userId: number
    bookId: number
  }): Promise<BorrowResponseDto> {
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
    // Get current borrowings to find the record we're returning
    const currentBorrowings = await this.getMyCurrentBorrowings()
    const borrowRecord = currentBorrowings.find((b) => b.borrowId === borrowId)

    if (!borrowRecord) {
      throw new Error(`未找到借阅记录 ID: ${borrowId}`)
    }

    // Use the secure endpoint that validates ownership
    const result = await this.returnBookById(borrowId)

    // Return a compatible response format with real data for backward compatibility
    return {
      borrowId: borrowRecord.borrowId,
      userId: borrowRecord.userId,
      username: borrowRecord.username,
      copyId: borrowRecord.copyId,
      bookTitle: borrowRecord.bookTitle,
      isbn: borrowRecord.isbn,
      borrowTime: borrowRecord.borrowTime,
      returnTime: new Date().toISOString(), // Current timestamp as return time
      status: 'RETURNED' as any,
      message: result.message,
    }
  }

  // Convenience method for renewing by borrow ID - SECURE
  async renewBook(borrowId: number): Promise<BorrowResponseDto> {
    // Get current borrowings to find the record we're renewing
    const currentBorrowings = await this.getMyCurrentBorrowings()
    const borrowRecord = currentBorrowings.find((b) => b.borrowId === borrowId)

    if (!borrowRecord) {
      throw new Error(`未找到借阅记录 ID: ${borrowId}`)
    }

    // Use the secure endpoint that validates ownership
    const result = await this.renewBookById(borrowId)

    // Get updated borrowings after renewal to get the new due date
    const updatedBorrowings = await this.getMyCurrentBorrowings()
    const updatedBorrowRecord = updatedBorrowings.find((b) => b.borrowId === borrowId)

    // Return a compatible response format with real data for backward compatibility
    return {
      borrowId: borrowRecord.borrowId,
      userId: borrowRecord.userId,
      username: borrowRecord.username,
      copyId: borrowRecord.copyId,
      bookTitle: borrowRecord.bookTitle,
      isbn: borrowRecord.isbn,
      borrowTime: borrowRecord.borrowTime,
      returnTime: updatedBorrowRecord?.returnTime || borrowRecord.returnTime, // Use updated due date if available
      status: 'BORROWED' as any,
      message: result.message,
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

  // Get complete borrowing history (including returned books) with pagination
  async getUserBorrows(params?: { page?: number; size?: number }): Promise<PagedResponse<Borrow>> {
    // Call the correct backend endpoint that returns paginated borrowing history
    const page = params?.page ?? 0
    const size = params?.size ?? 10

    return apiClient.get<PagedResponse<Borrow>>(`${this.basePath}/user`, {
      page,
      size,
    })
  }

  async getOverdueBorrows(params?: { page?: number; size?: number }): Promise<Borrow[]> {
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
