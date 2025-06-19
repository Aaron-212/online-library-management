<script setup lang="ts">
import { computed } from 'vue'
import { cn } from '@/lib/utils'

interface DialogHeaderProps {
  class?: string
}

const props = defineProps<DialogHeaderProps>()

const headerClass = computed(() => cn(
  'flex flex-col space-y-1.5 text-center sm:text-left mb-4',
  props.class
))
</script>

<template>
  <div :class="headerClass">
    <slot />
  </div>
</template>