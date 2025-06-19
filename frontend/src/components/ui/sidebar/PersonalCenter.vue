<template>
  <div class="flex h-screen">
    <!-- 侧边栏菜单 -->
    <Sidebar>
      <SidebarHeader class="border-b border-sidebar-border h-16">
        <div class="flex items-center gap-2 px-2 h-full">
          <BookOpen class="h-6 w-6" />
          <span class="font-semibold">Personal_Center</span>
        </div>
      </SidebarHeader>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Personal_Center_Navigation</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              <SidebarMenuItem v-for="item in personalCenterItems" :key="item.title">
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
    </Sidebar>
    <!-- 主内容区 -->
    <div class="flex-1 p-6">
      <router-view />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { RouterLink } from 'vue-router'
import { BookOpen, Clock, DollarSign, Heart, User } from 'lucide-vue-next'
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

const personalCenterItems = [
  { title: 'Personal_Profile', url: '/personal-center/profile', icon: User },
  { title: 'Borrow_Records', url: '/personal-center/borrow-records', icon: Clock },
  { title: 'My_Reservation', url: '/personal-center/reservations', icon: Clock },
  { title: 'Favorites', url: '/personal-center/favorites', icon: Heart },
  { title: 'Bill_Center', url: '/personal-center/billing', icon: DollarSign },
]
</script>