import { ref } from 'vue'
import { defineStore } from 'pinia'

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

      const response = await fetch('http://localhost:8090/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username,
          email,
          password: hashedPassword,
        }),
      })

      if (!response.ok) {
        // Handle error response
        let errorText = 'Registration failed. Please try again.'
        try {
          const errorData = await response.text()
          if (errorData) {
            errorText = errorData
          }
        } catch (e) {
          // If we can't parse the error, use default message
        }
        throw new Error(errorText)
      }

      return { success: true, message: 'Registration successful! Please log in.' }
    } catch (error) {
      console.error('Registration error:', error)
      return {
        success: false,
        message: error instanceof Error ? error.message : 'Registration failed. Please try again.',
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

      const response = await fetch('http://localhost:8090/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username,
          password: hashedPassword,
        }),
      })

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ message: 'Login failed' }))
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`)
      }

      const token = await response.text()

      user.value = {
        username,
        token: token,
      }

      isAuthenticated.value = true

      localStorage.setItem('auth_token', token)
      localStorage.setItem('username', username)

      return { success: true }
    } catch (error) {
      console.error('Login error:', error)
      return {
        success: false,
        message: error instanceof Error ? error.message : 'Login failed. Please try again.',
      }
    } finally {
      isLoading.value = false
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
  const initAuth = () => {
    const token = localStorage.getItem('auth_token')
    const username = localStorage.getItem('username')

    if (token && username) {
      user.value = { username, token }
      isAuthenticated.value = true
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
    initAuth,
    getAuthHeader,
  }
})
