import { apiClient } from '../client'
import type { BookStatisticsDto, LibraryStatisticsDto, TopBooksRequestDto, PagedResponse, UserAdmin } from '../types'
import { usersService } from './users'

export class StatisticsService {
  private basePath = '/statistics'

  async getBookStatistics(): Promise<LibraryStatisticsDto> {
    return apiClient.get<LibraryStatisticsDto>(`${this.basePath}/books`)
  }

  async getTopBooks(request: TopBooksRequestDto): Promise<BookStatisticsDto[]> {
    return apiClient.post<BookStatisticsDto[]>(`${this.basePath}/top-books`, request)
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
    try {
      const response = await usersService.getAllUsers({ page: 0, size: 1 })
      return response.totalElements || 0
    } catch (error) {
      console.error('Error fetching total users:', error)
      return 0
    }
  }
}

export const statisticsService = new StatisticsService()
