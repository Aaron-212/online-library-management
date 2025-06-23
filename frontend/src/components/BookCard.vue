<script lang="ts" setup>
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
import FavoriteButton from '@/components/FavoriteButton.vue'
import { useI18n } from 'vue-i18n'

const props = defineProps<{
  title: string
  author: string
  coverImageUrl?: string
  isbn?: string
  available?: boolean
  totalCopies?: number
  availableCopies?: number
  bookId?: number
  showFavoriteButton?: boolean
}>()

const emit = defineEmits<{
  favoriteChanged: [bookId: number, isFavorite: boolean]
}>()

const { t } = useI18n()
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
  if (props.availableCopies === 0) return t('bookCard.status.notAvailable')
  if (props.availableCopies === 1) return t('bookCard.availability.singleCopy')
  return t('bookCard.availability.multipleCopies', { count: props.availableCopies })
})

const handleFavoriteChanged = (isFavorite: boolean) => {
  if (props.bookId) {
    emit('favoriteChanged', props.bookId, isFavorite)
  }
}
</script>

<template>
  <Card class="w-full h-auto flex flex-col hover:shadow-lg transition-shadow p-0">
    <CardHeader class="p-0">
      <div
        class="aspect-[3/4] w-full bg-muted flex items-center justify-center rounded-t-lg overflow-hidden relative"
      >
        <img
          v-if="!isLoading && !error && props.coverImageUrl"
          :alt="altText"
          :src="props.coverImageUrl"
          class="object-cover w-full h-full"
        />
        <div v-else-if="isLoading" class="text-sm text-muted-foreground p-4 text-center">
          {{ t('bookCard.loadingImage') }}
        </div>
        <div v-else class="text-sm text-muted-foreground p-4 text-center">
          {{ t('bookCard.noImageAvailable') }}
        </div>

        <!-- Top left favorite button -->
        <div v-if="showFavoriteButton && bookId" class="absolute top-2 left-2">
          <FavoriteButton
            :book-id="bookId"
            size="sm"
            variant="outline"
            @favorite-changed="handleFavoriteChanged"
          />
        </div>

        <!-- Top right availability badge -->
        <div v-if="availabilityStatus" class="absolute top-2 right-2">
          <Badge
            :variant="
              availabilityStatus === 'available'
                ? 'default'
                : availabilityStatus === 'limited'
                  ? 'secondary'
                  : 'destructive'
            "
            class="text-xs"
          >
            {{
              availabilityStatus === 'available'
                ? t('bookCard.status.available')
                : availabilityStatus === 'limited'
                  ? t('bookCard.status.limited')
                  : t('bookCard.status.outOfStock')
            }}
          </Badge>
        </div>
      </div>
    </CardHeader>
    <CardContent class="pt-4 pb-2 flex-grow">
      <CardTitle :title="props.title" class="text-lg leading-tight mb-2 line-clamp-2">
        {{ props.title }}
      </CardTitle>
      <CardDescription :title="props.author" class="text-sm mb-2 line-clamp-1">
        {{ t('bookCard.authorPrefix') }}{{ props.author }}
      </CardDescription>

      <!-- Availability Info -->
      <div v-if="availableCopies !== undefined" class="text-xs text-muted-foreground mb-1">
        {{ availabilityText }}
        <span v-if="totalCopies !== undefined" class="ml-1"
          >({{ totalCopies }} {{ t('bookCard.availability.total') }})</span
        >
      </div>
    </CardContent>
    <CardFooter v-if="props.isbn" class="pb-4 pt-0">
      <p :title="props.isbn" class="text-xs text-muted-foreground truncate w-full">
        {{ t('bookCard.isbnPrefix') }}{{ props.isbn }}
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
