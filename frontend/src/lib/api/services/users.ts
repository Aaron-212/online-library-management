import { apiClient } from '../client'
import type { 
  User, 
  UserPublic, 
  UserUpdateDto, 
  MessageResponse 
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
}

export const usersService = new UsersService()