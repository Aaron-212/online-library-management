<script setup lang="ts">
import { computed } from 'vue'
import { cn } from '@/lib/utils'

interface DialogContentProps {
  class?: string
}

const props = defineProps<DialogContentProps>()

const contentClass = computed(() => cn(
  'relative bg-background p-6 shadow-lg rounded-lg border max-w-lg w-full mx-auto',
  'animate-in zoom-in-95 duration-200',
  props.class
))
</script>

<template>
  <div :class="contentClass" @click.stop>
    <slot />
  </div>
</template>

<style scoped>
@keyframes zoom-in-95 {
  0% {
    opacity: 0;
    transform: scale(0.95);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

.animate-in {
  animation-fill-mode: both;
}

.zoom-in-95 {
  animation-name: zoom-in-95;
}

.duration-200 {
  animation-duration: 200ms;
}
</style>