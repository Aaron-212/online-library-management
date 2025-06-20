<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { toast } from 'vue-sonner'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from '@/components/ui/alert-dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
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
  Users 
} from 'lucide-vue-next'
import { usersService } from '@/lib/api'
import type { UserPublic, UserAdmin, UserCreateDto, UserUpdateDto } from '@/lib/api/types'
import { useAuthStore } from '@/stores/auth'

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
  role: 'USER'
})

// Edit form
const editForm = ref<UserUpdateDto>({
  username: '',
  email: ''
})

// Role form
const newRole = ref<'USER' | 'ADMIN'>('USER')

// Computed
const hasNextPage = computed(() => currentPage.value < totalPages.value - 1)
const hasPrevPage = computed(() => currentPage.value > 0)

const isFormValid = computed(() => {
  return createForm.value.username.trim() && 
         createForm.value.email.trim() && 
         createForm.value.password.trim()
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
      search: searchQuery.value || undefined
    })
    
    users.value = response.content
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
  } catch (error) {
    console.error('Error loading users:', error)
    toast.error('Failed to load users')
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
    role: 'USER'
  }
  isCreateDialogOpen.value = true
}

const openEditDialog = (user: UserAdmin) => {
  selectedUser.value = user
  editForm.value = {
    username: user.username,
    email: user.email || ''
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
    toast.error('Please fill in all required fields')
    return
  }

  try {
    isSubmitting.value = true
    await usersService.createUser(createForm.value)
    toast.success('User created successfully')
    isCreateDialogOpen.value = false
    await loadUsers()
  } catch (error: any) {
    console.error('Error creating user:', error)
    toast.error(error.error || 'Failed to create user')
  } finally {
    isSubmitting.value = false
  }
}

const handleEditUser = async () => {
  if (!isEditFormValid.value || !selectedUser.value) {
    toast.error('Please fill in all required fields')
    return
  }

  try {
    isSubmitting.value = true
    await usersService.updateUser(selectedUser.value.id, editForm.value)
    toast.success('User updated successfully')
    isEditDialogOpen.value = false
    await loadUsers()
  } catch (error: any) {
    console.error('Error updating user:', error)
    toast.error(error.error || 'Failed to update user')
  } finally {
    isSubmitting.value = false
  }
}

const handleUpdateRole = async () => {
  if (!selectedUser.value) return

  try {
    isSubmitting.value = true
    await usersService.updateUserRole(selectedUser.value.id, newRole.value)
    toast.success('User role updated successfully')
    isRoleDialogOpen.value = false
    await loadUsers()
  } catch (error: any) {
    console.error('Error updating user role:', error)
    toast.error(error.error || 'Failed to update user role')
  } finally {
    isSubmitting.value = false
  }
}

const handleDeleteUser = async () => {
  if (!selectedUser.value) return

  try {
    isSubmitting.value = true
    await usersService.deleteUser(selectedUser.value.id)
    toast.success('User deleted successfully')
    isDeleteDialogOpen.value = false
    await loadUsers()
  } catch (error: any) {
    console.error('Error deleting user:', error)
    toast.error(error.error || 'Failed to delete user')
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
        <h1 class="text-2xl font-bold">User Management</h1>
        <p class="text-muted-foreground">Manage library users and their permissions</p>
      </div>
      <Button @click="openCreateDialog" class="gap-2">
        <Plus class="h-4 w-4" />
        Add User
      </Button>
    </div>

    <!-- Search and Controls -->
    <Card>
      <CardHeader>
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="relative">
              <Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input 
                v-model="searchQuery"
                placeholder="Search users by username or email..."
                class="pl-10 w-64"
              />
            </div>
          </div>
          <div class="flex items-center gap-2 text-sm text-muted-foreground">
            <Users class="h-4 w-4" />
            {{ totalElements }} total users
          </div>
        </div>
      </CardHeader>
    </Card>

    <!-- Users Table -->
    <Card>
      <CardContent class="p-0">
        <div class="border rounded-lg">
          <!-- Table Header -->
          <div class="grid grid-cols-6 gap-4 p-4 border-b bg-muted/50 font-medium">
            <div>User</div>
            <div>Email</div>
            <div>Role</div>
            <div>Created</div>
            <div>Last Updated</div>
            <div class="text-right">Actions</div>
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
            <p class="text-lg font-medium mb-2">No users found</p>
            <p>{{ searchQuery ? 'Try adjusting your search terms' : 'Get started by creating your first user' }}</p>
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
                 {{ user.email || 'Not provided' }}
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
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="flex items-center justify-between p-4 border-t">
          <div class="text-sm text-muted-foreground">
            Showing {{ currentPage * pageSize + 1 }} to {{ Math.min((currentPage + 1) * pageSize, totalElements) }} 
            of {{ totalElements }} users
          </div>
          <div class="flex items-center gap-2">
            <Button variant="outline" size="sm" @click="prevPage" :disabled="!hasPrevPage">
              <ChevronLeft class="h-4 w-4" />
              Previous
            </Button>
            <div class="flex items-center gap-1">
              <span class="text-sm">Page {{ currentPage + 1 }} of {{ totalPages }}</span>
            </div>
            <Button variant="outline" size="sm" @click="nextPage" :disabled="!hasNextPage">
              Next
              <ChevronRight class="h-4 w-4" />
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>

    <!-- Create User Dialog -->
    <Dialog v-model:open="isCreateDialogOpen">
      <DialogContent class="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Create New User</DialogTitle>
          <DialogDescription>
            Add a new user to the library management system.
          </DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-4">
          <div class="grid gap-2">
            <Label for="create-username">Username</Label>
            <Input
              id="create-username"
              v-model="createForm.username"
              placeholder="Enter username"
            />
          </div>
          <div class="grid gap-2">
            <Label for="create-email">Email</Label>
            <Input
              id="create-email"
              v-model="createForm.email"
              type="email"
              placeholder="Enter email address"
            />
          </div>
          <div class="grid gap-2">
            <Label for="create-password">Password</Label>
            <Input
              id="create-password"
              v-model="createForm.password"
              type="password"
              placeholder="Enter password"
            />
          </div>
          <div class="grid gap-2">
            <Label for="create-role">Role</Label>
            <Select v-model="createForm.role">
              <SelectTrigger>
                <SelectValue placeholder="Select role" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="USER">User</SelectItem>
                <SelectItem value="ADMIN">Admin</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="isCreateDialogOpen = false">Cancel</Button>
          <Button @click="handleCreateUser" :disabled="!isFormValid || isSubmitting">
            <Loader2 v-if="isSubmitting" class="h-4 w-4 mr-2 animate-spin" />
            Create User
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Edit User Dialog -->
    <Dialog v-model:open="isEditDialogOpen">
      <DialogContent class="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Edit User</DialogTitle>
          <DialogDescription>
            Update user information for {{ selectedUser?.username }}.
          </DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-4">
          <div class="grid gap-2">
            <Label for="edit-username">Username</Label>
            <Input
              id="edit-username"
              v-model="editForm.username"
              placeholder="Enter username"
            />
          </div>
          <div class="grid gap-2">
            <Label for="edit-email">Email</Label>
            <Input
              id="edit-email"
              v-model="editForm.email"
              type="email"
              placeholder="Enter email address"
            />
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="isEditDialogOpen = false">Cancel</Button>
          <Button @click="handleEditUser" :disabled="!isEditFormValid || isSubmitting">
            <Loader2 v-if="isSubmitting" class="h-4 w-4 mr-2 animate-spin" />
            Update User
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Role Update Dialog -->
    <Dialog v-model:open="isRoleDialogOpen">
      <DialogContent class="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Update User Role</DialogTitle>
          <DialogDescription>
            Change the role for {{ selectedUser?.username }}.
          </DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-4">
          <div class="grid gap-2">
            <Label for="new-role">New Role</Label>
            <Select v-model="newRole">
              <SelectTrigger>
                <SelectValue placeholder="Select new role" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="USER">User</SelectItem>
                <SelectItem value="ADMIN">Admin</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="isRoleDialogOpen = false">Cancel</Button>
          <Button @click="handleUpdateRole" :disabled="isSubmitting">
            <Loader2 v-if="isSubmitting" class="h-4 w-4 mr-2 animate-spin" />
            Update Role
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Delete User Dialog -->
    <AlertDialog v-model:open="isDeleteDialogOpen">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Delete User</AlertDialogTitle>
          <AlertDialogDescription>
            Are you sure you want to delete {{ selectedUser?.username }}? This action cannot be undone and will remove all user data.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Cancel</AlertDialogCancel>
          <AlertDialogAction @click="handleDeleteUser" :disabled="isSubmitting">
            <Loader2 v-if="isSubmitting" class="h-4 w-4 mr-2 animate-spin" />
            Delete User
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  </div>
</template>
