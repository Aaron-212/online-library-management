import { apiClient } from '../client'
import type { 
  FeeCalculationDto, 
  FeeResponseDto,
  PagedResponse,
  MessageResponse 
} from '../types'

export class FeesService {
  private basePath = '/fees'

  async calculate(borrowId: number): Promise<FeeCalculationDto> {
    return apiClient.get<FeeCalculationDto>(`${this.basePath}/calculate/${borrowId}`)
  }

  async getUserFees(params?: { page?: number; size?: number }): Promise<PagedResponse<FeeResponseDto>> {
    return apiClient.get<PagedResponse<FeeResponseDto>>(`${this.basePath}/user`, params)
  }

  async getAllFees(params?: { page?: number; size?: number }): Promise<PagedResponse<FeeResponseDto>> {
    return apiClient.get<PagedResponse<FeeResponseDto>>(this.basePath, params)
  }

  async getUnpaidFees(params?: { page?: number; size?: number }): Promise<PagedResponse<FeeResponseDto>> {
    return apiClient.get<PagedResponse<FeeResponseDto>>(`${this.basePath}/unpaid`, params)
  }

  async payFee(feeId: number): Promise<MessageResponse> {
    return apiClient.post<MessageResponse>(`${this.basePath}/${feeId}/pay`)
  }
}

export const feesService = new FeesService()