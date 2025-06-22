import { useAuthStore } from '@/stores/auth'

// Base configuration - use environment variable or fallback
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8090/api/v1'

// API Response types
export interface ApiResponse<T = any> {
  data: T
  status: number
  message?: string
}

export interface ApiError {
  error: string
  status: number
  message?: string
}

// Base API client class
class ApiClient {
  private baseUrl: string

  constructor(baseUrl: string = API_BASE_URL) {
    this.baseUrl = baseUrl
  }

  /**
   * Build the absolute URL for an API request. Handles leading/trailing slashes gracefully
   * so that service classes can pass either `/books` or `books` and still get a valid
   * URL like `http://host/api/v1/books`.
   */
  private buildUrl(endpoint: string): string {
    // If the caller already supplied a full URL, use it verbatim
    if (/^https?:\/\//i.test(endpoint)) {
      return endpoint
    }

    const sanitizedBase = this.baseUrl.replace(/\/$/, '') // remove trailing slash
    const sanitizedEndpoint = endpoint.replace(/^\//, '') // remove leading slash

    return `${sanitizedBase}/${sanitizedEndpoint}`
  }

  private async makeRequest<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const authStore = useAuthStore()

    // Default headers
    const defaultHeaders: HeadersInit = {
      'Content-Type': 'application/json',
    }

    // Add auth header if available
    const authHeader = authStore.getAuthHeader()
    if (authHeader) {
      defaultHeaders.Authorization = authHeader
    }

    // Merge headers
    const headers = {
      ...defaultHeaders,
      ...options.headers,
    }

    const url = this.buildUrl(endpoint)

    // Make the request
    const response = await fetch(url, {
      ...options,
      headers,
    })

    // Handle response
    if (!response.ok) {
      let errorMessage = `HTTP error! status: ${response.status}`

      try {
        const errorData = await response.json()
        // Prefer the detailed server-provided message if available, otherwise fall back to the generic error field
        errorMessage = errorData.message || errorData.error || errorMessage
      } catch {
        // If we can't parse JSON, use the response text
        try {
          errorMessage = (await response.text()) || errorMessage
        } catch {
          // Use default message if all else fails
        }
      }

      const error: ApiError = {
        error: errorMessage,
        status: response.status,
        message: errorMessage,
      }

      throw error
    }

    // Handle different response types
    const contentType = response.headers.get('content-type')

    if (contentType?.includes('application/json')) {
      return await response.json()
    } else if (response.status === 204) {
      // No content
      return {} as T
    } else {
      // Text response (like token strings)
      return (await response.text()) as unknown as T
    }
  }

  // HTTP methods
  async get<T>(endpoint: string, params?: Record<string, any>): Promise<T> {
    const url = new URL(this.buildUrl(endpoint))

    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          url.searchParams.append(key, String(value))
        }
      })
    }

    return this.makeRequest<T>(url.toString())
  }

  async post<T>(endpoint: string, data?: any): Promise<T> {
    return this.makeRequest<T>(endpoint, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined,
    })
  }

  async put<T>(endpoint: string, data?: any): Promise<T> {
    return this.makeRequest<T>(endpoint, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined,
    })
  }

  async patch<T>(endpoint: string, data?: any): Promise<T> {
    return this.makeRequest<T>(endpoint, {
      method: 'PATCH',
      body: data ? JSON.stringify(data) : undefined,
    })
  }

  async delete<T>(endpoint: string): Promise<T> {
    return this.makeRequest<T>(endpoint, {
      method: 'DELETE',
    })
  }
}

// Export singleton instance
export const apiClient = new ApiClient()
export default apiClient
