import { apiClient } from '../client'
import type { 
  BookStatisticsDto, 
  TopBooksRequestDto,
  Book 
} from '../types'

export class StatisticsService {
  private basePath = '/statistics'

  async getBookStatistics(): Promise<BookStatisticsDto> {
    return apiClient.get<BookStatisticsDto>(`${this.basePath}/books`)
  }

  async getTopBooks(request: TopBooksRequestDto): Promise<Book[]> {
    return apiClient.post<Book[]>(`${this.basePath}/top-books`, request)
  }

  async getUserStatistics(): Promise<{
    totalBorrows: number
    activeBorrows: number
    overdueBorrows: number
    totalFees: number
    unpaidFees: number
  }> {
    return apiClient.get(`${this.basePath}/user`)
  }
}

export const statisticsService = new StatisticsService()