<script setup lang="ts">
import { computed } from 'vue'
import { cn } from '@/lib/utils'

interface DialogDescriptionProps {
  class?: string
}

const props = defineProps<DialogDescriptionProps>()

const descriptionClass = computed(() => cn(
  'text-sm text-muted-foreground',
  props.class
))
</script>

<template>
  <p :class="descriptionClass">
    <slot />
  </p>
</template>