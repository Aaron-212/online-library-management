import { apiClient } from '../client'
import type { 
  User, 
  UserPublic, 
  UserUpdateDto, 
  MessageResponse,
  PagedResponse
} from '../types'

export class UsersService {
  private basePath = '/users'

  async getById(id: number): Promise<UserPublic> {
    return apiClient.get<UserPublic>(`${this.basePath}/${id}`)
  }

  async getByUsername(username: string): Promise<UserPublic> {
    return apiClient.get<UserPublic>(`${this.basePath}/username/${username}`)
  }

  async getCurrentUser(): Promise<User> {
    return apiClient.get<User>(`${this.basePath}/me`)
  }

  async updateCurrentUser(userData: UserUpdateDto): Promise<MessageResponse> {
    return apiClient.put<MessageResponse>(`${this.basePath}/me`, userData)
  }

  // Admin methods
  async getAllUsers(params?: { page?: number; size?: number; search?: string }): Promise<PagedResponse<UserPublic>> {
    return apiClient.get<PagedResponse<UserPublic>>(`${this.basePath}/all`, params)
  }
}

export const usersService = new UsersService()