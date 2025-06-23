<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Eye, EyeOff, Loader2, Lock, Mail, User, UserPlus } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

const toggleConfirmPasswordVisibility = () => {
  showConfirmPassword.value = !showConfirmPassword.value
}

const validateForm = () => {
  if (!username.value || !email.value || !password.value || !confirmPassword.value) {
    errorMessage.value = t('register.form.validation.allFieldsRequired')
    return false
  }

  if (!email.value.includes('@')) {
    errorMessage.value = t('register.form.validation.validEmail')
    return false
  }

  if (password.value.length < 6) {
    errorMessage.value = t('register.form.validation.passwordLength')
    return false
  }

  if (password.value !== confirmPassword.value) {
    errorMessage.value = t('register.form.validation.passwordMatch')
    return false
  }

  return true
}

const handleRegister = async () => {
  if (!validateForm()) {
    return
  }

  errorMessage.value = ''

  try {
    const result = await authStore.register(username.value, email.value, password.value)

    if (result.success) {
      // Success - redirect to login page
      await router.push({
        path: '/login',
        query: { message: result.message || t('register.form.messages.success') },
      })
    } else {
      errorMessage.value = result.message || t('register.form.messages.registrationFailed')
    }
  } catch (error) {
    errorMessage.value = t('register.form.messages.unexpectedError')
    console.error('Registration error:', error)
  }
}

const handleKeyPress = (event: KeyboardEvent) => {
  if (event.key === 'Enter') {
    handleRegister()
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="min-h-[80vh] flex items-center justify-center bg-background">
    <div class="w-full max-w-md space-y-8">
      <div class="text-center">
        <h1 class="text-3xl font-bold tracking-tight text-foreground">{{ t('register.title') }}</h1>
        <p class="mt-2 text-sm text-muted-foreground">{{ t('register.subtitle') }}</p>
      </div>

      <div class="bg-card border border-border rounded-lg p-6 shadow-sm">
        <form class="space-y-6" @submit.prevent="handleRegister">
          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground" for="username">{{
              t('register.form.fields.username.label') }}</label>
            <div class="relative">
              <User class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input id="username" v-model="username" class="w-full pl-10"
                :placeholder="t('register.form.fields.username.placeholder')" required type="text"
                @onKeyDown="handleKeyPress" />
            </div>
          </div>

          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground" for="email">{{ t('register.form.fields.email.label')
            }}</label>
            <div class="relative">
              <Mail class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input id="email" v-model="email" class="w-full pl-10"
                :placeholder="t('register.form.fields.email.placeholder')" required type="email"
                @onKeyDown="handleKeyPress" />
            </div>
          </div>

          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground" for="password">{{
              t('register.form.fields.password.label') }}</label>
            <div class="relative">
              <Lock class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input id="password" v-model="password" :type="showPassword ? 'text' : 'password'"
                class="w-full pl-10 pr-10" :placeholder="t('register.form.fields.password.placeholder')" required
                @onKeyDown="handleKeyPress" />
              <button
                class="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                type="button" @click="togglePasswordVisibility">
                <Eye v-if="!showPassword" class="h-4 w-4" />
                <EyeOff v-else class="h-4 w-4" />
              </button>
            </div>
          </div>

          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground" for="confirmPassword">{{
              t('register.form.fields.confirmPassword.label') }}</label>
            <div class="relative">
              <Lock class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input id="confirmPassword" v-model="confirmPassword" :type="showConfirmPassword ? 'text' : 'password'"
                class="w-full pl-10 pr-10" :placeholder="t('register.form.fields.confirmPassword.placeholder')" required
                @onKeyDown="handleKeyPress" />
              <button
                class="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                type="button" @click="toggleConfirmPasswordVisibility">
                <Eye v-if="!showConfirmPassword" class="h-4 w-4" />
                <EyeOff v-else class="h-4 w-4" />
              </button>
            </div>
          </div>

          <div v-if="errorMessage"
            class="text-sm text-destructive text-center bg-destructive/10 border border-destructive/20 rounded-md p-3">
            {{ errorMessage }}
          </div>

          <Button :disabled="authStore.isLoading" class="w-full" type="submit">
            <Loader2 v-if="authStore.isLoading" class="mr-2 h-4 w-4 animate-spin" />
            <UserPlus v-else class="mr-2 h-4 w-4" />
            <span v-if="authStore.isLoading">{{ t('register.form.buttons.creatingAccount') }}</span>
            <span v-else>{{ t('register.form.buttons.createAccount') }}</span>
          </Button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-sm text-muted-foreground">
            {{ t('register.login.text') }}
            <button class="font-medium text-primary hover:underline cursor-pointer" type="button" @click="goToLogin">
              {{ t('register.login.link') }}
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
