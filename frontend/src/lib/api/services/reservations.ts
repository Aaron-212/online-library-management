import { apiClient } from '../client'
import type { MessageResponse, PagedResponse, Reservation, ReserveRequestDto } from '../types'

export class ReservationsService {
  private basePath = '/reservations'

  async getUserReservations(params?: {
    page?: number
    size?: number
  }): Promise<PagedResponse<Reservation>> {
    return apiClient.get<PagedResponse<Reservation>>(`${this.basePath}/user`, params)
  }

  async getAllReservations(params?: {
    page?: number
    size?: number
  }): Promise<PagedResponse<Reservation>> {
    return apiClient.get<PagedResponse<Reservation>>(this.basePath, params)
  }

  async createReservation(data: ReserveRequestDto): Promise<MessageResponse> {
    return apiClient.post<MessageResponse>(this.basePath, data)
  }

  async cancelReservation(reservationId: number): Promise<MessageResponse> {
    return apiClient.delete<MessageResponse>(`${this.basePath}/${reservationId}`)
  }

  async getReservation(reservationId: number): Promise<Reservation> {
    return apiClient.get<Reservation>(`${this.basePath}/${reservationId}`)
  }
}

export const reservationsService = new ReservationsService()
