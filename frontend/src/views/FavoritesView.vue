<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold">Favorites</h1>
      <p class="text-muted-foreground">Manage your favorite books</p>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-8">
      <div class="text-muted-foreground">Loading favorites...</div>
    </div>

    <template v-else>
      <!-- Favorites List -->
      <div class="bg-background border rounded-lg shadow-sm">
        <div class="p-4 border-b">
          <h2 class="text-xl font-semibold">My Favorite Books</h2>
          <p class="text-sm text-muted-foreground">
            {{ favorites.length }} book{{ favorites.length !== 1 ? 's' : '' }} in your favorites
          </p>
        </div>
        <div class="p-4">
          <div v-if="favorites.length === 0" class="text-center py-12 text-muted-foreground">
            <div class="mb-4">
              <svg
                class="mx-auto h-12 w-12 text-muted-foreground/50"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
                />
              </svg>
            </div>
            <h3 class="text-lg font-semibold mb-2">No favorites yet</h3>
            <p>Start adding books to your favorites to see them here.</p>
          </div>
          <div v-else class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b">
                  <th class="text-left p-3">Book Title</th>
                  <th class="text-left p-3">Author(s)</th>
                  <th class="text-left p-3">Category</th>
                  <th class="text-left p-3">Added Date</th>
                  <th class="text-left p-3">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="favorite in favorites"
                  :key="favorite.id"
                  class="border-b hover:bg-muted/50"
                >
                  <td class="p-3">
                    <div class="font-medium">{{ favorite.book.title }}</div>
                    <div class="text-sm text-muted-foreground">ISBN: {{ favorite.book.isbn }}</div>
                  </td>
                  <td class="p-3">
                    <div class="text-sm">
                      {{ favorite.book.authors.map((a) => a.name).join(', ') }}
                    </div>
                  </td>
                  <td class="p-3">
                    <span
                      class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                    >
                      {{ favorite.book.indexCategory?.name || 'Uncategorized' }}
                    </span>
                  </td>
                  <td class="p-3 text-sm text-muted-foreground">
                    {{ formatDate(favorite.createTime) }}
                  </td>
                  <td class="p-3">
                    <div class="flex gap-2">
                      <button
                        class="px-3 py-1 bg-primary text-primary-foreground rounded-md text-sm hover:bg-primary/90 transition-colors"
                        @click="viewBook(favorite.book.id)"
                      >
                        View Details
                      </button>
                      <button
                        class="px-3 py-1 border border-destructive text-destructive bg-background hover:bg-destructive hover:text-destructive-foreground rounded-md text-sm transition-colors disabled:opacity-50"
                        @click="removeFavorite(favorite.id)"
                        :disabled="isRemoving"
                      >
                        {{ isRemoving ? 'Removing...' : 'Remove' }}
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
import { favoritesService } from '@/lib/api'
import type { FavoriteDto } from '@/lib/api/types'
import { toast } from 'vue-sonner'

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
    toast.error('Failed to load favorites')
  } finally {
    isLoading.value = false
  }
}

const removeFavorite = async (favoriteId: number) => {
  if (!confirm('Are you sure you want to remove this book from your favorites?')) {
    return
  }

  try {
    isRemoving.value = true
    await favoritesService.removeFavorite(favoriteId)
    toast.success('Book removed from favorites!')
    // Refresh the favorites list
    await fetchFavorites()
  } catch (error) {
    console.error('Failed to remove favorite:', error)
    toast.error('Failed to remove favorite. Please try again.')
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
