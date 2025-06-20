<template>
  <div class="comment-list space-y-4">
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-4">Loading comments...</div>

    <!-- Add Comment Button -->
    <div v-if="canAddComment" class="flex justify-center">
      <Button variant="outline" @click="showAddComment = true">
        <Plus class="h-4 w-4 mr-2" />
        Add Review
      </Button>
    </div>

    <!-- Add Comment Form -->
    <Card v-if="showAddComment" class="p-4">
      <div class="space-y-4">
        <div>
          <Label for="rating">Rating</Label>
          <div class="flex items-center gap-1 mt-1">
            <button
              v-for="star in renderStars(5, true, newRating)"
              :key="star.index"
              class="p-1 hover:scale-110 transition-transform"
              @click="newRating = star.index"
            >
              <Star
                :class="star.filled ? 'text-yellow-400 fill-current' : 'text-gray-300'"
                class="h-5 w-5"
              />
            </button>
            <span class="ml-2 text-sm text-muted-foreground">{{ newRating }}/5</span>
          </div>
        </div>

        <div>
          <Label for="comment">Your Review</Label>
          <textarea
            id="comment"
            v-model="newComment"
            class="w-full mt-1 p-3 border rounded-md resize-none"
            placeholder="Share your thoughts about this book..."
            rows="4"
          />
        </div>

        <div class="flex gap-2">
          <Button :disabled="isSubmitting || !newComment.trim()" @click="handleAddComment">
            {{ isSubmitting ? 'Submitting...' : 'Submit Review' }}
          </Button>
          <Button variant="outline" @click="showAddComment = false"> Cancel </Button>
        </div>
      </div>
    </Card>

    <!-- Comments List -->
    <div v-if="!loading && sortedComments.length > 0" class="space-y-4">
      <div v-for="comment in sortedComments" :key="comment.id">
        <Card class="p-4">
          <!-- Comment Header -->
          <div class="flex justify-between items-start mb-3">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                <span class="text-sm font-medium">
                  {{ comment.user.username.charAt(0).toUpperCase() }}
                </span>
              </div>
              <div>
                <p class="font-medium text-sm">{{ comment.user.username }}</p>
                <p class="text-xs text-muted-foreground">{{ formatTime(comment.commentDate) }}</p>
              </div>
            </div>

            <!-- Action Buttons -->
            <div v-if="canEditComment(comment)" class="flex gap-1">
              <Button
                v-if="editingCommentId !== comment.id"
                size="sm"
                variant="ghost"
                @click="startEdit(comment)"
              >
                <Edit class="h-4 w-4" />
              </Button>
              <Button size="sm" variant="ghost" @click="handleDeleteComment(comment.id)">
                <Trash2 class="h-4 w-4" />
              </Button>
            </div>
          </div>

          <!-- Rating -->
          <div class="flex items-center gap-2 mb-2">
            <div class="flex items-center">
              <Star
                v-for="star in renderStars(comment.rating)"
                :key="star.index"
                :class="star.filled ? 'text-yellow-400 fill-current' : 'text-gray-300'"
                class="h-4 w-4"
              />
            </div>
            <span class="text-sm text-muted-foreground">{{ comment.rating }}/5</span>
          </div>

          <!-- Comment Content or Edit Form -->
          <div v-if="editingCommentId === comment.id" class="space-y-3">
            <!-- Edit Rating -->
            <div>
              <Label>Rating</Label>
              <div class="flex items-center gap-1 mt-1">
                <button
                  v-for="star in renderStars(5, true, editRating)"
                  :key="star.index"
                  class="p-1 hover:scale-110 transition-transform"
                  @click="editRating = star.index"
                >
                  <Star
                    :class="star.filled ? 'text-yellow-400 fill-current' : 'text-gray-300'"
                    class="h-4 w-4"
                  />
                </button>
                <span class="ml-2 text-sm text-muted-foreground">{{ editRating }}/5</span>
              </div>
            </div>

            <!-- Edit Content -->
            <div>
              <textarea
                v-model="editComment"
                class="w-full p-3 border rounded-md resize-none"
                rows="3"
              />
            </div>

            <!-- Edit Actions -->
            <div class="flex gap-2">
              <Button size="sm" @click="handleUpdateComment(comment.id)"> Save Changes </Button>
              <Button size="sm" variant="outline" @click="cancelEdit"> Cancel </Button>
            </div>
          </div>

          <!-- Display Content -->
          <div v-else>
            <p class="text-sm leading-relaxed">{{ comment.content }}</p>
          </div>
        </Card>
      </div>
    </div>

    <!-- Empty State -->
    <div
      v-if="!loading && sortedComments.length === 0"
      class="flex flex-col items-center justify-center py-12 text-center"
    >
      <div class="w-12 h-12 rounded-full bg-muted flex items-center justify-center mb-4">
        <MessageCircle class="w-6 h-6 text-muted-foreground" />
      </div>
      <p class="text-sm text-muted-foreground">No reviews yet</p>
      <p class="text-xs text-muted-foreground mt-1">Be the first to share your thoughts!</p>

      <Button
        v-if="authStore.isAuthenticated && !showAddComment"
        class="mt-4"
        variant="outline"
        @click="showAddComment = true"
      >
        <Plus class="h-4 w-4 mr-2" />
        Write a Review
      </Button>
    </div>

    <!-- Not Logged In Message -->
    <div v-if="!authStore.isAuthenticated && !showAddComment" class="text-center py-4">
      <p class="text-sm text-muted-foreground">
        Please
        <router-link class="text-primary hover:underline" to="/login">log in</router-link>
        to add a review
      </p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'
import { Edit, MessageCircle, Plus, Star, Trash2 } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Label } from '@/components/ui/label'
import { commentsService } from '@/lib/api'
import type { Comment } from '@/lib/api/types'
import { toast } from 'vue-sonner'

interface Props {
  comments: Comment[]
  bookId: number
  loading?: boolean
}

interface Emits {
  (e: 'comment-added'): void

  (e: 'comment-updated'): void

  (e: 'comment-deleted'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const authStore = useAuthStore()

// Data
const showAddComment = ref(false)
const newComment = ref('')
const newRating = ref(5)
const isSubmitting = ref(false)

// Editing state
const editingCommentId = ref<number | null>(null)
const editComment = ref('')
const editRating = ref(5)

// Computed
const sortedComments = computed(() => {
  return [...props.comments].sort(
    (a, b) => new Date(b.commentDate).getTime() - new Date(a.commentDate).getTime(),
  )
})

const canAddComment = computed(() => {
  return authStore.isAuthenticated && !showAddComment.value
})

// Methods
const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (minutes < 1) return 'Just now'
  if (minutes < 60) return `${minutes} minute${minutes === 1 ? '' : 's'} ago`
  if (hours < 24) return `${hours} hour${hours === 1 ? '' : 's'} ago`
  if (days < 7) return `${days} day${days === 1 ? '' : 's'} ago`

  return date.toLocaleDateString()
}

const renderStars = (rating: number, interactive = false, currentRating = 0) => {
  const stars = []
  for (let i = 1; i <= 5; i++) {
    stars.push({
      index: i,
      filled: i <= (interactive ? currentRating : rating),
    })
  }
  return stars
}

const handleAddComment = async () => {
  if (!newComment.value.trim()) {
    toast.error('Please enter a comment')
    return
  }

  try {
    isSubmitting.value = true
    await commentsService.create({
      bookId: props.bookId,
      content: newComment.value.trim(),
      rating: newRating.value,
    })

    // Reset form
    newComment.value = ''
    newRating.value = 5
    showAddComment.value = false

    toast.success('Comment added successfully!')
    emit('comment-added')
  } catch (error) {
    console.error('Error adding comment:', error)
    toast.error('Failed to add comment')
  } finally {
    isSubmitting.value = false
  }
}

const startEdit = (comment: Comment) => {
  editingCommentId.value = comment.id
  editComment.value = comment.content
  editRating.value = comment.rating
}

const cancelEdit = () => {
  editingCommentId.value = null
  editComment.value = ''
  editRating.value = 5
}

const handleUpdateComment = async (commentId: number) => {
  if (!editComment.value.trim()) {
    toast.error('Please enter a comment')
    return
  }

  try {
    await commentsService.update(commentId, {
      content: editComment.value.trim(),
      rating: editRating.value,
    })

    cancelEdit()
    toast.success('Comment updated successfully!')
    emit('comment-updated')
  } catch (error) {
    console.error('Error updating comment:', error)
    toast.error('Failed to update comment')
  }
}

const handleDeleteComment = async (commentId: number) => {
  if (!confirm('Are you sure you want to delete this comment?')) {
    return
  }

  try {
    await commentsService.delete(commentId)
    toast.success('Comment deleted successfully!')
    emit('comment-deleted')
  } catch (error) {
    console.error('Error deleting comment:', error)
    toast.error('Failed to delete comment')
  }
}

const canEditComment = (comment: Comment) => {
  return authStore.user?.username === comment.user.username || false
}
</script>

<style scoped>
textarea {
  background: hsl(var(--background));
  border: 1px solid hsl(var(--border));
  color: hsl(var(--foreground));
}

textarea:focus {
  outline: none;
  border-color: hsl(var(--ring));
  box-shadow: 0 0 0 2px hsl(var(--ring) / 0.2);
}
</style>
