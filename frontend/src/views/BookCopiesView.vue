<template>
  <div class="container mx-auto px-4 py-8">
    <!-- Header -->
    <div class="mb-6">
      <nav class="flex items-center gap-2 text-sm text-gray-600 mb-4">
        <router-link to="/books" class="hover:text-gray-900">{{
          t('bookCopies.breadcrumb.books')
        }}</router-link>
        <span>/</span>
        <router-link v-if="book" :to="`/books/${book.id}`" class="hover:text-gray-900">
          {{ book.title }}
        </router-link>
        <span>/</span>
        <span class="text-gray-900">{{ t('bookCopies.breadcrumb.copies') }}</span>
      </nav>

      <div v-if="book" class="flex items-start justify-between">
        <div>
          <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ t('bookCopies.header.title') }}</h1>
          <div class="flex items-center gap-4 text-sm text-gray-600">
            <span>{{ book.title }}</span>
            <Badge variant="outline">{{ book.isbn }}</Badge>
          </div>
        </div>

        <div v-if="isAdmin" class="flex gap-2">
          <Button @click="showCreateDialog = true" class="gap-2">
            <Plus class="h-4 w-4" />
            {{ t('bookCopies.header.addCopy') }}
          </Button>
        </div>
      </div>
    </div>

    <!-- Loading book details -->
    <div v-if="loadingBook" class="flex justify-center py-8">
      <div class="text-center">
        <div
          class="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900 mx-auto mb-4"
        ></div>
        <p class="text-gray-600">{{ t('bookCopies.loading.bookDetails') }}</p>
      </div>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="text-center py-8">
      <AlertCircle class="h-16 w-16 text-red-400 mx-auto mb-4" />
      <h3 class="text-lg font-medium text-gray-900 mb-2">{{ t('bookCopies.loading.error') }}</h3>
      <p class="text-gray-500 mb-4">{{ error }}</p>
      <Button @click="loadBookAndCopies" variant="outline">
        <RefreshCw class="h-4 w-4 mr-2" />
        {{ t('bookCopies.loading.tryAgain') }}
      </Button>
    </div>

    <!-- Book copies list -->
    <div v-else-if="book">
      <BookCopyList
        :copies="copies"
        :loading="loadingCopies"
        :show-actions="true"
        :user-borrowed-copies="userBorrowedCopies"
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
          <DialogTitle>{{ t('bookCopies.dialogs.create.title') }}</DialogTitle>
          <DialogDescription
            >{{ t('bookCopies.dialogs.create.description', { title: book?.title }) }}
          </DialogDescription>
        </DialogHeader>

        <div class="space-y-4">
          <div>
            <Label for="barcode">{{ t('bookCopies.dialogs.create.fields.barcode.label') }}</Label>
            <Input
              id="barcode"
              v-model="newCopy.barcode"
              :placeholder="t('bookCopies.dialogs.create.fields.barcode.placeholder')"
            />
          </div>

          <div>
            <Label for="status">{{ t('bookCopies.dialogs.create.fields.status.label') }}</Label>
            <Select v-model="newCopy.status">
              <SelectTrigger>
                <SelectValue
                  :placeholder="t('bookCopies.dialogs.create.fields.status.placeholder')"
                />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="AVAILABLE">{{ t('bookCopies.status.available') }}</SelectItem>
                <SelectItem value="MAINTENANCE">{{
                  t('bookCopies.status.maintenance')
                }}</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div>
            <Label for="purchasePrice">{{
              t('bookCopies.dialogs.create.fields.purchasePrice.label')
            }}</Label>
            <Input
              id="purchasePrice"
              v-model.number="newCopy.purchasePrice"
              type="number"
              step="0.01"
              :placeholder="t('bookCopies.dialogs.create.fields.purchasePrice.placeholder')"
            />
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" @click="showCreateDialog = false">{{
            t('bookCopies.dialogs.create.buttons.cancel')
          }}</Button>
          <Button @click="createCopy" :disabled="!newCopy.barcode">{{
            t('bookCopies.dialogs.create.buttons.create')
          }}</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Edit Copy Dialog -->
    <Dialog v-model:open="showEditDialog">
      <DialogContent class="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>{{ t('bookCopies.dialogs.edit.title') }}</DialogTitle>
          <DialogDescription>{{ t('bookCopies.dialogs.edit.description') }}</DialogDescription>
        </DialogHeader>

        <div v-if="editingCopy" class="space-y-4">
          <div>
            <Label for="edit-barcode">{{
              t('bookCopies.dialogs.edit.fields.barcode.label')
            }}</Label>
            <Input
              id="edit-barcode"
              v-model="editForm.barcode"
              :placeholder="t('bookCopies.dialogs.edit.fields.barcode.placeholder')"
            />
          </div>

          <div>
            <Label for="edit-status">{{ t('bookCopies.dialogs.edit.fields.status.label') }}</Label>
            <Select v-model="editForm.status">
              <SelectTrigger>
                <SelectValue
                  :placeholder="t('bookCopies.dialogs.edit.fields.status.placeholder')"
                />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="AVAILABLE">{{ t('bookCopies.status.available') }}</SelectItem>
                <SelectItem value="BORROWED">{{ t('bookCopies.status.borrowed') }}</SelectItem>
                <SelectItem value="MAINTENANCE">{{
                  t('bookCopies.status.maintenance')
                }}</SelectItem>
                <SelectItem value="SCRAPPED">{{ t('bookCopies.status.scrapped') }}</SelectItem>
                <SelectItem value="DISCARDED">{{ t('bookCopies.status.discarded') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div>
            <Label for="edit-purchasePrice">{{
              t('bookCopies.dialogs.edit.fields.purchasePrice.label')
            }}</Label>
            <Input
              id="edit-purchasePrice"
              v-model.number="editForm.purchasePrice"
              type="number"
              step="0.01"
              :placeholder="t('bookCopies.dialogs.edit.fields.purchasePrice.placeholder')"
            />
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" @click="showEditDialog = false">{{
            t('bookCopies.dialogs.edit.buttons.cancel')
          }}</Button>
          <Button @click="updateCopy" :disabled="!editForm.barcode">{{
            t('bookCopies.dialogs.edit.buttons.update')
          }}</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
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
import { toast } from 'vue-sonner'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

const book = ref<BookDto | null>(null)
const copies = ref<BookCopy[]>([])
const loadingBook = ref(false)
const loadingCopies = ref(false)
const error = ref<string | null>(null)
const userBorrowedCopies = ref<Set<number>>(new Set())

const showCreateDialog = ref(false)
const showEditDialog = ref(false)
const editingCopy = ref<BookCopy | null>(null)

const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

// New copy form
const newCopy = reactive({
  barcode: '',
  status: 'AVAILABLE' as BookCopyStatus,
  purchasePrice: undefined as number | undefined,
})

// Edit copy form
const editForm = reactive({
  barcode: '',
  status: 'AVAILABLE' as BookCopyStatus,
  purchasePrice: undefined as number | undefined,
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

    // Load book details, copies, and user borrowed copies in parallel
    const [bookData, copiesData] = await Promise.all([
      api.books.getById(bookId),
      api.bookCopies.getCopiesByBookId(bookId),
    ])

    book.value = bookData
    copies.value = copiesData

    // Load user borrowed copies after we have the copies data
    await loadUserBorrowedCopies()
  } catch (err: any) {
    error.value = err.message || 'Failed to load book details'
    console.error('Error loading book and copies:', err)
  } finally {
    loadingBook.value = false
    loadingCopies.value = false
  }
}

const loadUserBorrowedCopies = async () => {
  try {
    if (!authStore.user) return

    const currentBorrowings = await api.borrow.getMyCurrentBorrowings()
    const borrowedCopyIds = currentBorrowings
      .filter((borrow) => borrow.status === 'BORROWED' || borrow.status === 'OVERDUE')
      .map((borrow) => borrow.copyId)

    userBorrowedCopies.value = new Set(borrowedCopyIds)
  } catch (error) {
    console.error('Error loading user borrowed copies:', error)
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
      purchaseTime: new Date().toISOString(),
    })

    toast.success(t('bookCopies.messages.createSuccess'))
    showCreateDialog.value = false
    resetNewCopyForm()
    await loadBookAndCopies() // Reload to get the new copy
  } catch (err: any) {
    toast.error(err.message || t('bookCopies.messages.createError'))
  }
}

const updateCopy = async () => {
  if (!editingCopy.value || !editForm.barcode) return

  try {
    await api.bookCopies.updateCopy(editingCopy.value.id, {
      barcode: editForm.barcode,
      status: editForm.status,
      purchasePrice: editForm.purchasePrice,
    })

    toast.success(t('bookCopies.messages.updateSuccess'))
    showEditDialog.value = false
    editingCopy.value = null
    await loadBookAndCopies() // Reload to get updated data
  } catch (err: any) {
    toast.error(err.message || t('bookCopies.messages.updateError'))
  }
}

const handleBorrow = async (copy: BookCopy) => {
  try {
    if (!authStore.user) {
      toast.error(t('bookCopies.messages.loginRequired'))
      return
    }

    await api.borrow.borrowBook({
      userId: authStore.user.id,
      copyId: copy.id,
    })

    toast.success(t('bookCopies.messages.borrowSuccess'))
    await loadBookAndCopies() // Reload to update status
  } catch (err: any) {
    toast.error(err.message || t('bookCopies.messages.borrowError'))
  }
}

const handleReturn = async (copy: BookCopy) => {
  try {
    // Security: Only allow returning if user is authenticated
    if (!authStore.user) {
      toast.error(t('bookCopies.messages.loginRequiredReturn'))
      return
    }

    // Find active borrow record for this user and copy
    const currentBorrowings = await api.borrow.getMyCurrentBorrowings()
    const activeBorrow = currentBorrowings.find(
      (borrow) =>
        borrow.copyId === copy.id && (borrow.status === 'BORROWED' || borrow.status === 'OVERDUE'),
    )

    if (!activeBorrow) {
      toast.error(t('bookCopies.messages.noBorrowRecord'))
      return
    }

    // Use secure return method with borrow ID
    await api.borrow.returnBookById(activeBorrow.borrowId)
    toast.success(t('bookCopies.messages.returnSuccess'))
    await loadBookAndCopies() // Reload to update status
  } catch (err: any) {
    toast.error(err.message || t('bookCopies.messages.returnError'))
  }
}

const handleMaintenance = async (copy: BookCopy) => {
  try {
    await api.bookCopies.updateCopyStatus(copy.id, 'MAINTENANCE')
    toast.success(t('bookCopies.messages.maintenanceSuccess'))
    await loadBookAndCopies()
  } catch (err: any) {
    toast.error(err.message || t('bookCopies.messages.maintenanceError'))
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
