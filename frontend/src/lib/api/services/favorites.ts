import { apiClient } from '../client'
import type { 
  Book,
  PagedResponse,
  MessageResponse 
} from '../types'

export interface Favorite {
  id: number
  user: {
    id: number
    username: string
  }
  book: Book
  createdDate: string
}

export interface FavoriteCreateDto {
  bookId: number
}

export class FavoritesService {
  private basePath = '/favorites'

  async getUserFavorites(params?: { page?: number; size?: number }): Promise<PagedResponse<Favorite>> {
    return apiClient.get<PagedResponse<Favorite>>(`${this.basePath}/user`, params)
  }

  async getAllFavorites(params?: { page?: number; size?: number }): Promise<PagedResponse<Favorite>> {
    return apiClient.get<PagedResponse<Favorite>>(this.basePath, params)
  }

  async addFavorite(data: FavoriteCreateDto): Promise<MessageResponse> {
    return apiClient.post<MessageResponse>(this.basePath, data)
  }

  async removeFavorite(favoriteId: number): Promise<MessageResponse> {
    return apiClient.delete<MessageResponse>(`${this.basePath}/${favoriteId}`)
  }

  async checkIsFavorite(bookId: number): Promise<boolean> {
    try {
      const response = await apiClient.get<{ isFavorite: boolean }>(`${this.basePath}/check/${bookId}`)
      return response.isFavorite
    } catch {
      return false
    }
  }
}

export const favoritesService = new FavoritesService()