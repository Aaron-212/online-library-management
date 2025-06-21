<template>
  <div class="border rounded-lg p-4 hover:shadow-md transition-shadow">
    <div class="flex justify-between items-start mb-3">
      <div class="flex-1">
        <div class="text-sm text-gray-500 mb-1">Copy ID: {{ copy.id }}</div>
        <div class="font-mono text-sm bg-gray-100 px-2 py-1 rounded">
          {{ copy.barcode }}
        </div>
      </div>
      <Badge :class="statusBadgeColor">
        {{ formatStatus(copy.status) }}
      </Badge>
    </div>

    <div class="space-y-2 text-sm">
      <div v-if="copy.purchasePrice" class="flex justify-between">
        <span class="text-gray-600">Purchase Price:</span>
        <span class="font-medium">${{ copy.purchasePrice.toFixed(2) }}</span>
      </div>
      
      <div v-if="copy.purchaseTime" class="flex justify-between">
        <span class="text-gray-600">Purchase Date:</span>
        <span>{{ formatDate(copy.purchaseTime) }}</span>
      </div>
      
      <div v-if="copy.lastMaintenance" class="flex justify-between">
        <span class="text-gray-600">Last Maintenance:</span>
        <span>{{ formatDate(copy.lastMaintenance) }}</span>
      </div>
      
      <div class="flex justify-between">
        <span class="text-gray-600">Created:</span>
        <span>{{ formatDate(copy.createTime) }}</span>
      </div>
    </div>

    <div v-if="showActions" class="flex gap-2 mt-4">
      <Button
        v-if="copy.status === 'AVAILABLE'"
        size="sm"
        variant="outline"
        @click="$emit('borrow', copy)"
      >
        <BookOpen class="h-4 w-4 mr-1" />
        Borrow
      </Button>
      
      <Button
        v-if="copy.status === 'BORROWED'"
        size="sm"
        variant="outline"
        @click="$emit('return', copy)"
      >
        <ArrowLeftCircle class="h-4 w-4 mr-1" />
        Return
      </Button>
      
      <Button
        v-if="copy.status === 'AVAILABLE'"
        size="sm"
        variant="outline"
        @click="$emit('maintenance', copy)"
      >
        <Wrench class="h-4 w-4 mr-1" />
        Maintenance
      </Button>
      
      <Button
        v-if="isAdmin"
        size="sm"
        variant="outline"
        @click="$emit('edit', copy)"
      >
        <Edit class="h-4 w-4 mr-1" />
        Edit
      </Button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { BookOpen, ArrowLeftCircle, Wrench, Edit } from 'lucide-vue-next'
import { bookCopiesService } from '@/lib/api'
import type { BookCopy } from '@/lib/api/types'
import { useAuthStore } from '@/stores/auth'

interface Props {
  copy: BookCopy
  showActions?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showActions: true
})

defineEmits<{
  borrow: [copy: BookCopy]
  return: [copy: BookCopy]
  maintenance: [copy: BookCopy]
  edit: [copy: BookCopy]
}>()

const authStore = useAuthStore()

const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

const statusBadgeColor = computed(() => 
  bookCopiesService.getStatusBadgeColor(props.copy.status)
)

const formatStatus = (status: string) => 
  bookCopiesService.formatStatus(status)

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}
</script>