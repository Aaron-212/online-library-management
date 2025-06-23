<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { toast } from 'vue-sonner'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Skeleton } from '@/components/ui/skeleton'
import {
  ChevronLeft,
  ChevronRight,
  Edit,
  Loader2,
  Plus,
  Search,
  Shield,
  ShieldCheck,
  Trash2,
  User,
  Users,
} from 'lucide-vue-next'
import { usersService } from '@/lib/api'
import type { UserPublic, UserAdmin, UserCreateDto, UserUpdateDto } from '@/lib/api/types'
import { useAuthStore } from '@/stores/auth'

const { t } = useI18n()
const router = useRouter()
const authStore = useAuthStore()

// Data
const users = ref<UserAdmin[]>([])
const isLoading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(10)
const searchQuery = ref('')

// Dialog states
const isCreateDialogOpen = ref(false)
const isEditDialogOpen = ref(false)
const isDeleteDialogOpen = ref(false)
const isRoleDialogOpen = ref(false)

// Form states
const isSubmitting = ref(false)
const selectedUser = ref<UserAdmin | null>(null)

// Create form
const createForm = ref<UserCreateDto>({
  username: '',
  email: '',
  password: '',
  role: 'USER',
})

// Edit form
const editForm = ref<UserUpdateDto>({
  username: '',
  email: '',
})

// Role form
const newRole = ref<'USER' | 'ADMIN'>('USER')

// Computed
const hasNextPage = computed(() => currentPage.value < totalPages.value - 1)
const hasPrevPage = computed(() => currentPage.value > 0)

const isFormValid = computed(() => {
  return (
    createForm.value.username.trim() &&
    createForm.value.email.trim() &&
    createForm.value.password.trim()
  )
})

const isEditFormValid = computed(() => {
  return editForm.value.username?.trim() && editForm.value.email?.trim()
})

// Methods
const loadUsers = async () => {
  try {
    isLoading.value = true
    const response = await usersService.getAllUsers({
      page: currentPage.value,
      size: pageSize.value,
      search: searchQuery.value || undefined,
    })

    users.value = response.content
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
  } catch (error) {
    console.error('Error loading users:', error)
    toast.error(t('userManagement.messages.error.loadFailed'))
  } finally {
    isLoading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 0
  loadUsers()
}

const nextPage = () => {
  if (hasNextPage.value) {
    currentPage.value++
    loadUsers()
  }
}

const prevPage = () => {
  if (hasPrevPage.value) {
    currentPage.value--
    loadUsers()
  }
}

const openCreateDialog = () => {
  createForm.value = {
    username: '',
    email: '',
    password: '',
    role: 'USER',
  }
  isCreateDialogOpen.value = true
}

const openEditDialog = (user: UserAdmin) => {
  selectedUser.value = user
  editForm.value = {
    username: user.username,
    email: user.email || '',
  }
  isEditDialogOpen.value = true
}

const openRoleDialog = (user: UserAdmin) => {
  selectedUser.value = user
  newRole.value = user.role || 'USER'
  isRoleDialogOpen.value = true
}

const openDeleteDialog = (user: UserAdmin) => {
  selectedUser.value = user
  isDeleteDialogOpen.value = true
}

const handleCreateUser = async () => {
  if (!isFormValid.value) {
    toast.error(t('userManagement.messages.validation.requiredFields'))
    return
  }

  try {
    isSubmitting.value = true
    await usersService.createUser(createForm.value)
    toast.success(t('userManagement.messages.success.userCreated'))
    isCreateDialogOpen.value = false
    await loadUsers()
  } catch (error: any) {
    console.error('Error creating user:', error)
    toast.error(error.error || t('userManagement.messages.error.createFailed'))
  } finally {
    isSubmitting.value = false
  }
}

const handleEditUser = async () => {
  if (!isEditFormValid.value || !selectedUser.value) {
    toast.error(t('userManagement.messages.validation.requiredFields'))
    return
  }

  try {
    isSubmitting.value = true
    await usersService.updateUser(selectedUser.value.id, editForm.value)
    toast.success(t('userManagement.messages.success.userUpdated'))
    isEditDialogOpen.value = false
    await loadUsers()
  } catch (error: any) {
    console.error('Error updating user:', error)
    toast.error(error.error || t('userManagement.messages.error.updateFailed'))
  } finally {
    isSubmitting.value = false
  }
}

const handleUpdateRole = async () => {
  if (!selectedUser.value) return

  try {
    isSubmitting.value = true
    await usersService.updateUserRole(selectedUser.value.id, newRole.value)
    toast.success(t('userManagement.messages.success.roleUpdated'))
    isRoleDialogOpen.value = false
    await loadUsers()
  } catch (error: any) {
    console.error('Error updating user role:', error)
    toast.error(error.error || t('userManagement.messages.error.roleUpdateFailed'))
  } finally {
    isSubmitting.value = false
  }
}

const handleDeleteUser = async () => {
  if (!selectedUser.value) return

  try {
    isSubmitting.value = true
    await usersService.deleteUser(selectedUser.value.id)
    toast.success(t('userManagement.messages.success.userDeleted'))
    isDeleteDialogOpen.value = false
    await loadUsers()
  } catch (error: any) {
    console.error('Error deleting user:', error)
    toast.error(error.error || t('userManagement.messages.error.deleteFailed'))
  } finally {
    isSubmitting.value = false
  }
}

const getRoleBadge = (role: string) => {
  switch (role) {
    case 'ADMIN':
      return { variant: 'destructive' as const, icon: ShieldCheck }
    case 'USER':
    default:
      return { variant: 'secondary' as const, icon: User }
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

// Watch for search changes
watch(searchQuery, () => {
  const timeoutId = setTimeout(() => {
    handleSearch()
  }, 500)

  return () => clearTimeout(timeoutId)
})

// Lifecycle
onMounted(() => {
  loadUsers()
})
</script>

<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold">{{ t('userManagement.title') }}</h1>
        <p class="text-muted-foreground">{{ t('userManagement.description') }}</p>
      </div>
      <Button @click="openCreateDialog" class="gap-2">
        <Plus class="h-4 w-4" />
        {{ t('userManagement.addUser') }}
      </Button>
    </div>

    <!-- Search and Controls -->
    <Card>
      <CardHeader>
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="relative">
              <Search
                class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
              />
              <Input
                v-model="searchQuery"
                :placeholder="t('userManagement.search.placeholder')"
                class="pl-10 w-64"
              />
            </div>
          </div>
          <div class="flex items-center gap-2 text-sm text-muted-foreground">
            <Users class="h-4 w-4" />
            {{ totalElements }} {{ t('userManagement.totalUsers') }}
          </div>
        </div>
      </CardHeader>
    </Card>

    <!-- Users Table -->
    <div class="border rounded-lg">
      <!-- Table Header -->
      <div class="grid grid-cols-6 gap-4 p-4 border-b bg-muted/50 font-medium">
        <div>{{ t('userManagement.user') }}</div>
        <div>{{ t('userManagement.email') }}</div>
        <div>{{ t('userManagement.role') }}</div>
        <div>{{ t('userManagement.created') }}</div>
        <div>{{ t('userManagement.lastUpdated') }}</div>
        <div class="text-right">{{ t('userManagement.actions') }}</div>
      </div>

      <!-- Table Body -->
      <div v-if="isLoading" class="space-y-2 p-4">
        <div v-for="i in pageSize" :key="i" class="grid grid-cols-6 gap-4 items-center">
          <Skeleton class="h-4 w-full" />
          <Skeleton class="h-4 w-full" />
          <Skeleton class="h-4 w-16" />
          <Skeleton class="h-4 w-20" />
          <Skeleton class="h-4 w-20" />
          <Skeleton class="h-4 w-20" />
        </div>
      </div>

      <div v-else-if="users.length === 0" class="p-8 text-center text-muted-foreground">
        <Users class="h-12 w-12 mx-auto mb-4 opacity-50" />
        <p class="text-lg font-medium mb-2">{{ t('userManagement.noUsersFound') }}</p>
        <p>
          {{
            searchQuery
              ? t('userManagement.empty.searchDescription')
              : t('userManagement.empty.defaultDescription')
          }}
        </p>
      </div>

      <div v-else>
        <div
          v-for="user in users"
          :key="user.id"
          class="grid grid-cols-6 gap-4 p-4 border-b last:border-b-0 hover:bg-muted/50 items-center"
        >
          <!-- User Info -->
          <div class="flex items-center gap-3">
            <div class="h-8 w-8 rounded-full bg-primary/10 flex items-center justify-center">
              <User class="h-4 w-4" />
            </div>
            <div>
              <div class="font-medium">{{ user.username }}</div>
              <div class="text-sm text-muted-foreground">ID: {{ user.id }}</div>
            </div>
          </div>

          <!-- Email -->
          <div class="text-sm">
            {{ user.email || t('userManagement.table.noEmail') }}
          </div>

          <!-- Role -->
          <div>
            <Badge :variant="getRoleBadge(user.role || 'USER').variant" class="gap-1">
              <component :is="getRoleBadge(user.role || 'USER').icon" class="h-3 w-3" />
              {{ user.role || 'USER' }}
            </Badge>
          </div>

          <!-- Created Date -->
          <div class="text-sm text-muted-foreground">
            {{ formatDate(user.createdTime) }}
          </div>

          <!-- Last Updated -->
          <div class="text-sm text-muted-foreground">
            {{ formatDate(user.lastUpdateTime || user.createdTime) }}
          </div>

          <!-- Actions -->
          <div class="flex items-center justify-end gap-2">
            <Button variant="ghost" size="sm" @click="openEditDialog(user)">
              <Edit class="h-4 w-4" />
            </Button>
            <Button variant="ghost" size="sm" @click="openRoleDialog(user)">
              <Shield class="h-4 w-4" />
            </Button>
            <Button variant="ghost" size="sm" @click="openDeleteDialog(user)">
              <Trash2 class="h-4 w-4" />
            </Button>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="flex items-center justify-between p-4 border-t">
        <div class="text-sm text-muted-foreground">
          {{ t('userManagement.showing') }} {{ currentPage * pageSize + 1 }}
          {{ t('userManagement.to') }}
          {{ Math.min((currentPage + 1) * pageSize, totalElements) }}
          {{ t('userManagement.of') }} {{ totalElements }}
          {{ t('userManagement.users') }}
        </div>
        <div class="flex items-center gap-2">
          <Button variant="outline" size="sm" @click="prevPage" :disabled="!hasPrevPage">
            <ChevronLeft class="h-4 w-4" />
            {{ t('userManagement.previous') }}
          </Button>
          <div class="flex items-center gap-1">
            <span class="text-sm"
              >{{ t('userManagement.page') }} {{ currentPage + 1 }} {{ t('userManagement.of') }}
              {{ totalPages }}</span
            >
          </div>
          <Button variant="outline" size="sm" @click="nextPage" :disabled="!hasNextPage">
            {{ t('userManagement.next') }}
            <ChevronRight class="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>

    <!-- Create User Dialog -->
    <Dialog v-model:open="isCreateDialogOpen">
      <DialogContent class="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>{{ t('userManagement.createNewUser') }}</DialogTitle>
          <DialogDescription> {{ t('userManagement.addNewUser') }} </DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-4">
          <div class="grid gap-2">
            <Label for="create-username">{{ t('userManagement.username') }}</Label>
            <Input
              id="create-username"
              v-model="createForm.username"
              :placeholder="t('userManagement.dialogs.create.fields.username.placeholder')"
            />
          </div>
          <div class="grid gap-2">
            <Label for="create-email">{{ t('userManagement.email') }}</Label>
            <Input
              id="create-email"
              v-model="createForm.email"
              type="email"
              :placeholder="t('userManagement.dialogs.create.fields.email.placeholder')"
            />
          </div>
          <div class="grid gap-2">
            <Label for="create-password">{{ t('userManagement.password') }}</Label>
            <Input
              id="create-password"
              v-model="createForm.password"
              type="password"
              :placeholder="t('userManagement.dialogs.create.fields.password.placeholder')"
            />
          </div>
          <div class="grid gap-2">
            <Label for="create-role">{{ t('userManagement.role') }}</Label>
            <Select v-model="createForm.role">
              <SelectTrigger>
                <SelectValue
                  :placeholder="t('userManagement.dialogs.create.fields.role.placeholder')"
                />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="USER">{{ t('userManagement.roles.user') }}</SelectItem>
                <SelectItem value="ADMIN">{{ t('userManagement.roles.admin') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="isCreateDialogOpen = false">{{
            t('userManagement.dialogs.create.buttons.cancel')
          }}</Button>
          <Button @click="handleCreateUser" :disabled="!isFormValid || isSubmitting">
            <Loader2 v-if="isSubmitting" class="h-4 w-4 mr-2 animate-spin" />
            {{ t('userManagement.createUser') }}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Edit User Dialog -->
    <Dialog v-model:open="isEditDialogOpen">
      <DialogContent class="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>{{ t('userManagement.editUser') }}</DialogTitle>
          <DialogDescription>
            {{ t('userManagement.updateUserInfo') }} {{ selectedUser?.username }}.
          </DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-4">
          <div class="grid gap-2">
            <Label for="edit-username">{{ t('userManagement.username') }}</Label>
            <Input
              id="edit-username"
              v-model="editForm.username"
              :placeholder="t('userManagement.dialogs.edit.fields.username.placeholder')"
            />
          </div>
          <div class="grid gap-2">
            <Label for="edit-email">{{ t('userManagement.email') }}</Label>
            <Input
              id="edit-email"
              v-model="editForm.email"
              type="email"
              :placeholder="t('userManagement.dialogs.edit.fields.email.placeholder')"
            />
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="isEditDialogOpen = false">{{
            t('userManagement.dialogs.edit.buttons.cancel')
          }}</Button>
          <Button @click="handleEditUser" :disabled="!isEditFormValid || isSubmitting">
            <Loader2 v-if="isSubmitting" class="h-4 w-4 mr-2 animate-spin" />
            {{ t('userManagement.updateUser') }}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Role Update Dialog -->
    <Dialog v-model:open="isRoleDialogOpen">
      <DialogContent class="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>{{ t('userManagement.updateUserRole') }}</DialogTitle>
          <DialogDescription>
            {{ t('userManagement.changeRole') }} {{ selectedUser?.username }}.
          </DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-4">
          <div class="grid gap-2">
            <Label for="new-role">{{ t('userManagement.newRole') }}</Label>
            <Select v-model="newRole">
              <SelectTrigger>
                <SelectValue
                  :placeholder="t('userManagement.dialogs.role.fields.newRole.placeholder')"
                />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="USER">{{ t('userManagement.roles.user') }}</SelectItem>
                <SelectItem value="ADMIN">{{ t('userManagement.roles.admin') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="isRoleDialogOpen = false">{{
            t('userManagement.dialogs.role.buttons.cancel')
          }}</Button>
          <Button @click="handleUpdateRole" :disabled="isSubmitting">
            <Loader2 v-if="isSubmitting" class="h-4 w-4 mr-2 animate-spin" />
            {{ t('userManagement.updateRole') }}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Delete User Dialog -->
    <AlertDialog v-model:open="isDeleteDialogOpen">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{{ t('userManagement.deleteUser') }}</AlertDialogTitle>
          <AlertDialogDescription>
            {{ t('userManagement.sureDelete') }} {{ selectedUser?.username }}?
            {{ t('userManagement.actionCannotBeUndone') }}
            {{ t('userManagement.willRemoveAllUserData') }}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>{{ t('userManagement.cancel') }}</AlertDialogCancel>
          <AlertDialogAction @click="handleDeleteUser" :disabled="isSubmitting">
            <Loader2 v-if="isSubmitting" class="h-4 w-4 mr-2 animate-spin" />
            {{ t('userManagement.deleteUser') }}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  </div>
</template>
