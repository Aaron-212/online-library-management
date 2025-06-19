import { apiClient } from '../client'
import type { 
  UserLoginDto, 
  UserRegisterDto, 
  AuthResponse, 
  MessageResponse 
} from '../types'

export class AuthService {
  private basePath = '/auth'

  async login(credentials: UserLoginDto): Promise<AuthResponse> {
    return apiClient.post<AuthResponse>(`${this.basePath}/login`, credentials)
  }

  async register(userData: UserRegisterDto): Promise<MessageResponse> {
    return apiClient.post<MessageResponse>(`${this.basePath}/register`, userData)
  }

  async verifyToken(): Promise<MessageResponse> {
    return apiClient.get<MessageResponse>(`${this.basePath}/verify`)
  }

  async changePassword(oldPassword: string, newPassword: string): Promise<MessageResponse> {
    const params = new URLSearchParams({
      oldPassword,
      newPassword
    })
    
    return apiClient.put<MessageResponse>(`${this.basePath}/password?${params}`)
  }
}

export const authService = new AuthService()