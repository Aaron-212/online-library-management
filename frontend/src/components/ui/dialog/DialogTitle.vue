<script setup lang="ts">
import { computed } from 'vue'
import { cn } from '@/lib/utils'

interface DialogTitleProps {
  class?: string
}

const props = defineProps<DialogTitleProps>()

const titleClass = computed(() => cn(
  'text-lg font-semibold leading-none tracking-tight',
  props.class
))
</script>

<template>
  <h2 :class="titleClass">
    <slot />
  </h2>
</template>