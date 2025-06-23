<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold">{{ t('favorites.title') }}</h1>
      <p class="text-muted-foreground">{{ t('favorites.description') }}</p>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-8">
      <div class="text-muted-foreground">{{ t('favorites.loading') }}</div>
    </div>

    <template v-else>
      <!-- Favorites List -->
      <div class="bg-background border rounded-lg shadow-sm">
        <div class="p-4 border-b">
          <h2 class="text-xl font-semibold">{{ t('favorites.pageTitle') }}</h2>
          <p class="text-sm text-muted-foreground">
            {{ favorites.length === 1 ? t('favorites.count.singular', { count: favorites.length }) :
              t('favorites.count.plural', { count: favorites.length }) }}
          </p>
        </div>
        <div class="p-4">
          <div v-if="favorites.length === 0" class="text-center py-12 text-muted-foreground">
            <div class="mb-4">
              <Heart class="mx-auto h-12 w-12 text-muted-foreground/50" />
            </div>
            <h3 class="text-lg font-semibold mb-2">{{ t('favorites.empty.title') }}</h3>
            <p>{{ t('favorites.empty.description') }}</p>
          </div>
          <div v-else class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b">
                  <th class="text-left p-3">{{ t('favorites.table.headers.title') }}</th>
                  <th class="text-left p-3">{{ t('favorites.table.headers.authors') }}</th>
                  <th class="text-left p-3">{{ t('favorites.table.headers.category') }}</th>
                  <th class="text-left p-3">{{ t('favorites.table.headers.addedDate') }}</th>
                  <th class="text-left p-3">{{ t('favorites.table.headers.actions') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="favorite in favorites" :key="favorite.id" class="border-b hover:bg-muted/50">
                  <td class="p-3">
                    <div class="font-medium">{{ favorite.book.title }}</div>
                    <div class="text-sm text-muted-foreground">{{ t('favorites.table.isbnPrefix') }}{{
                      favorite.book.isbn }}</div>
                  </td>
                  <td class="p-3">
                    <div class="text-sm">
                      {{favorite.book.authors.map((a) => a.name).join(', ')}}
                    </div>
                  </td>
                  <td class="p-3">
                    <span
                      class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                      {{ favorite.book.indexCategory?.name || t('favorites.table.categoryDefault') }}
                    </span>
                  </td>
                  <td class="p-3 text-sm text-muted-foreground">
                    {{ formatDate(favorite.createTime) }}
                  </td>
                  <td class="p-3">
                    <div class="flex gap-2">
                      <button
                        class="px-3 py-1 bg-primary text-primary-foreground rounded-md text-sm hover:bg-primary/90 transition-colors"
                        @click="viewBook(favorite.book.id)">
                        {{ t('favorites.table.actions.viewDetails') }}
                      </button>
                      <button
                        class="px-3 py-1 border border-destructive text-destructive bg-background hover:bg-destructive hover:text-destructive-foreground rounded-md text-sm transition-colors disabled:opacity-50"
                        @click="removeFavorite(favorite.id)" :disabled="isRemoving">
                        {{ isRemoving ? t('favorites.table.actions.removing') : t('favorites.table.actions.remove') }}
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Heart } from 'lucide-vue-next'
import { favoritesService } from '@/lib/api'
import type { FavoriteDto } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const { t } = useI18n()

const router = useRouter()

// Reactive data
const isLoading = ref(false)
const isRemoving = ref(false)
const favorites = ref<FavoriteDto[]>([])

// Methods
const fetchFavorites = async () => {
  try {
    isLoading.value = true
    const response = await favoritesService.getUserFavorites({ page: 0, size: 100 })
    favorites.value = response.content
  } catch (error) {
    console.error('Failed to fetch favorites:', error)
    toast.error(t('favorites.messages.loadError'))
  } finally {
    isLoading.value = false
  }
}

const removeFavorite = async (favoriteId: number) => {
  if (!confirm(t('favorites.dialogs.removeConfirm'))) {
    return
  }

  try {
    isRemoving.value = true
    await favoritesService.removeFavorite(favoriteId)
    toast.success(t('favorites.messages.removeSuccess'))
    // Refresh the favorites list
    await fetchFavorites()
  } catch (error) {
    console.error('Failed to remove favorite:', error)
    toast.error(t('favorites.messages.removeError'))
  } finally {
    isRemoving.value = false
  }
}

const viewBook = (bookId: number) => {
  router.push(`/books/${bookId}`)
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

// Lifecycle
onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
table {
  border-collapse: collapse;
}
</style>
