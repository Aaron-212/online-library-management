<script lang="ts" setup>
import {
  TooltipRoot,
  type TooltipRootEmits,
  type TooltipRootProps,
  useForwardPropsEmits,
} from 'reka-ui'

const props = defineProps<TooltipRootProps>()
const emits = defineEmits<TooltipRootEmits>()

const forwarded = useForwardPropsEmits(props, emits)
</script>

<template>
  <TooltipRoot data-slot="tooltip" v-bind="forwarded">
    <slot />
  </TooltipRoot>
</template>
