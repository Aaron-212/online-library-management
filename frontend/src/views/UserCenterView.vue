<template>
  <SidebarProvider class="flex min-h-screen w-full">
    <AppSidebar />
    <main class="flex flex-1 flex-col overflow-hidden">
      <header class="flex h-16 shrink-0 items-center gap-2 px-4">
        <SidebarTrigger class="-ml-1" />
        <div class="py-2 w-px bg-sidebar-border" />
        <h1 class="text-lg font-semibold">PersonalCenter</h1>
      </header>
      <div class="flex flex-1 flex-col gap-4 p-4 pt-6">
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

<script lang="ts" setup>
import { useRoute } from 'vue-router';
import AppSidebar from '@/components/AppSidebar.vue';
import { SidebarProvider, SidebarTrigger } from '@/components/ui/sidebar';

const route = useRoute();
</script>