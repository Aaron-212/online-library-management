import { apiClient } from '../client'
import type { AuthResponse, MessageResponse, UserLoginDto, UserRegisterDto } from '../types'

export class AuthService {
  private basePath = '/auth'

  async login(credentials: UserLoginDto): Promise<AuthResponse | string> {
    return apiClient.post<AuthResponse | string>(`${this.basePath}/login`, credentials)
  }

  async register(userData: UserRegisterDto): Promise<MessageResponse> {
    return apiClient.post<MessageResponse>(`${this.basePath}/register`, userData)
  }

  async verifyToken(): Promise<MessageResponse> {
    return apiClient.get<MessageResponse>(`${this.basePath}/verify`)
  }

  async changePassword(oldPassword: string, newPassword: string): Promise<MessageResponse> {
    return apiClient.put<MessageResponse>(
      `${this.basePath}/password?oldPassword=${encodeURIComponent(oldPassword)}&newPassword=${encodeURIComponent(newPassword)}`,
    )
  }
}

export const authService = new AuthService()
