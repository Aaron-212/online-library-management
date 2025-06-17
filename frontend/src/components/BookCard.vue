<script setup lang="ts">
import { useImage } from '@vueuse/core'
import { computed } from 'vue'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'

const props = defineProps<{
  title: string
  author: string
  coverImageUrl?: string
  isbn?: string
}>()

const { isLoading, error } = useImage({ src: props.coverImageUrl || '' })

const altText = computed(() => `Cover image for ${props.title}`)
</script>

<template>
  <Card class="w-64 h-auto flex flex-col">
    <CardHeader class="p-0">
      <div
        class="aspect-[3/4] w-full bg-muted flex items-center justify-center rounded-t-lg overflow-hidden"
      >
        <img
          v-if="!isLoading && !error && props.coverImageUrl"
          :src="props.coverImageUrl"
          :alt="altText"
          class="object-cover w-full h-full"
        />
        <div v-else-if="isLoading" class="text-sm text-muted-foreground p-4 text-center">
          Loading image...
        </div>
        <div v-else class="text-sm text-muted-foreground p-4 text-center">No image available</div>
      </div>
    </CardHeader>
    <CardContent class="pt-4 pb-2 flex-grow">
      <CardTitle class="text-lg leading-tight mb-1 truncate" :title="props.title">
        {{ props.title }}
      </CardTitle>
      <CardDescription class="text-sm truncate" :title="props.author">
        By: {{ props.author }}
      </CardDescription>
    </CardContent>
    <CardFooter v-if="props.isbn" class="pb-4 pt-0">
      <p class="text-xs text-muted-foreground" :title="props.isbn">ISBN: {{ props.isbn }}</p>
    </CardFooter>
  </Card>
</template>
