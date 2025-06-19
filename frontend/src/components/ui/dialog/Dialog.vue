<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { cn } from '@/lib/utils'

interface DialogProps {
  open?: boolean
  onOpenChange?: (open: boolean) => void
}

const props = withDefaults(defineProps<DialogProps>(), {
  open: false
})

const emit = defineEmits<{
  'update:open': [open: boolean]
}>()

const isOpen = ref(props.open)

watch(() => props.open, (newValue) => {
  isOpen.value = newValue
})

watch(isOpen, (newValue) => {
  emit('update:open', newValue)
  if (props.onOpenChange) {
    props.onOpenChange(newValue)
  }
})

const closeDialog = () => {
  isOpen.value = false
}

// Close on escape key
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && isOpen.value) {
    closeDialog()
  }
}

// Close on overlay click
const handleOverlayClick = (event: MouseEvent) => {
  if (event.target === event.currentTarget) {
    closeDialog()
  }
}

// Manage body scroll
watch(isOpen, (newValue) => {
  if (newValue) {
    document.body.style.overflow = 'hidden'
    document.addEventListener('keydown', handleKeydown)
  } else {
    document.body.style.overflow = ''
    document.removeEventListener('keydown', handleKeydown)
  }
})
</script>

<template>
  <Teleport to="body">
    <Transition
      enter-active-class="transition-opacity duration-300"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition-opacity duration-300"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="isOpen"
        class="fixed inset-0 z-50 bg-black/80 flex items-center justify-center p-4"
        @click="handleOverlayClick"
      >
        <slot :close="closeDialog" />
      </div>
    </Transition>
  </Teleport>
</template>