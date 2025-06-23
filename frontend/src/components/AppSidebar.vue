<script lang="ts" setup>
import { RouterLink, useRouter } from 'vue-router'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  BarChart3,
  Bell,
  BookOpen,
  Clock,
  CreditCard,
  Gauge,
  Heart,
  Home,
  Languages,
  Library,
  LogOut,
  Settings,
  Shield,
  User,
  Users,
} from 'lucide-vue-next'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { useAuthStore } from '@/stores/auth'
import { useLanguage } from '@/composables/useLanguage'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'

const router = useRouter()
const authStore = useAuthStore()
const { currentLanguage, currentLanguageLabel, availableLanguages, changeLanguage } = useLanguage()
const { t } = useI18n()

// Main navigation for all users - computed to allow dynamic URLs
const navigationItems = computed(() => [
  {
    title: t('sidebar.home'),
    url: '/',
    icon: Home,
  },
  {
    title: t('sidebar.dashboard'),
    url: '/dashboard',
    icon: Gauge,
    requiresAuth: true,
    hideForAdmin: true, // Hide this for admins since they have Admin Dashboard
  },
  {
    title: t('sidebar.books'),
    url: '/books',
    icon: BookOpen,
  },
  {
    title: t('sidebar.notices'),
    url: '/notices',
    icon: Bell,
  },
  {
    title: t('sidebar.favorites'),
    url: '/favorites',
    icon: Heart,
    requiresAuth: true,
  },
  {
    title: t('sidebar.billingCenter'),
    url: '/billing',
    icon: CreditCard,
    requiresAuth: true,
  },
])

// Library management for authenticated users
const libraryItems = computed(() => [
  {
    title: t('sidebar.myBorrowing'),
    url: '/borrows',
    icon: Clock,
    requiresAuth: true,
  },
])

// Admin navigation items
const adminItems = computed(() => [
  {
    title: t('sidebar.adminDashboard'),
    url: '/admin/dashboard',
    icon: Gauge,
  },
  {
    title: t('sidebar.manageBooks'),
    url: '/admin/books',
    icon: Library,
  },
  {
    title: t('sidebar.userManagement'),
    url: '/admin/users',
    icon: Users,
  },
  {
    title: t('sidebar.borrowingManagement'),
    url: '/admin/borrowing',
    icon: Clock,
  },
  {
    title: t('sidebar.borrowingRules'),
    url: '/admin/borrowing-rules',
    icon: Settings,
  },
  {
    title: t('sidebar.feeManagement'),
    url: '/admin/fees',
    icon: CreditCard,
  },
  {
    title: t('sidebar.reports'),
    url: '/admin/reports',
    icon: BarChart3,
  },
])

// Computed properties
const isAdmin = computed(() => {
  return authStore.isAuthenticated && authStore.isAdmin()
})

const filteredNavigationItems = computed(() => {
  return navigationItems.value.filter((item) => {
    // Filter out items that require auth when user is not authenticated
    if (item.requiresAuth && !authStore.isAuthenticated) return false

    // Filter out items that should be hidden for admins when user is admin
    if (item.hideForAdmin && authStore.isAuthenticated && authStore.isAdmin()) return false

    return true
  })
})

const filteredLibraryItems = computed(() => {
  return libraryItems.value.filter((item) => !item.requiresAuth || authStore.isAuthenticated)
})

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <Sidebar>
    <SidebarHeader class="border-b border-sidebar-border h-16">
      <div class="flex items-center gap-2 px-2 h-full">
        <BookOpen class="h-6 w-6" />
        <span class="font-semibold">{{ t('sidebar.title') }}</span>
      </div>
    </SidebarHeader>

    <SidebarContent>
      <!-- Main Navigation -->
      <SidebarGroup>
        <SidebarGroupLabel>{{ t('sidebar.navigation') }}</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in filteredNavigationItems" :key="item.title">
              <SidebarMenuButton asChild>
                <RouterLink :to="item.url" class="flex items-center gap-2">
                  <component :is="item.icon" class="h-4 w-4" />
                  <span>{{ item.title }}</span>
                </RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>

      <!-- Library Management -->
      <SidebarGroup v-if="authStore.isAuthenticated && filteredLibraryItems.length > 0">
        <SidebarGroupLabel>{{ t('sidebar.libraryManagement') }}</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in filteredLibraryItems" :key="item.title">
              <SidebarMenuButton asChild>
                <RouterLink :to="item.url" class="flex items-center gap-2">
                  <component :is="item.icon" class="h-4 w-4" />
                  <span>{{ item.title }}</span>
                </RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>

      <!-- Admin Section -->
      <SidebarGroup v-if="isAdmin">
        <SidebarGroupLabel>{{ t('sidebar.administration') }}</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in adminItems" :key="item.title">
              <SidebarMenuButton asChild>
                <RouterLink :to="item.url" class="flex items-center gap-2">
                  <component :is="item.icon" class="h-4 w-4" />
                  <span>{{ item.title }}</span>
                </RouterLink>
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarGroupContent>
      </SidebarGroup>
    </SidebarContent>

    <SidebarFooter>
      <SidebarMenu>
        <!-- Language Selector -->
        <SidebarMenuItem>
          <Select :model-value="currentLanguage"
            @update:model-value="(value) => typeof value === 'string' && changeLanguage(value)">
            <SelectTrigger class="w-full">
              <SelectValue>
                <Languages class="h-4 w-4" />
                {{ currentLanguageLabel }}
              </SelectValue>
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="lang in availableLanguages" :key="lang.value" :value="lang.value">
                {{ lang.label }}
              </SelectItem>
            </SelectContent>
          </Select>
        </SidebarMenuItem>

        <SidebarMenuItem>
          <!-- Show dropdown when authenticated, otherwise show login link -->
          <DropdownMenu v-if="authStore.isAuthenticated">
            <DropdownMenuTrigger as-child>
              <SidebarMenuButton class="h-12 px-2">
                <Avatar class="h-8 w-8">
                  <AvatarImage alt="User" src="/placeholder-avatar.jpg" />
                  <AvatarFallback>{{ authStore.user?.username?.charAt(0).toUpperCase() || 'U' }}
                  </AvatarFallback>
                </Avatar>
                <div class="flex flex-col items-start">
                  <span class="text-sm font-medium">{{
                    authStore.user?.username || t('sidebar.anonymous')
                  }}</span>
                  <span class="text-xs text-muted-foreground">{{ t('sidebar.viewProfile') }}</span>
                </div>
              </SidebarMenuButton>
            </DropdownMenuTrigger>
            <DropdownMenuContent class="w-56" side="top">
              <DropdownMenuItem as-child>
                <RouterLink class="flex items-center gap-2" to="/profile">
                  <User class="h-4 w-4" />
                  <span>{{ t('sidebar.profile') }}</span>
                </RouterLink>
              </DropdownMenuItem>
              <DropdownMenuItem as-child>
                <RouterLink class="flex items-center gap-2" to="/borrows">
                  <Clock class="h-4 w-4" />
                  <span>{{ t('sidebar.myBorrowing') }}</span>
                </RouterLink>
              </DropdownMenuItem>
              <DropdownMenuSeparator v-if="isAdmin" />
              <DropdownMenuItem v-if="isAdmin" as-child>
                <RouterLink class="flex items-center gap-2" to="/admin/dashboard">
                  <Shield class="h-4 w-4" />
                  <span>{{ t('sidebar.adminDashboard') }}</span>
                </RouterLink>
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem class="flex items-center gap-2" variant="destructive" @click="handleLogout">
                <LogOut class="h-4 w-4" />
                <span>{{ t('sidebar.logout') }}</span>
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>

          <!-- Show login link when not authenticated -->
          <SidebarMenuButton v-else asChild class="h-12 px-2">
            <RouterLink class="flex items-center gap-2" to="/login">
              <Avatar class="h-8 w-8">
                <AvatarImage alt="User" src="/placeholder-avatar.jpg" />
                <AvatarFallback>U</AvatarFallback>
              </Avatar>
              <div class="flex flex-col items-start">
                <span class="text-sm font-medium">{{ t('sidebar.anonymous') }}</span>
                <span class="text-xs text-muted-foreground">{{ t('sidebar.clickToLogin') }}</span>
              </div>
            </RouterLink>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarFooter>
  </Sidebar>
</template>
