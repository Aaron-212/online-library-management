import { apiClient } from '../client'
import type { BookStatisticsDto, LibraryStatisticsDto, TopBooksRequestDto } from '../types'

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
    totalUserCount: number
    registrationCount: number
    activeUserCount: number
  }> {
    const response = await apiClient.get<{
      analysis: {
        totalUserCount: number
        registrationCount: number
        activeUserCount: number
      }
      timestamp: number
    }>(`${this.basePath}/user-behavior`)
    return response.analysis
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
      totalUserCount: number
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
    const response = await apiClient.get<{
      userBehavior: {
        totalUserCount: number
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
    }>(`${this.basePath}/dashboard`)
    return response
  }
}

export const statisticsService = new StatisticsService()
