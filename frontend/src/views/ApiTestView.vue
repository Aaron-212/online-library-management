<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { booksService, authService } from '@/lib/api'
import { useAuthStore } from '@/stores/auth'
import { toast } from 'vue-sonner'

const authStore = useAuthStore()

// State
const isTestingConnection = ref(false)
const connectionResult = ref<{
  status: 'success' | 'error' | 'pending'
  message: string
  details?: any
} | null>(null)

const booksData = ref<any>(null)
const isFetchingBooks = ref(false)

// Test connection to API
const testConnection = async () => {
  isTestingConnection.value = true
  connectionResult.value = { status: 'pending', message: 'Testing connection...' }
  
  try {
    // Test a simple endpoint that doesn't require auth
    const response = await booksService.getAll({ page: 0, size: 5 })
    
    connectionResult.value = {
      status: 'success',
      message: 'API connection successful!',
      details: {
        totalBooks: response.totalElements,
        booksOnPage: response.content.length,
        baseUrl: import.meta.env.VITE_API_BASE_URL
      }
    }
    
    toast.success('API connection test passed!')
  } catch (error: any) {
    connectionResult.value = {
      status: 'error',
      message: 'API connection failed',
      details: {
        error: error.message || 'Unknown error',
        status: error.status,
        baseUrl: import.meta.env.VITE_API_BASE_URL
      }
    }
    
    toast.error('API connection test failed')
  } finally {
    isTestingConnection.value = false
  }
}

// Fetch sample books
const fetchSampleBooks = async () => {
  isFetchingBooks.value = true
  try {
    const response = await booksService.getAll({ page: 0, size: 10 })
    booksData.value = response
    toast.success('Books fetched successfully!')
  } catch (error: any) {
    toast.error('Failed to fetch books: ' + (error.message || 'Unknown error'))
  } finally {
    isFetchingBooks.value = false
  }
}

// Test authentication (if logged in)
const testAuth = async () => {
  if (!authStore.isAuthenticated) {
    toast.error('Please log in first to test authentication')
    return
  }
  
  try {
    const response = await authService.verifyToken()
    toast.success('Authentication test passed!')
  } catch (error: any) {
    toast.error('Authentication test failed: ' + (error.message || 'Unknown error'))
  }
}

onMounted(() => {
  // Auto-test connection on mount
  testConnection()
})
</script>

<template>
  <div class="api-example space-y-6">
    <div>
      <h1 class="text-2xl font-bold">API Integration Test</h1>
      <p class="text-muted-foreground">Test the connection between frontend and backend API</p>
    </div>

    <!-- Connection Status -->
    <Card>
      <CardHeader>
        <CardTitle>Connection Status</CardTitle>
        <CardDescription>Test the basic API connectivity</CardDescription>
      </CardHeader>
      <CardContent class="space-y-4">
        <div class="flex gap-2">
          <Button @click="testConnection" :disabled="isTestingConnection">
            {{ isTestingConnection ? 'Testing...' : 'Test Connection' }}
          </Button>
          <Button @click="testAuth" variant="outline" :disabled="!authStore.isAuthenticated">
            Test Auth
          </Button>
        </div>

        <div v-if="connectionResult" class="space-y-2">
          <div class="flex items-center gap-2">
            <Badge 
              :variant="connectionResult.status === 'success' ? 'default' : 
                       connectionResult.status === 'error' ? 'destructive' : 'secondary'"
            >
              {{ connectionResult.status }}
            </Badge>
            <span>{{ connectionResult.message }}</span>
          </div>
          
          <div v-if="connectionResult.details" class="text-sm text-muted-foreground">
            <pre class="bg-muted p-2 rounded text-xs overflow-auto">{{ JSON.stringify(connectionResult.details, null, 2) }}</pre>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Sample Data -->
    <Card>
      <CardHeader>
        <CardTitle>Sample Books Data</CardTitle>
        <CardDescription>Fetch and display sample books from the API</CardDescription>
      </CardHeader>
      <CardContent class="space-y-4">
        <Button @click="fetchSampleBooks" :disabled="isFetchingBooks">
          {{ isFetchingBooks ? 'Fetching...' : 'Fetch Sample Books' }}
        </Button>

        <div v-if="isFetchingBooks" class="space-y-2">
          <Skeleton class="h-4 w-full" />
          <Skeleton class="h-4 w-3/4" />
          <Skeleton class="h-4 w-1/2" />
        </div>

        <div v-else-if="booksData" class="space-y-2">
          <div class="text-sm">
            <strong>Total Books:</strong> {{ booksData.totalElements }}
          </div>
          <div class="text-sm">
            <strong>Books on this page:</strong> {{ booksData.content.length }}
          </div>
          
          <div class="space-y-2">
            <h4 class="font-medium">Sample Books:</h4>
            <div class="grid gap-2">
              <div 
                v-for="book in booksData.content.slice(0, 5)" 
                :key="book.id"
                class="p-2 border rounded text-sm"
              >
                <div class="font-medium">{{ book.title }}</div>
                <div class="text-muted-foreground">
                  ISBN: {{ book.isbn }} | 
                  Available: {{ book.availableQuantity }}/{{ book.totalQuantity }}
                </div>
                <div class="text-xs text-muted-foreground">
                  Authors: {{ book.authors?.map((a: any) => a.name).join(', ') || 'N/A' }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- User Info -->
    <Card v-if="authStore.isAuthenticated">
      <CardHeader>
        <CardTitle>User Information</CardTitle>
        <CardDescription>Current authenticated user details</CardDescription>
      </CardHeader>
      <CardContent>
        <div class="space-y-2">
          <div class="text-sm">
            <strong>Username:</strong> {{ authStore.user?.username }}
          </div>
          <div class="text-sm">
            <strong>Email:</strong> {{ authStore.user?.email }}
          </div>
          <div class="text-sm">
            <strong>Role:</strong> 
            <Badge :variant="authStore.user?.role === 'ADMIN' ? 'destructive' : 'default'">
              {{ authStore.user?.role }}
            </Badge>
          </div>
          <div class="text-sm">
            <strong>Is Admin:</strong> {{ authStore.isAdmin() ? 'Yes' : 'No' }}
          </div>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<style scoped>
.api-example {
  max-width: 800px;
  margin: 0 auto;
}
</style>