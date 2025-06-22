import { apiClient } from '../client'
import type {
  MessageResponse,
  PagedResponse,
  User,
  UserPublic,
  UserAdmin,
  UserUpdateDto,
  UserCreateDto,
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
  async getAllUsers(params?: {
    page?: number
    size?: number
    search?: string
  }): Promise<PagedResponse<UserAdmin>> {
    return apiClient.get<PagedResponse<UserAdmin>>(`${this.basePath}/all`, params)
  }

  async createUser(userData: UserCreateDto): Promise<MessageResponse> {
    return apiClient.post<MessageResponse>(`${this.basePath}/create`, userData)
  }

  async updateUser(id: number, userData: UserUpdateDto): Promise<MessageResponse> {
    return apiClient.put<MessageResponse>(`${this.basePath}/${id}`, userData)
  }

  async updateUserRole(id: number, role: 'USER' | 'ADMIN'): Promise<MessageResponse> {
    return apiClient.put<MessageResponse>(`${this.basePath}/${id}/role?role=${role}`)
  }

  async deleteUser(id: number): Promise<MessageResponse> {
    return apiClient.delete<MessageResponse>(`${this.basePath}/${id}`)
  }
}

export const usersService = new UsersService()
