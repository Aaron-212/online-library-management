<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import {
  ArrowLeft,
  BookOpen,
  Building,
  Copy,
  Edit,
  MessageSquare,
  Package,
  Share,
  Star,
  Tag,
  User,
} from 'lucide-vue-next'
import CommentList from '@/components/CommentList.vue'
import FavoriteButton from '@/components/FavoriteButton.vue'
import { booksService, borrowService, commentsService, usersService } from '@/lib/api'
import type { Book, Comment } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

// Data
const book = ref<Book | null>(null)
const comments = ref<Comment[]>([])
const isLoading = ref(false)
const isCommentsLoading = ref(false)
const isBorrowing = ref(false)
const imageLoadError = ref(false)

// Props
const bookId = computed(() => Number(route.params.id))

// Computed
const isAdmin = computed(() => {
  // This should be replaced with actual role checking when user roles are available
  return authStore.isAuthenticated
})

const averageRating = computed(() => {
  if (comments.value.length === 0) return 0
  const total = comments.value.reduce((sum, comment) => sum + comment.rating, 0)
  return total / comments.value.length
})

const availabilityStatus = computed(() => {
  if (!book.value) return null
  if (book.value.availableQuantity === 0) return 'unavailable'
  if (book.value.availableQuantity <= 2) return 'limited'
  return 'available'
})

const availabilityText = computed(() => {
  if (!book.value) return ''
  if (book.value.availableQuantity === 0) return t('bookDetail.availability.notAvailable')
  if (book.value.availableQuantity === 1) return t('bookDetail.availability.singleCopy')
  return t('bookDetail.availability.multipleCopies', { count: book.value.availableQuantity })
})

// Methods
const loadBook = async () => {
  try {
    isLoading.value = true
    imageLoadError.value = false // Reset image error state
    book.value = await booksService.getById(bookId.value)
  } catch (error) {
    console.error('Error loading book:', error)
    toast.error(t('bookDetail.messages.loadError'))
    router.push('/books')
  } finally {
    isLoading.value = false
  }
}

const loadComments = async () => {
  try {
    isCommentsLoading.value = true
    const response = await commentsService.getByBookId(bookId.value, { page: 0, size: 50 })
    comments.value = response.content
  } catch (error) {
    console.error('Error loading comments:', error)
  } finally {
    isCommentsLoading.value = false
  }
}

const handleBorrow = async () => {
  if (!authStore.isAuthenticated) {
    toast.error(t('bookDetail.messages.borrowLoginRequired'))
    router.push('/login')
    return
  }

  if (!book.value || book.value.availableQuantity === 0) {
    toast.error(t('bookDetail.messages.borrowNotAvailable'))
    return
  }

  try {
    isBorrowing.value = true

    // Get current user data to get the user ID
    const currentUser = await usersService.getCurrentUser()

    // Use the new convenience method to borrow by book ID
    await borrowService.borrowBookByBookId({
      userId: currentUser.id,
      bookId: bookId.value,
    })
    toast.success(t('bookDetail.messages.borrowSuccess'))
    // Reload book data to update availability
    await loadBook()
  } catch (error) {
    console.error('Error borrowing book:', error)
    // Use the detailed error message from the API if available to give users clearer feedback
    const errMsg = (error as any)?.message || t('bookDetail.messages.borrowError')
    toast.error(errMsg)
  } finally {
    isBorrowing.value = false
  }
}

const handleEdit = () => {
  router.push(`/admin/books/${bookId.value}/edit`)
}

const handleViewCopies = () => {
  router.push(`/books/${bookId.value}/copies`)
}

const handleShare = async () => {
  try {
    await navigator.share({
      title: book.value?.title,
      text: `Check out this book: ${book.value?.title}`,
      url: window.location.href,
    })
  } catch (error) {
    // Fallback to copying to clipboard
    await navigator.clipboard.writeText(window.location.href)
    toast.success(t('bookDetail.messages.shareSuccess'))
  }
}

const goBack = () => {
  router.back()
}

const renderStars = (rating: number) => {
  const stars = []
  for (let i = 1; i <= 5; i++) {
    stars.push(i <= rating)
  }
  return stars
}

const handleFavoriteChanged = (isFavorite: boolean) => {
  // The FavoriteButton component already handles toast notifications
  // We could add additional logic here if needed, such as updating
  // analytics or refreshing related data
  console.log(`Book ${bookId.value} favorite status changed to: ${isFavorite}`)
}

// Lifecycle
onMounted(() => {
  loadBook()
  loadComments()
})
</script>

<template>
  <div class="book-detail space-y-6">
    <!-- Back Button -->
    <Button variant="ghost" @click="goBack" class="mb-4">
      <ArrowLeft class="h-4 w-4 mr-2" />
      {{ t('bookDetail.backToBooks') }}
    </Button>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-8">{{ t('bookDetail.loading') }}</div>

    <!-- Book Details -->
    <template v-else-if="book">
      <!-- Book Header -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Book Cover -->
        <div class="lg:col-span-1">
          <Card class="overflow-hidden">
            <div class="aspect-[3/4] bg-muted flex items-center justify-center relative">
              <img
                v-if="book.coverURL && !imageLoadError"
                :src="book.coverURL"
                :alt="t('bookDetail.cover.altText', { title: book.title })"
                class="object-cover w-full h-full"
                @error="imageLoadError = true"
              />
              <div v-else class="text-muted-foreground p-8 text-center">
                <BookOpen class="h-16 w-16 mx-auto mb-4" />
                <p>{{ t('bookDetail.cover.noImageAvailable') }}</p>
              </div>

              <!-- Availability Badge -->
              <div v-if="availabilityStatus" class="absolute top-4 right-4">
                <Badge
                  :variant="
                    availabilityStatus === 'available'
                      ? 'success'
                      : availabilityStatus === 'limited'
                        ? 'secondary'
                        : 'destructive'
                  "
                >
                  {{
                    availabilityStatus === 'available'
                      ? t('bookDetail.availability.available')
                      : availabilityStatus === 'limited'
                        ? t('bookDetail.availability.limited')
                        : t('bookDetail.availability.outOfStock')
                  }}
                </Badge>
              </div>
            </div>
          </Card>
        </div>

        <!-- Book Information -->
        <div class="lg:col-span-2 space-y-6">
          <div>
            <h1 class="text-3xl font-bold mb-2">{{ book.title }}</h1>

            <!-- Authors -->
            <div class="flex items-center gap-2 text-lg text-muted-foreground mb-4">
              <User class="h-5 w-5" />
              <span>{{ book.authors.map((a) => a.name).join(', ') }}</span>
            </div>

            <!-- Rating -->
            <div v-if="comments.length > 0" class="flex items-center gap-2 mb-4">
              <div class="flex items-center">
                <Star
                  v-for="(filled, index) in renderStars(Math.round(averageRating))"
                  :key="index"
                  :class="filled ? 'text-yellow-400 fill-current' : 'text-gray-300'"
                  class="h-5 w-5"
                />
              </div>
              <span class="text-sm text-muted-foreground">
                {{ averageRating.toFixed(1) }} ({{ comments.length }}
                {{
                  comments.length === 1
                    ? t('bookDetail.rating.singleReview')
                    : t('bookDetail.rating.multipleReviews', { count: comments.length })
                }})
              </span>
            </div>

            <!-- Availability -->
            <div class="mb-6">
              <p class="text-lg font-semibold mb-2">{{ availabilityText }}</p>
              <p class="text-sm text-muted-foreground">
                {{
                  t('bookDetail.availability.totalCopies', {
                    count: book.totalQuantity,
                  })
                }}
              </p>
            </div>

            <!-- Action Buttons -->
            <div class="flex flex-wrap gap-3 mb-6">
              <Button
                @click="handleBorrow"
                :disabled="book.availableQuantity === 0 || isBorrowing"
                size="lg"
              >
                <BookOpen class="h-4 w-4 mr-2" />
                {{
                  isBorrowing ? t('bookDetail.buttons.borrowing') : t('bookDetail.buttons.borrow')
                }}
              </Button>

              <FavoriteButton
                v-if="authStore.isAuthenticated"
                :book-id="bookId"
                show-text
                variant="outline"
                size="lg"
                @favorite-changed="handleFavoriteChanged"
              />

              <Button variant="outline" @click="handleViewCopies" size="lg">
                <Copy class="h-4 w-4 mr-2" />
                {{ t('bookDetail.buttons.viewCopies') }}
              </Button>

              <Button variant="outline" @click="handleShare" size="lg">
                <Share class="h-4 w-4 mr-2" />
                {{ t('bookDetail.buttons.share') }}
              </Button>

              <Button v-if="isAdmin" variant="outline" @click="handleEdit" size="lg">
                <Edit class="h-4 w-4 mr-2" />
                {{ t('bookDetail.buttons.edit') }}
              </Button>
            </div>
          </div>

          <!-- Book Details Grid -->
          <Card>
            <CardHeader>
              <CardTitle>{{ t('bookDetail.details.title') }}</CardTitle>
            </CardHeader>
            <CardContent class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-3">
                <div class="flex items-center gap-2">
                  <Package class="h-4 w-4 text-muted-foreground" />
                  <span class="text-sm font-medium">{{ t('bookDetail.details.isbn') }}</span>
                  <span class="text-sm">{{ book.isbn }}</span>
                </div>

                <div class="flex items-center gap-2">
                  <Tag class="h-4 w-4 text-muted-foreground" />
                  <span class="text-sm font-medium">{{ t('bookDetail.details.category') }}</span>
                  <span class="text-sm">{{
                    book.indexCategory?.name || t('bookDetail.details.uncategorized')
                  }}</span>
                </div>

                <div class="flex items-center gap-2">
                  <Building class="h-4 w-4 text-muted-foreground" />
                  <span class="text-sm font-medium">{{ t('bookDetail.details.publishers') }}</span>
                  <span class="text-sm">{{ book.publishers.map((p) => p.name).join(', ') }}</span>
                </div>
              </div>

              <div class="space-y-3">
                <div class="flex items-center gap-2">
                  <span class="text-sm font-medium">{{ t('bookDetail.details.language') }}</span>
                  <span class="text-sm">{{ book.language }}</span>
                </div>

                <div class="flex items-center gap-2">
                  <span class="text-sm font-medium">{{ t('bookDetail.details.available') }}</span>
                  <span class="text-sm"
                    >{{ book.availableQuantity }} / {{ book.totalQuantity }}</span
                  >
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Description -->
          <Card v-if="book.description">
            <CardHeader>
              <CardTitle>{{ t('bookDetail.description.title') }}</CardTitle>
            </CardHeader>
            <CardContent>
              <p class="text-sm leading-relaxed">{{ book.description }}</p>
            </CardContent>
          </Card>
        </div>
      </div>

      <Separator class="my-8" />

      <!-- Comments Section -->
      <Card>
        <CardHeader>
          <CardTitle class="flex items-center gap-2">
            <MessageSquare class="h-5 w-5" />
            {{ t('bookDetail.comments.title') }}
          </CardTitle>
          <CardDescription>{{ t('bookDetail.comments.description') }}</CardDescription>
        </CardHeader>
        <CardContent>
          <CommentList
            :comments="comments"
            :book-id="bookId"
            :loading="isCommentsLoading"
            @comment-added="loadComments"
            @comment-updated="loadComments"
            @comment-deleted="loadComments"
          />
        </CardContent>
      </Card>
    </template>

    <!-- Book Not Found -->
    <div v-else class="text-center py-8">
      <h2 class="text-2xl font-bold mb-4">{{ t('bookDetail.notFound.title') }}</h2>
      <p class="text-muted-foreground mb-4">{{ t('bookDetail.notFound.description') }}</p>
      <Button @click="router.push('/books')">
        <ArrowLeft class="h-4 w-4 mr-2" />
        {{ t('bookDetail.notFound.backButton') }}
      </Button>
    </div>
  </div>
</template>

<style scoped>
.book-detail {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
