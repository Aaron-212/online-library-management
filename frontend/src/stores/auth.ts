import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { UserLoginDto, UserRegisterDto } from '@/lib/api'
import { authService, usersService } from '@/lib/api'
import type { ApiError } from '@/lib/api/client'

interface AuthUser {
  id: number
  username: string
  token: string
  role: 'USER' | 'ADMIN'
  email: string
  createdTime: string
  lastUpdateTime: string
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<AuthUser | null>(null)
  const isAuthenticated = ref(false)
  const isLoading = ref(false)
  const isInitialized = ref(false)

  // Computed properties for role checking
  const isAdmin = () => user.value?.role === 'ADMIN'
  const isUser = () => user.value?.role === 'USER'

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
      return {
        success: true,
        message: response.message || 'Registration successful! Please log in.',
      }
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
    usernameOrEmail: string,
    password: string,
  ): Promise<{ success: boolean; message?: string }> => {
    isLoading.value = true
    try {
      const hashedPassword = await hashPassword(password)

      const loginData: UserLoginDto = {
        usernameOrEmail,
        password: hashedPassword,
      }

      const response = await authService.login(loginData)

      // Handle different response formats from backend
      let token: string
      let message: string

      if (typeof response === 'string') {
        // Backend returned plain text token
        token = response
        message = 'Login successful!'
      } else if (response && typeof response === 'object') {
        // Backend returned JSON object
        if ('token' in response && typeof response.token === 'string') {
          token = response.token
          message =
            'message' in response && typeof response.message === 'string'
              ? response.message
              : 'Login successful!'
        } else {
          // No valid token found in response object
          throw new Error('No valid token found in server response')
        }
      } else {
        throw new Error('Invalid response format from server')
      }

      // Get the actual user data to retrieve correct username
      // Temporarily set the token for the API call
      localStorage.setItem('auth_token', token)

      try {
        const userData = await usersService.getCurrentUser()

        user.value = {
          id: userData.id,
          username: userData.username, // Use actual username from user data
          token: token,
          role: userData.role,
          email: userData.email,
          createdTime: userData.createdTime,
          lastUpdateTime: userData.lastUpdateTime,
        }

        isAuthenticated.value = true
        localStorage.setItem('username', userData.username)

        return { success: true, message }
      } catch (userError) {
        // If getting user data fails, clean up and throw error
        localStorage.removeItem('auth_token')
        throw userError
      }
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
        message:
          apiError.error || apiError.message || 'Failed to change password. Please try again.',
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
    isInitialized.value = false
    localStorage.removeItem('auth_token')
    localStorage.removeItem('username')
  }

  // Initialize auth state from localStorage
  const initAuth = async () => {
    const token = localStorage.getItem('auth_token')
    const username = localStorage.getItem('username')

    if (token && username) {
      // Try to get full user data from API
      try {
        const userData = await usersService.getCurrentUser()
        user.value = {
          id: userData.id,
          username: userData.username,
          token: token,
          role: userData.role,
          email: userData.email,
          createdTime: userData.createdTime,
          lastUpdateTime: userData.lastUpdateTime,
        }
        isAuthenticated.value = true

        // Verify token is still valid
        const isValid = await verifyToken()
        if (!isValid) {
          user.value = null
          isAuthenticated.value = false
        }
      } catch (error) {
        // If we can't get user data, clear auth state
        console.error('Failed to get user data during init:', error)
        logout()
      }
    }

    isInitialized.value = true
  }

  // Get authorization header for API calls
  const getAuthHeader = () => {
    // First check if we have the token in the user object
    if (user.value?.token) {
      return `Bearer ${user.value.token}`
    }

    // Fallback to localStorage (useful during login process when user.value is not yet set)
    const token = localStorage.getItem('auth_token')
    return token ? `Bearer ${token}` : null
  }

  return {
    user,
    isAuthenticated,
    isLoading,
    isInitialized,
    register,
    login,
    logout,
    changePassword,
    verifyToken,
    initAuth,
    getAuthHeader,
    isAdmin,
    isUser,
  }
})
