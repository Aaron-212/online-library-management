<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Eye, EyeOff, Loader2, Lock, User } from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const successMessage = ref('')
const showPassword = ref(false)

// Check for success message from registration
onMounted(() => {
  if (route.query.message) {
    successMessage.value = route.query.message as string
    // Clear the query parameter
    router.replace({ path: '/login' })
  }
})

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMessage.value = 'Please fill in all fields'
    return
  }

  errorMessage.value = ''

  try {
    const result = await authStore.login(username.value, password.value)

    if (result.success) {
      // Redirect to dashboard on successful login
      router.push('/dashboard')
    } else {
      errorMessage.value = result.message || 'Login failed. Please try again.'
    }
  } catch (error) {
    errorMessage.value = 'An unexpected error occurred. Please try again.'
    console.error('Login error:', error)
  }
}

const handleKeyPress = (event: KeyboardEvent) => {
  if (event.key === 'Enter') {
    handleLogin()
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-background">
    <div class="w-full max-w-md space-y-8">
      <div class="text-center">
        <h1 class="text-3xl font-bold tracking-tight text-foreground">Sign in to your account</h1>
        <p class="mt-2 text-sm text-muted-foreground">Welcome back to Library Management System</p>
      </div>

      <div class="bg-card border border-border rounded-lg p-6 shadow-sm">
        <form class="space-y-6" @submit.prevent="handleLogin">
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground" for="email"> Username </label>
            <div class="relative">
              <User
                class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
              />
              <Input
                id="username"
                v-model="username"
                class="w-full pl-10"
                placeholder="Enter your username"
                required
                type="text"
                @keypress="handleKeyPress"
              />
            </div>
          </div>

          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground" for="password"> Password </label>
            <div class="relative">
              <Lock
                class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
              />
              <Input
                id="password"
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                class="w-full pl-10 pr-10"
                placeholder="Enter your password"
                required
                @keypress="handleKeyPress"
              />
              <button
                class="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                type="button"
                @click="togglePasswordVisibility"
              >
                <Eye v-if="!showPassword" class="h-4 w-4" />
                <EyeOff v-else class="h-4 w-4" />
              </button>
            </div>
          </div>

          <div
            v-if="successMessage"
            class="text-sm text-green-700 text-center bg-green-50 border border-green-200 rounded-md p-3"
          >
            {{ successMessage }}
          </div>

          <div
            v-if="errorMessage"
            class="text-sm text-destructive text-center bg-destructive/10 border border-destructive/20 rounded-md p-3"
          >
            {{ errorMessage }}
          </div>

          <Button :disabled="authStore.isLoading" class="w-full" type="submit">
            <Loader2 v-if="authStore.isLoading" class="mr-2 h-4 w-4 animate-spin" />
            <span v-if="authStore.isLoading">Signing in...</span>
            <span v-else>Sign in</span>
          </Button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-sm text-muted-foreground">
            Don't have an account?
            <button
              class="font-medium text-primary hover:underline cursor-pointer"
              type="button"
              @click="router.push('/register')"
            >
              Sign up here
            </button>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Additional custom styles can be added here if needed */
</style>
