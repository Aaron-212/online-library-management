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

  async returnBook(borrowId: number): Promise<MessageResponse> {
    return apiClient.put<MessageResponse>(`${this.basePath}/${borrowId}/return`)
  }

  async renewBook(borrowId: number): Promise<MessageResponse> {
    return apiClient.put<MessageResponse>(`${this.basePath}/${borrowId}/renew`)
  }

  async getUserBorrows(params?: { page?: number; size?: number }): Promise<PagedResponse<Borrow>> {
    return apiClient.get<PagedResponse<Borrow>>(`${this.basePath}/user`, params)
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

  async adminGetAllBorrows(params?: { page?: number; size?: number }): Promise<Borrow[]> {
    return apiClient.get<Borrow[]>(`${this.basePath}/admin/all`, params)
  }
}

export const borrowService = new BorrowService()
