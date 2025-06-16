<script lang="ts" setup>
import { RouterLink, useRouter } from 'vue-router'
import { BookOpen, Clock, Gauge, Home, Library, LogOut, Search, User, Users } from 'lucide-vue-next'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { useAuthStore } from '@/stores/auth'
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

const navigationItems = [
  {
    title: 'Home',
    url: '/',
    icon: Home,
  },
  {
    title: 'Dashboard',
    url: '/dashboard',
    icon: Gauge,
  },
  {
    title: 'Books',
    url: '/books',
    icon: BookOpen,
  },
  {
    title: 'Members',
    url: '/members',
    icon: Users,
  },
  {
    title: 'Search',
    url: '/search',
    icon: Search,
  },
]

const libraryItems = [
  {
    title: 'Borrow Books',
    url: '/borrow',
    icon: Clock,
  },
  {
    title: 'Categories',
    url: '/categories',
    icon: Library,
  },
]

const router = useRouter()
const authStore = useAuthStore()

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
        <span class="font-semibold">Library System</span>
      </div>
    </SidebarHeader>

    <SidebarContent>
      <!-- Main Navigation -->
      <SidebarGroup>
        <SidebarGroupLabel>Navigation</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in navigationItems" :key="item.title">
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
      <SidebarGroup v-if="authStore.isAuthenticated">
        <SidebarGroupLabel>Library Management</SidebarGroupLabel>
        <SidebarGroupContent>
          <SidebarMenu>
            <SidebarMenuItem v-for="item in libraryItems" :key="item.title">
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
        <SidebarMenuItem>
          <!-- Show dropdown when authenticated, otherwise show login link -->
          <DropdownMenu v-if="authStore.isAuthenticated">
            <DropdownMenuTrigger as-child>
              <SidebarMenuButton class="h-12 px-2">
                <Avatar class="h-8 w-8">
                  <AvatarImage alt="User" src="/placeholder-avatar.jpg" />
                  <AvatarFallback
                    >{{ authStore.user?.username?.charAt(0).toUpperCase() || 'U' }}
                  </AvatarFallback>
                </Avatar>
                <div class="flex flex-col items-start">
                  <span class="text-sm font-medium">{{
                    authStore.user?.username || 'Anonymous'
                  }}</span>
                  <span class="text-xs text-muted-foreground">View Profile</span>
                </div>
              </SidebarMenuButton>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" class="w-56" side="top">
              <DropdownMenuItem as-child>
                <RouterLink class="flex items-center gap-2" to="/profile">
                  <User class="h-4 w-4" />
                  <span>Profile</span>
                </RouterLink>
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem
                class="flex items-center gap-2"
                variant="destructive"
                @click="handleLogout"
              >
                <LogOut class="h-4 w-4" />
                <span>Logout</span>
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
                <span class="text-sm font-medium">Anonymous</span>
                <span class="text-xs text-muted-foreground">Click here to login</span>
              </div>
            </RouterLink>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarFooter>
  </Sidebar>
</template>
