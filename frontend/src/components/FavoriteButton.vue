<template>
  <button
    :class="buttonClasses"
    @click="handleClick"
    :disabled="isLoading || isInitializing"
    :title="isFavorite ? 'Remove from favorites' : 'Add to favorites'"
  >
    <Heart
      :class="heartClasses"
      :fill="isFavorite ? 'currentColor' : 'none'"
    />
    <span v-if="showText" class="ml-1 text-sm">
      {{ isFavorite ? 'Favorited' : 'Add to favorites' }}
    </span>
  </button>
</template>

<script lang="ts" setup>
import { ref, computed, watch, onMounted } from 'vue'
import { Heart } from 'lucide-vue-next'
import { favoritesService } from '@/lib/api'
import { toast } from 'vue-sonner'

interface Props {
  bookId: number
  initialIsFavorite?: boolean
  showText?: boolean
  variant?: 'default' | 'ghost' | 'outline'
  size?: 'sm' | 'md' | 'lg'
}

const props = withDefaults(defineProps<Props>(), {
  initialIsFavorite: false,
  showText: false,
  variant: 'ghost',
  size: 'md'
})

const emit = defineEmits<{
  favoriteChanged: [isFavorite: boolean]
}>()

const isFavorite = ref(props.initialIsFavorite)
const isLoading = ref(false)
const isInitializing = ref(false)

// Watch for prop changes
watch(() => props.initialIsFavorite, (newValue) => {
  isFavorite.value = newValue
})

const buttonClasses = computed(() => {
  const baseClasses = 'inline-flex items-center justify-center rounded-md transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50'
  
  const sizeClasses = {
    sm: 'h-8 px-2',
    md: 'h-9 px-3',
    lg: 'h-10 px-4'
  }
  
  const variantClasses = {
    default: 'bg-primary text-primary-foreground hover:bg-primary/90',
    ghost: 'hover:bg-accent hover:text-accent-foreground',
    outline: 'border border-input bg-background hover:bg-accent hover:text-accent-foreground'
  }
  
  return `${baseClasses} ${sizeClasses[props.size]} ${variantClasses[props.variant]}`
})

const heartClasses = computed(() => {
  const baseClasses = 'transition-colors'
  const sizeClasses = {
    sm: 'h-4 w-4',
    md: 'h-5 w-5',
    lg: 'h-6 w-6'
  }
  
  const colorClasses = isFavorite.value 
    ? 'text-red-500' 
    : 'text-muted-foreground hover:text-red-500'
  
  return `${baseClasses} ${sizeClasses[props.size]} ${colorClasses}`
})

const handleClick = async () => {
  if (isLoading.value || isInitializing.value) return
  
  try {
    isLoading.value = true
    
    if (isFavorite.value) {
      // Remove from favorites
      await favoritesService.removeFavoriteByBook(props.bookId)
      isFavorite.value = false
      toast.success('Removed from favorites')
    } else {
      // Add to favorites
      await favoritesService.addFavorite({ bookId: props.bookId })
      isFavorite.value = true
      toast.success('Added to favorites')
    }
    
    emit('favoriteChanged', isFavorite.value)
  } catch (error) {
    console.error('Failed to toggle favorite:', error)
    toast.error(isFavorite.value ? 'Failed to remove from favorites' : 'Failed to add to favorites')
  } finally {
    isLoading.value = false
  }
}

// Method to check favorite status from server
const checkFavoriteStatus = async () => {
  try {
    isInitializing.value = true
    const status = await favoritesService.checkIsFavorite(props.bookId)
    isFavorite.value = status
  } catch (error) {
    console.error('Failed to check favorite status:', error)
    // Don't show error toast for this as it's not a user-initiated action
  } finally {
    isInitializing.value = false
  }
}

// Automatically check favorite status when component mounts
onMounted(() => {
  // Only check if not explicitly provided via props
  if (props.initialIsFavorite === false) {
    checkFavoriteStatus()
  }
})

// Expose methods for parent components
defineExpose({
  checkFavoriteStatus,
  isFavorite: computed(() => isFavorite.value)
})
</script>