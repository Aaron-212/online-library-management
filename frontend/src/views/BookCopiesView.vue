<template>
  <div class="container mx-auto px-4 py-8">
    <!-- Header -->
    <div class="mb-6">
      <nav class="flex items-center gap-2 text-sm text-gray-600 mb-4">
        <router-link to="/books" class="hover:text-gray-900">Books</router-link>
        <span>/</span>
        <router-link 
          v-if="book" 
          :to="`/books/${book.id}`" 
          class="hover:text-gray-900"
        >
          {{ book.title }}
        </router-link>
        <span>/</span>
        <span class="text-gray-900">Copies</span>
      </nav>
      
      <div v-if="book" class="flex items-start justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900 mb-2">Book Copies</h1>
          <div class="flex items-center gap-4 text-sm text-gray-600">
            <span>{{ book.title }}</span>
            <Badge variant="outline">{{ book.isbn }}</Badge>
          </div>
        </div>
        
        <div v-if="isAdmin" class="flex gap-2">
          <Button @click="showCreateDialog = true" class="gap-2">
            <Plus class="h-4 w-4" />
            Add Copy
          </Button>
        </div>
      </div>
    </div>

    <!-- Loading book details -->
    <div v-if="loadingBook" class="flex justify-center py-8">
      <div class="text-center">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900 mx-auto mb-4"></div>
        <p class="text-gray-600">Loading book details...</p>
      </div>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="text-center py-8">
      <AlertCircle class="h-16 w-16 text-red-400 mx-auto mb-4" />
      <h3 class="text-lg font-medium text-gray-900 mb-2">Error Loading Book</h3>
      <p class="text-gray-500 mb-4">{{ error }}</p>
      <Button @click="loadBookAndCopies" variant="outline">
        <RefreshCw class="h-4 w-4 mr-2" />
        Try Again
      </Button>
    </div>

    <!-- Book copies list -->
    <div v-else-if="book">
      <BookCopyList
        :copies="copies"
        :loading="loadingCopies"
        :show-actions="true"
        @borrow="handleBorrow"
        @return="handleReturn"
        @maintenance="handleMaintenance"
        @edit="handleEdit"
      />
    </div>

    <!-- Create Copy Dialog -->
    <Dialog v-model:open="showCreateDialog">
      <DialogContent class="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Add New Copy</DialogTitle>
          <DialogDescription>
            Create a new copy of "{{ book?.title }}"
          </DialogDescription>
        </DialogHeader>
        
        <div class="space-y-4">
          <div>
            <Label for="barcode">Barcode</Label>
            <Input
              id="barcode"
              v-model="newCopy.barcode"
              placeholder="Enter barcode..."
            />
          </div>
          
          <div>
            <Label for="status">Status</Label>
            <Select v-model="newCopy.status">
              <SelectTrigger>
                <SelectValue placeholder="Select status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="AVAILABLE">Available</SelectItem>
                <SelectItem value="MAINTENANCE">Maintenance</SelectItem>
              </SelectContent>
            </Select>
          </div>
          
          <div>
            <Label for="purchasePrice">Purchase Price (Optional)</Label>
            <Input
              id="purchasePrice"
              v-model.number="newCopy.purchasePrice"
              type="number"
              step="0.01"
              placeholder="0.00"
            />
          </div>
        </div>
        
        <DialogFooter>
          <Button variant="outline" @click="showCreateDialog = false">
            Cancel
          </Button>
          <Button @click="createCopy" :disabled="!newCopy.barcode">
            Create Copy
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Edit Copy Dialog -->
    <Dialog v-model:open="showEditDialog">
      <DialogContent class="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Edit Copy</DialogTitle>
          <DialogDescription>
            Update copy details
          </DialogDescription>
        </DialogHeader>
        
        <div v-if="editingCopy" class="space-y-4">
          <div>
            <Label for="edit-barcode">Barcode</Label>
            <Input
              id="edit-barcode"
              v-model="editForm.barcode"
              placeholder="Enter barcode..."
            />
          </div>
          
          <div>
            <Label for="edit-status">Status</Label>
            <Select v-model="editForm.status">
              <SelectTrigger>
                <SelectValue placeholder="Select status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="AVAILABLE">Available</SelectItem>
                <SelectItem value="BORROWED">Borrowed</SelectItem>
                <SelectItem value="MAINTENANCE">Maintenance</SelectItem>
                <SelectItem value="SCRAPPED">Scrapped</SelectItem>
                <SelectItem value="DISCARDED">Discarded</SelectItem>
              </SelectContent>
            </Select>
          </div>
          
          <div>
            <Label for="edit-purchasePrice">Purchase Price</Label>
            <Input
              id="edit-purchasePrice"
              v-model.number="editForm.purchasePrice"
              type="number"
              step="0.01"
              placeholder="0.00"
            />
          </div>
        </div>
        
        <DialogFooter>
          <Button variant="outline" @click="showEditDialog = false">
            Cancel
          </Button>
          <Button @click="updateCopy" :disabled="!editForm.barcode">
            Update Copy
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Plus, AlertCircle, RefreshCw } from 'lucide-vue-next'
import BookCopyList from '@/components/BookCopyList.vue'
import { api } from '@/lib/api'
import type { BookDto, BookCopy, BookCopyStatus } from '@/lib/api/types'
import { useAuthStore } from '@/stores/auth'
import { toast } from 'sonner'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const book = ref<BookDto | null>(null)
const copies = ref<BookCopy[]>([])
const loadingBook = ref(false)
const loadingCopies = ref(false)
const error = ref<string | null>(null)

const showCreateDialog = ref(false)
const showEditDialog = ref(false)
const editingCopy = ref<BookCopy | null>(null)

const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

// New copy form
const newCopy = reactive({
  barcode: '',
  status: 'AVAILABLE' as BookCopyStatus,
  purchasePrice: undefined as number | undefined
})

// Edit copy form
const editForm = reactive({
  barcode: '',
  status: 'AVAILABLE' as BookCopyStatus,
  purchasePrice: undefined as number | undefined
})

const resetNewCopyForm = () => {
  newCopy.barcode = ''
  newCopy.status = 'AVAILABLE'
  newCopy.purchasePrice = undefined
}

const resetEditForm = () => {
  if (editingCopy.value) {
    editForm.barcode = editingCopy.value.barcode
    editForm.status = editingCopy.value.status
    editForm.purchasePrice = editingCopy.value.purchasePrice
  }
}

const loadBookAndCopies = async () => {
  const bookId = Number(route.params.id)
  if (!bookId) {
    error.value = 'Invalid book ID'
    return
  }

  try {
    error.value = null
    loadingBook.value = true
    loadingCopies.value = true

    // Load book details and copies in parallel
    const [bookData, copiesData] = await Promise.all([
      api.books.getById(bookId),
      api.bookCopies.getCopiesByBookId(bookId)
    ])

    book.value = bookData
    copies.value = copiesData
  } catch (err: any) {
    error.value = err.message || 'Failed to load book details'
    console.error('Error loading book and copies:', err)
  } finally {
    loadingBook.value = false
    loadingCopies.value = false
  }
}

const createCopy = async () => {
  if (!book.value || !newCopy.barcode) return

  try {
    await api.bookCopies.createCopy({
      bookId: book.value.id,
      barcode: newCopy.barcode,
      status: newCopy.status,
      purchasePrice: newCopy.purchasePrice,
      purchaseTime: new Date().toISOString()
    })

    toast.success('Copy created successfully')
    showCreateDialog.value = false
    resetNewCopyForm()
    await loadBookAndCopies() // Reload to get the new copy
  } catch (err: any) {
    toast.error(err.message || 'Failed to create copy')
  }
}

const updateCopy = async () => {
  if (!editingCopy.value || !editForm.barcode) return

  try {
    await api.bookCopies.updateCopy(editingCopy.value.id, {
      barcode: editForm.barcode,
      status: editForm.status,
      purchasePrice: editForm.purchasePrice
    })

    toast.success('Copy updated successfully')
    showEditDialog.value = false
    editingCopy.value = null
    await loadBookAndCopies() // Reload to get updated data
  } catch (err: any) {
    toast.error(err.message || 'Failed to update copy')
  }
}

const handleBorrow = async (copy: BookCopy) => {
  try {
    if (!authStore.user) {
      toast.error('Please login to borrow books')
      return
    }

    await api.borrow.borrowBook({
      userId: authStore.user.id,
      copyId: copy.id
    })

    toast.success('Book borrowed successfully')
    await loadBookAndCopies() // Reload to update status
  } catch (err: any) {
    toast.error(err.message || 'Failed to borrow book')
  }
}

const handleReturn = async (copy: BookCopy) => {
  try {
    // This would need a return endpoint
    toast.error('Return functionality not yet implemented in backend')
  } catch (err: any) {
    toast.error(err.message || 'Failed to return book')
  }
}

const handleMaintenance = async (copy: BookCopy) => {
  try {
    await api.bookCopies.updateCopyStatus(copy.id, 'MAINTENANCE')
    toast.success('Copy status updated to maintenance')
    await loadBookAndCopies()
  } catch (err: any) {
    toast.error(err.message || 'Failed to update copy status')
  }
}

const handleEdit = (copy: BookCopy) => {
  editingCopy.value = copy
  resetEditForm()
  showEditDialog.value = true
}

onMounted(() => {
  loadBookAndCopies()
})
</script>