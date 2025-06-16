<script lang="ts" setup>
import { RouterLink } from 'vue-router'
import { BookOpen, Clock, Home, Library, Search, Users } from 'lucide-vue-next'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
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

const navigationItems = [
  {
    title: 'Dashboard',
    url: '/',
    icon: Home,
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
</script>

<template>
  <Sidebar>
    <SidebarHeader class="border-b border-sidebar-border h-12">
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
      <SidebarGroup>
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
          <SidebarMenuButton asChild class="h-12 px-2">
            <RouterLink class="flex items-center gap-2" to="/profile">
              <Avatar class="h-8 w-8">
                <AvatarImage alt="User" src="/placeholder-avatar.jpg" />
                <AvatarFallback>U</AvatarFallback>
              </Avatar>
              <div class="flex flex-col items-start">
                <span class="text-sm font-medium">User Profile</span>
                <span class="text-xs text-muted-foreground">View profile</span>
              </div>
            </RouterLink>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </SidebarMenu>
    </SidebarFooter>
  </Sidebar>
</template>
