<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Button } from '@/components/ui/button'
import { ArrowLeft, Home } from 'lucide-vue-next'

const router = useRouter()
const { t } = useI18n()

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
      <h1 class="text-6xl font-bold text-muted-foreground">{{ t('notFound.title') }}</h1>
      <h2 class="text-2xl font-semibold">{{ t('notFound.heading') }}</h2>
      <p class="text-muted-foreground max-w-md">
        {{ t('notFound.description') }}
      </p>
    </div>

    <div class="flex gap-4">
      <Button @click="goBack" variant="outline">
        <ArrowLeft class="h-4 w-4 mr-2" />
        {{ t('notFound.buttons.goBack') }}
      </Button>
      <Button @click="goHome">
        <Home class="h-4 w-4 mr-2" />
        {{ t('notFound.buttons.goHome') }}
      </Button>
    </div>
  </div>
</template>
