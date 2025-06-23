<script lang="ts" setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppSidebar from '@/components/AppSidebar.vue'
import { SidebarProvider, SidebarTrigger } from '@/components/ui/sidebar'
import { useDarkMode } from '@/composables/useDarkMode'
import { toTitleCase } from '@/lib/utils.ts'
import { Toaster } from '@/components/ui/sonner'
import 'vue-sonner/style.css'

// Initialize automatic dark mode detection
useDarkMode()

const route = useRoute()

// Define page titles for each route
const pageTitle = computed(() => {
  const routeTitles: Record<string, string> = {
    borrow: 'Borrow Books',
  }

  const routeName = route.name?.toString()
  return routeTitles[routeName || ''] || (routeName ? toTitleCase(routeName) : 'Unknown Page')
})
</script>

<template>
  <Toaster />
  <SidebarProvider class="flex min-h-screen w-full">
    <AppSidebar />
    <main class="flex flex-1 flex-col overflow-hidden relative">
      <header class="absolute inset-x-0 top-0 z-50 flex h-16 items-center gap-2 px-4 backdrop-blur-lg border-b">
        <SidebarTrigger class="-ml-1" />
        <div class="py-2 w-px bg-sidebar-border" />
        <h1 class="text-lg font-semibold">{{ pageTitle }}</h1>
      </header>
      <div class="flex flex-1 flex-col gap-4 p-4 pt-18 overflow-auto">
        <RouterView v-slot="{ Component, route }">
          <Transition mode="out-in" name="fade">
            <div :key="route.name">
              <component :is="Component"></component>
            </div>
          </Transition>
        </RouterView>
      </div>
    </main>
  </SidebarProvider>
</template>

<!--suppress CssUnusedSymbol-->
<style scoped>
.fade-enter-active {
  animation: fade 0.25s;
}

.fade-leave-active {
  animation: fade 0.25s reverse;
}

@keyframes fade {
  from {
    opacity: 0;
  }

  to {
    opacity: 100%;
  }
}
</style>
