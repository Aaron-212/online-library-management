<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Button } from '@/components/ui/button'
import { ArrowLeft, Home } from 'lucide-vue-next'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.back()
}
</script>

<template>
  <div class="flex flex-col items-center justify-center min-h-[60vh] text-center space-y-6">
    <div class="space-y-4">
      <h1 class="text-6xl font-bold text-muted-foreground">404</h1>
      <h2 class="text-2xl font-semibold">Page Not Found</h2>
      <p class="text-muted-foreground max-w-md">
        The page you're looking for doesn't exist. It might have been moved, deleted, or you entered the wrong URL.
      </p>
    </div>
    
    <div class="flex gap-4">
      <Button @click="goBack" variant="outline">
        <ArrowLeft class="h-4 w-4 mr-2" />
        Go Back
      </Button>
      <Button @click="goHome">
        <Home class="h-4 w-4 mr-2" />
        Go Home
      </Button>
    </div>
  </div>
</template>