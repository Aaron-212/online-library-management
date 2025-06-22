import { apiClient } from '../client'
import type {
  FavoriteDto,
  FavoriteResponseDto,
  FavoriteCreateDto,
  MessageResponse,
  PagedResponse,
} from '../types'

export class FavoritesService {
  private basePath = '/favorites'

  async getUserFavorites(params?: {
    page?: number
    size?: number
  }): Promise<PagedResponse<FavoriteDto>> {
    return apiClient.get<PagedResponse<FavoriteDto>>(`${this.basePath}/user`, params)
  }

  async addFavorite(data: FavoriteCreateDto): Promise<FavoriteResponseDto> {
    return apiClient.post<FavoriteResponseDto>(this.basePath, data)
  }

  async removeFavorite(favoriteId: number): Promise<MessageResponse> {
    return apiClient.delete<MessageResponse>(`${this.basePath}/${favoriteId}`)
  }

  async removeFavoriteByBook(bookId: number): Promise<MessageResponse> {
    return apiClient.delete<MessageResponse>(`${this.basePath}/book/${bookId}`)
  }

  async checkIsFavorite(bookId: number): Promise<boolean> {
    try {
      const response = await apiClient.get<{ isFavorite: boolean }>(
        `${this.basePath}/check/${bookId}`,
      )
      return response.isFavorite
    } catch {
      return false
    }
  }

  async getFavoritesCount(): Promise<number> {
    try {
      const response = await apiClient.get<{ count: number }>(`${this.basePath}/count`)
      return response.count
    } catch {
      return 0
    }
  }
}

export const favoritesService = new FavoritesService()
