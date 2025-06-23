import { apiClient } from '../client'
import type { Book, BookStatisticsDto, TopBooksRequestDto, PagedResponse, UserAdmin } from '../types'

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

  // Admin-only statistics endpoints
  async getUserBehaviorAnalysis(): Promise<{
    registrationCount: number
    activeUserCount: number
  }> {
    return apiClient.get(`${this.basePath}/user-behavior`)
  }

  async getBookInventoryStatistics(): Promise<{
    [categoryName: string]: {
      totalCount: number
      availableCount: number
    }
  }> {
    return apiClient.get(`${this.basePath}/inventory`)
  }

  async getDashboardSummary(): Promise<{
    userBehavior: {
      registrationCount: number
      activeUserCount: number
    }
    inventory: {
      [categoryName: string]: {
        totalCount: number
        availableCount: number
      }
    }
    timestamp: number
  }> {
    return apiClient.get(`${this.basePath}/dashboard`)
  }

  // Helper method to get total user count (for admin dashboard)
  async getTotalUsers(): Promise<number> {
    const response = await apiClient.get<PagedResponse<UserAdmin>>('/users/all', { page: 0, size: 1 })
    return response.totalElements
  }
}

export const statisticsService = new StatisticsService()
