<template>
  <div class="space-y-4">
    <!-- Header with filters and stats -->
    <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
      <div>
        <h3 class="text-lg font-semibold">Book Copies ({{ filteredCopies.length }})</h3>
        <div class="flex gap-4 text-sm text-gray-600 mt-1">
          <span>Available: {{ availableCount }}</span>
          <span>Borrowed: {{ borrowedCount }}</span>
          <span>Maintenance: {{ maintenanceCount }}</span>
        </div>
      </div>
      
      <div class="flex gap-2">
        <!-- Status filter -->
        <Select v-model="statusFilter">
          <SelectTrigger class="w-40">
            <SelectValue placeholder="Filter by status" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Statuses</SelectItem>
            <SelectItem value="AVAILABLE">Available</SelectItem>
            <SelectItem value="BORROWED">Borrowed</SelectItem>
            <SelectItem value="MAINTENANCE">Maintenance</SelectItem>
            <SelectItem value="SCRAPPED">Scrapped</SelectItem>
            <SelectItem value="DISCARDED">Discarded</SelectItem>
          </SelectContent>
        </Select>
        
        <!-- Sort options -->
        <Select v-model="sortBy">
          <SelectTrigger class="w-40">
            <SelectValue placeholder="Sort by" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="id">Copy ID</SelectItem>
            <SelectItem value="barcode">Barcode</SelectItem>
            <SelectItem value="status">Status</SelectItem>
            <SelectItem value="createTime">Date Created</SelectItem>
            <SelectItem value="purchaseTime">Purchase Date</SelectItem>
          </SelectContent>
        </Select>
        
        <Button
          variant="outline"
          size="sm"
          @click="sortOrder = sortOrder === 'asc' ? 'desc' : 'asc'"
        >
          <ArrowUpDown class="h-4 w-4" />
        </Button>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div v-for="i in 6" :key="i" class="border rounded-lg p-4">
        <Skeleton class="h-4 w-20 mb-2" />
        <Skeleton class="h-6 w-32 mb-3" />
        <Skeleton class="h-4 w-full mb-2" />
        <Skeleton class="h-4 w-3/4" />
      </div>
    </div>

    <!-- Empty state -->
    <div v-else-if="filteredCopies.length === 0" class="text-center py-8">
      <BookX class="h-16 w-16 text-gray-400 mx-auto mb-4" />
      <h3 class="text-lg font-medium text-gray-900 mb-2">No book copies found</h3>
      <p class="text-gray-500">
        {{ statusFilter === 'all' ? 'This book has no copies yet.' : `No copies with status "${formatStatus(statusFilter)}".` }}
      </p>
    </div>

    <!-- Copies grid -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <BookCopyCard
        v-for="copy in filteredCopies"
        :key="copy.id"
        :copy="copy"
        :show-actions="showActions"
        :is-user-borrowed="userBorrowedCopies.has(copy.id)"
        @borrow="$emit('borrow', $event)"
        @return="$emit('return', $event)"
        @maintenance="$emit('maintenance', $event)"
        @edit="$emit('edit', $event)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { ArrowUpDown, BookX } from 'lucide-vue-next'
import BookCopyCard from './BookCopyCard.vue'
import { bookCopiesService } from '@/lib/api'
import type { BookCopy, BookCopyStatus } from '@/lib/api/types'

interface Props {
  copies: BookCopy[]
  loading?: boolean
  showActions?: boolean
  userBorrowedCopies?: Set<number>
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  showActions: true,
  userBorrowedCopies: () => new Set()
})

defineEmits<{
  borrow: [copy: BookCopy]
  return: [copy: BookCopy]
  maintenance: [copy: BookCopy]
  edit: [copy: BookCopy]
}>()

const statusFilter = ref<string>('all')
const sortBy = ref<string>('id')
const sortOrder = ref<'asc' | 'desc'>('asc')

const formatStatus = (status: string) => 
  bookCopiesService.formatStatus(status)

// Computed statistics
const availableCount = computed(() => 
  props.copies.filter(copy => copy.status === 'AVAILABLE').length
)

const borrowedCount = computed(() => 
  props.copies.filter(copy => copy.status === 'BORROWED').length
)

const maintenanceCount = computed(() => 
  props.copies.filter(copy => copy.status === 'MAINTENANCE').length
)

// Filtered and sorted copies
const filteredCopies = computed(() => {
  let filtered = props.copies

  // Apply status filter
  if (statusFilter.value !== 'all') {
    filtered = filtered.filter(copy => copy.status === statusFilter.value)
  }

  // Apply sorting
  const sorted = [...filtered].sort((a, b) => {
    let aValue: any
    let bValue: any

    switch (sortBy.value) {
      case 'id':
        aValue = a.id
        bValue = b.id
        break
      case 'barcode':
        aValue = a.barcode
        bValue = b.barcode
        break
      case 'status':
        aValue = a.status
        bValue = b.status
        break
      case 'createTime':
        aValue = new Date(a.createTime)
        bValue = new Date(b.createTime)
        break
      case 'purchaseTime':
        aValue = a.purchaseTime ? new Date(a.purchaseTime) : new Date(0)
        bValue = b.purchaseTime ? new Date(b.purchaseTime) : new Date(0)
        break
      default:
        aValue = a.id
        bValue = b.id
    }

    if (aValue < bValue) return sortOrder.value === 'asc' ? -1 : 1
    if (aValue > bValue) return sortOrder.value === 'asc' ? 1 : -1
    return 0
  })

  return sorted
})
</script>