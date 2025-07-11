<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import {
  AlertTriangle,
  BookOpen,
  Calendar,
  CheckCircle,
  Clock,
  Edit,
  Lock,
  Mail,
  Save,
  User,
  X,
} from 'lucide-vue-next'
import { borrowService, usersService } from '@/lib/api'
import type { Borrow, User as UserType } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

// Data
const user = ref<UserType | null>(null)
const recentBorrows = ref<Borrow[]>([])
const borrowStats = ref({
  totalBorrows: 0,
  activeBorrows: 0,
  overdueBorrows: 0,
  returnedBorrows: 0,
})
const isLoading = ref(false)
const isEditingProfile = ref(false)
const isChangingPassword = ref(false)
const isSaving = ref(false)

// Form data
const profileForm = ref({
  username: '',
  email: '',
})

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})

// Computed
const userInitials = computed(() => {
  if (!user.value) return 'U'
  return user.value.username.charAt(0).toUpperCase()
})

// Methods
const loadUserData = async () => {
  try {
    isLoading.value = true
    user.value = await usersService.getCurrentUser()

    // Initialize form with current user data
    profileForm.value = {
      username: user.value.username,
      email: user.value.email || '',
    }
  } catch (error) {
    console.error('Error loading user data:', error)
    toast.error(t('profile.messages.loadUserDataError'))
  } finally {
    isLoading.value = false
  }
}

const loadBorrowingData = async () => {
  try {
    // Load recent borrows
    const borrowsResponse = await borrowService.getUserBorrows({ page: 0, size: 5 })
    recentBorrows.value = borrowsResponse.content

    // Calculate statistics
    const allBorrows = await borrowService.getUserBorrows({ page: 0, size: 1000 })
    const borrows = allBorrows.content

    borrowStats.value = {
      totalBorrows: borrows.length,
      activeBorrows: borrows.filter((b) => b.status === 'BORROWED').length,
      overdueBorrows: borrows.filter(
        (b) => b.status === 'BORROWED' && new Date(b.returnTime) < new Date(),
      ).length,
      returnedBorrows: borrows.filter((b) => b.status === 'RETURNED').length,
    }
  } catch (error) {
    console.error('Error loading borrowing data:', error)
    toast.error(t('profile.messages.loadBorrowingDataError'))
  }
}

const startEditProfile = () => {
  isEditingProfile.value = true
}

const cancelEditProfile = () => {
  isEditingProfile.value = false
  // Reset form to original values
  if (user.value) {
    profileForm.value = {
      username: user.value.username,
      email: user.value.email || '',
    }
  }
}

const saveProfile = async () => {
  try {
    isSaving.value = true

    await usersService.updateCurrentUser({
      username: profileForm.value.username || undefined,
      email: profileForm.value.email || undefined,
    })

    await loadUserData()
    isEditingProfile.value = false
    toast.success(t('profile.messages.profileUpdateSuccess'))
  } catch (error) {
    console.error('Error updating profile:', error)
    toast.error(t('profile.messages.profileUpdateError'))
  } finally {
    isSaving.value = false
  }
}

const startChangePassword = () => {
  isChangingPassword.value = true
  passwordForm.value = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  }
}

const cancelChangePassword = () => {
  isChangingPassword.value = false
  passwordForm.value = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  }
}

const changePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    toast.error(t('profile.messages.passwordMismatch'))
    return
  }

  if (passwordForm.value.newPassword.length < 6) {
    toast.error(t('profile.messages.passwordTooShort'))
    return
  }

  try {
    isSaving.value = true

    const result = await authStore.changePassword(
      passwordForm.value.currentPassword,
      passwordForm.value.newPassword,
    )

    if (result.success) {
      // On successful password change, immediately log the user out and redirect to login page
      isChangingPassword.value = false
      toast.success(t('profile.messages.passwordChangeSuccess'))

      // Clear authentication state and force re-authentication
      authStore.logout()

      // Redirect to login page with a success message
      // Handle navigation separately to avoid conflating router errors with password change errors
      try {
        await router.push({
          path: '/login',
          query: {
            message: t('profile.messages.passwordChangeSuccess'),
          },
        })
      } catch (navigationError) {
        console.error('Error navigating to login page:', navigationError)
        // Password change was successful, just log the navigation error
        // The user is already logged out, so they'll need to navigate manually
      }
    } else {
      toast.error(result.message || t('profile.messages.passwordChangeError'))
    }
  } catch (error) {
    console.error('Error changing password:', error)
    toast.error(t('profile.messages.passwordChangeError'))
  } finally {
    isSaving.value = false
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

const getBorrowStatusBadge = (borrow: Borrow) => {
  if (borrow.status === 'RETURNED') {
    return { color: 'text-green-600', text: t('profile.status.returned') }
  }

  if (borrow.status === 'BORROWED') {
    const dueDate = new Date(borrow.returnTime)
    const now = new Date()

    if (dueDate < now) {
      return { color: 'text-red-600', text: t('profile.status.overdue') }
    }

    const daysUntilDue = Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
    if (daysUntilDue <= 3) {
      return { color: 'text-orange-600', text: t('profile.status.dueSoon') }
    }

    return { color: 'text-blue-600', text: t('profile.status.active') }
  }

  return { color: 'text-gray-600', text: borrow.status }
}

// Lifecycle
onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  loadUserData()
  loadBorrowingData()
})
</script>

<template>
  <div class="user-profile space-y-6">
    <!-- Header -->
    <div class="flex items-center gap-4">
      <Avatar class="h-20 w-20">
        <AvatarImage alt="User avatar" src="/placeholder-avatar.jpg" />
        <AvatarFallback class="text-lg">{{ userInitials }}</AvatarFallback>
      </Avatar>
      <div>
        <h1 class="text-2xl font-bold">{{ user?.username }}</h1>
        <p class="text-sm text-muted-foreground">
          {{
            user
              ? t('profile.header.memberSince', { date: formatDate(user.createdTime) })
              : t('profile.header.memberSinceLoading')
          }}
        </p>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-8">{{ t('profile.loading.profile') }}</div>

    <template v-else>
      <!-- Profile Information -->
      <Card>
        <CardHeader class="flex flex-row items-center justify-between">
          <div>
            <CardTitle>{{ t('profile.sections.profileInfo.title') }}</CardTitle>
            <CardDescription>{{ t('profile.sections.profileInfo.description') }}</CardDescription>
          </div>
          <Button v-if="!isEditingProfile" variant="outline" @click="startEditProfile">
            <Edit class="h-4 w-4 mr-2" />
            {{ t('profile.buttons.editProfile') }}
          </Button>
        </CardHeader>
        <CardContent class="space-y-4">
          <div v-if="!isEditingProfile" class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label class="text-sm font-medium text-muted-foreground">{{
                t('profile.fields.username.label')
              }}</Label>
              <div class="flex items-center gap-2">
                <User class="h-4 w-4 text-muted-foreground" />
                <span>{{ user?.username || t('profile.placeholders.notAvailable') }}</span>
              </div>
            </div>

            <div class="space-y-2">
              <Label class="text-sm font-medium text-muted-foreground">{{
                t('profile.fields.email.label')
              }}</Label>
              <div class="flex items-center gap-2">
                <Mail class="h-4 w-4 text-muted-foreground" />
                <span>{{ user?.email || t('profile.placeholders.notAvailable') }}</span>
              </div>
            </div>

            <div class="space-y-2">
              <Label class="text-sm font-medium text-muted-foreground">{{
                t('profile.fields.registrationDate.label')
              }}</Label>
              <div class="flex items-center gap-2">
                <Calendar class="h-4 w-4 text-muted-foreground" />
                <span>{{
                  user ? formatDate(user.createdTime) : t('profile.placeholders.notAvailable')
                }}</span>
              </div>
            </div>

            <div class="space-y-2">
              <Label class="text-sm font-medium text-muted-foreground">{{
                t('profile.fields.lastUpdated.label')
              }}</Label>
              <span>{{
                user ? formatDate(user.lastUpdateTime) : t('profile.placeholders.notAvailable')
              }}</span>
            </div>
          </div>

          <!-- Edit Form -->
          <div v-else class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2 md:col-span-2">
                <Label for="username">{{ t('profile.fields.username.label') }}</Label>
                <Input
                  id="username"
                  v-model="profileForm.username"
                  :placeholder="t('profile.fields.username.placeholder')"
                />
              </div>

              <div class="space-y-2 md:col-span-2">
                <Label for="email">{{ t('profile.fields.email.label') }}</Label>
                <Input
                  id="email"
                  v-model="profileForm.email"
                  :placeholder="t('profile.fields.email.placeholder')"
                  type="email"
                />
              </div>
            </div>

            <div class="flex gap-2">
              <Button :disabled="isSaving" @click="saveProfile">
                <Save class="h-4 w-4 mr-2" />
                {{ isSaving ? t('profile.buttons.saving') : t('profile.buttons.saveChanges') }}
              </Button>
              <Button variant="outline" @click="cancelEditProfile">
                <X class="h-4 w-4 mr-2" />
                {{ t('profile.buttons.cancel') }}
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Password Change -->
      <Card>
        <CardHeader class="flex flex-row items-center justify-between">
          <div>
            <CardTitle>{{ t('profile.sections.security.title') }}</CardTitle>
            <CardDescription>{{ t('profile.sections.security.description') }}</CardDescription>
          </div>
          <Button v-if="!isChangingPassword" variant="outline" @click="startChangePassword">
            <Lock class="h-4 w-4 mr-2" />
            {{ t('profile.buttons.changePassword') }}
          </Button>
        </CardHeader>
        <CardContent>
          <div v-if="!isChangingPassword" class="text-sm text-muted-foreground">
            {{
              user
                ? t('profile.security.lastPasswordUpdate', {
                    date: formatDate(user.lastUpdateTime),
                  })
                : t('profile.security.lastPasswordUpdateDefault')
            }}
          </div>

          <!-- Change Password Form -->
          <div v-else class="space-y-4">
            <div class="space-y-2">
              <Label for="currentPassword">{{ t('profile.fields.currentPassword.label') }}</Label>
              <Input
                id="currentPassword"
                v-model="passwordForm.currentPassword"
                :placeholder="t('profile.fields.currentPassword.placeholder')"
                type="password"
              />
            </div>

            <div class="space-y-2">
              <Label for="newPassword">{{ t('profile.fields.newPassword.label') }}</Label>
              <Input
                id="newPassword"
                v-model="passwordForm.newPassword"
                :placeholder="t('profile.fields.newPassword.placeholder')"
                type="password"
              />
            </div>

            <div class="space-y-2">
              <Label for="confirmPassword">{{ t('profile.fields.confirmPassword.label') }}</Label>
              <Input
                id="confirmPassword"
                v-model="passwordForm.confirmPassword"
                :placeholder="t('profile.fields.confirmPassword.placeholder')"
                type="password"
              />
            </div>

            <div class="flex gap-2">
              <Button :disabled="isSaving" @click="changePassword">
                <Save class="h-4 w-4 mr-2" />
                {{
                  isSaving
                    ? t('profile.buttons.changingPassword')
                    : t('profile.buttons.changePassword')
                }}
              </Button>
              <Button variant="outline" @click="cancelChangePassword">
                <X class="h-4 w-4 mr-2" />
                {{ t('profile.buttons.cancel') }}
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Borrowing Statistics -->
      <Card>
        <CardHeader>
          <CardTitle>{{ t('profile.sections.borrowingStats.title') }}</CardTitle>
          <CardDescription>{{ t('profile.sections.borrowingStats.description') }}</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div class="text-center">
              <div class="text-2xl font-bold text-blue-600">{{ borrowStats.totalBorrows }}</div>
              <div class="text-sm text-muted-foreground">{{ t('profile.stats.totalBorrows') }}</div>
            </div>

            <div class="text-center">
              <div class="text-2xl font-bold text-green-600">{{ borrowStats.returnedBorrows }}</div>
              <div class="text-sm text-muted-foreground">{{ t('profile.stats.returned') }}</div>
            </div>

            <div class="text-center">
              <div class="text-2xl font-bold text-orange-600">{{ borrowStats.activeBorrows }}</div>
              <div class="text-sm text-muted-foreground">{{ t('profile.stats.active') }}</div>
            </div>

            <div class="text-center">
              <div class="text-2xl font-bold text-red-600">{{ borrowStats.overdueBorrows }}</div>
              <div class="text-sm text-muted-foreground">{{ t('profile.stats.overdue') }}</div>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Recent Borrowing Activity -->
      <Card>
        <CardHeader class="flex flex-row items-center justify-between">
          <div>
            <CardTitle>{{ t('profile.sections.recentActivity.title') }}</CardTitle>
            <CardDescription>{{
              t('profile.sections.recentActivity.description')
            }}</CardDescription>
          </div>
          <Button variant="outline" @click="router.push('/borrows')">{{
            t('profile.buttons.viewAllBorrows')
          }}</Button>
        </CardHeader>
        <CardContent>
          <div v-if="recentBorrows.length === 0" class="text-center py-8 text-muted-foreground">
            {{ t('profile.activity.noActivity') }}
          </div>

          <div v-else class="space-y-4">
            <div
              v-for="borrow in recentBorrows"
              :key="borrow.borrowId"
              class="flex items-center justify-between p-3 border rounded-lg hover:bg-muted/50 transition-colors"
            >
              <div class="flex items-center gap-3">
                <div
                  class="w-10 h-12 bg-muted rounded flex items-center justify-center overflow-hidden"
                >
                  <img
                    v-if="borrow.coverURL"
                    :src="borrow.coverURL"
                    :alt="`Cover for ${borrow.bookTitle}`"
                    class="w-full h-full object-cover"
                  />
                  <BookOpen v-else class="h-5 w-5 text-muted-foreground" />
                </div>

                <div>
                  <h4 class="font-medium">{{ borrow.bookTitle }}</h4>
                  <p class="text-sm text-muted-foreground">
                    {{ t('profile.activity.due', { date: formatDate(borrow.returnTime) }) }}
                  </p>
                </div>
              </div>

              <div class="flex items-center gap-2">
                <span :class="getBorrowStatusBadge(borrow).color" class="text-sm font-medium">
                  {{ getBorrowStatusBadge(borrow).text }}
                </span>
                <CheckCircle v-if="borrow.status === 'RETURNED'" class="h-4 w-4 text-green-600" />
                <Clock
                  v-else-if="!getBorrowStatusBadge(borrow).color.includes('red')"
                  class="h-4 w-4 text-blue-600"
                />
                <AlertTriangle v-else class="h-4 w-4 text-red-600" />
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </template>
  </div>
</template>

<style scoped>
.user-profile {
  max-width: 800px;
  margin: 0 auto;
}
</style>
