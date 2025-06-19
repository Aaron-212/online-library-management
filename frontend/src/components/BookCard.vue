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
import { Badge } from '@/components/ui/badge'

const props = defineProps<{
  title: string
  author: string
  coverImageUrl?: string
  isbn?: string
  available?: boolean
  totalCopies?: number
  availableCopies?: number
}>()

const { isLoading, error } = useImage({ src: props.coverImageUrl || '' })

const altText = computed(() => `Cover image for ${props.title}`)

const availabilityStatus = computed(() => {
  if (props.availableCopies === undefined) return null
  if (props.availableCopies === 0) return 'unavailable'
  if (props.availableCopies <= 2) return 'limited'
  return 'available'
})

const availabilityText = computed(() => {
  if (props.availableCopies === undefined) return ''
  if (props.availableCopies === 0) return 'Not Available'
  if (props.availableCopies === 1) return '1 copy available'
  return `${props.availableCopies} copies available`
})
</script>

<template>
  <Card class="w-64 h-auto flex flex-col hover:shadow-lg transition-shadow">
    <CardHeader class="p-0">
      <div
        class="aspect-[3/4] w-full bg-muted flex items-center justify-center rounded-t-lg overflow-hidden relative"
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
        
        <!-- Availability Badge -->
        <div v-if="availabilityStatus" class="absolute top-2 right-2">
          <Badge 
            :variant="availabilityStatus === 'available' ? 'default' : 
                     availabilityStatus === 'limited' ? 'secondary' : 'destructive'"
            class="text-xs"
          >
            {{ availabilityStatus === 'available' ? 'Available' : 
               availabilityStatus === 'limited' ? 'Limited' : 'Out of Stock' }}
          </Badge>
        </div>
      </div>
    </CardHeader>
    <CardContent class="pt-4 pb-2 flex-grow">
      <CardTitle class="text-lg leading-tight mb-2 line-clamp-2" :title="props.title">
        {{ props.title }}
      </CardTitle>
      <CardDescription class="text-sm mb-2 line-clamp-1" :title="props.author">
        By: {{ props.author }}
      </CardDescription>
      
      <!-- Availability Info -->
      <div v-if="availableCopies !== undefined" class="text-xs text-muted-foreground mb-1">
        {{ availabilityText }}
        <span v-if="totalCopies !== undefined" class="ml-1">({{ totalCopies }} total)</span>
      </div>
    </CardContent>
    <CardFooter v-if="props.isbn" class="pb-4 pt-0">
      <p class="text-xs text-muted-foreground truncate w-full" :title="props.isbn">
        ISBN: {{ props.isbn }}
      </p>
    </CardFooter>
  </Card>
</template>

<style scoped>
.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
