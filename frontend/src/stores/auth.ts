import { ref } from 'vue'
import { defineStore } from 'pinia'
import { authService } from '@/lib/api'
import { ApiError } from '@/lib/api/client'
import type { UserLoginDto, UserRegisterDto } from '@/lib/api'

interface User {
  username: string
  token: string
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const isAuthenticated = ref(false)
  const isLoading = ref(false)

  // Hash password using Web Crypto API
  const hashPassword = async (password: string): Promise<string> => {
    const encoder = new TextEncoder()
    const data = encoder.encode(password)
    const hashBuffer = await crypto.subtle.digest('SHA-256', data)
    const hashArray = Array.from(new Uint8Array(hashBuffer))
    return hashArray.map((b) => b.toString(16).padStart(2, '0')).join('')
  }

  // Register function
  const register = async (
    username: string,
    email: string,
    password: string,
  ): Promise<{ success: boolean; message?: string }> => {
    isLoading.value = true
    try {
      const hashedPassword = await hashPassword(password)

      const registerData: UserRegisterDto = {
        username,
        email,
        password: hashedPassword,
      }

      const response = await authService.register(registerData)
      return { success: true, message: response.message || 'Registration successful! Please log in.' }
    } catch (error) {
      console.error('Registration error:', error)
      const apiError = error as ApiError
      return {
        success: false,
        message: apiError.error || apiError.message || 'Registration failed. Please try again.',
      }
    } finally {
      isLoading.value = false
    }
  }

  // Login function
  const login = async (
    username: string,
    password: string,
  ): Promise<{ success: boolean; message?: string }> => {
    isLoading.value = true
    try {
      const hashedPassword = await hashPassword(password)

      const loginData: UserLoginDto = {
        usernameOrEmail: username,
        password: hashedPassword,
      }

      const response = await authService.login(loginData)

      user.value = {
        username,
        token: response.token,
      }

      isAuthenticated.value = true

      localStorage.setItem('auth_token', response.token)
      localStorage.setItem('username', username)

      return { success: true, message: response.message }
    } catch (error) {
      console.error('Login error:', error)
      const apiError = error as ApiError
      return {
        success: false,
        message: apiError.error || apiError.message || 'Login failed. Please try again.',
      }
    } finally {
      isLoading.value = false
    }
  }

  // Change password function
  const changePassword = async (
    oldPassword: string,
    newPassword: string,
  ): Promise<{ success: boolean; message?: string }> => {
    isLoading.value = true
    try {
      const hashedOldPassword = await hashPassword(oldPassword)
      const hashedNewPassword = await hashPassword(newPassword)

      const response = await authService.changePassword(hashedOldPassword, hashedNewPassword)
      return { success: true, message: response.message || 'Password changed successfully!' }
    } catch (error) {
      console.error('Change password error:', error)
      const apiError = error as ApiError
      return {
        success: false,
        message: apiError.error || apiError.message || 'Failed to change password. Please try again.',
      }
    } finally {
      isLoading.value = false
    }
  }

  // Verify token function
  const verifyToken = async (): Promise<boolean> => {
    try {
      await authService.verifyToken()
      return true
    } catch (error) {
      console.error('Token verification failed:', error)
      logout() // Auto logout if token is invalid
      return false
    }
  }

  // Logout function
  const logout = () => {
    user.value = null
    isAuthenticated.value = false
    localStorage.removeItem('auth_token')
    localStorage.removeItem('username')
  }

  // Initialize auth state from localStorage
  const initAuth = async () => {
    const token = localStorage.getItem('auth_token')
    const username = localStorage.getItem('username')

    if (token && username) {
      user.value = { username, token }
      isAuthenticated.value = true
      
      // Verify token is still valid
      const isValid = await verifyToken()
      if (!isValid) {
        user.value = null
        isAuthenticated.value = false
      }
    }
  }

  // Get authorization header for API calls
  const getAuthHeader = () => {
    return user.value?.token ? `Bearer ${user.value.token}` : null
  }

  return {
    user,
    isAuthenticated,
    isLoading,
    register,
    login,
    logout,
    changePassword,
    verifyToken,
    initAuth,
    getAuthHeader,
  }
})
