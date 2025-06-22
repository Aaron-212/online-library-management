<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Separator } from '@/components/ui/separator'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { ArrowLeft, Save, AlertCircle, Loader2 } from 'lucide-vue-next'
import { booksService } from '@/lib/api/services/books'
import type { BookDto, BookCreateDto, BookUpdateDto } from '@/lib/api/types'

const route = useRoute()
const router = useRouter()

// Props
const props = defineProps<{
  id?: string
}>()

// Reactive state
const isLoading = ref(false)
const isSaving = ref(false)
const error = ref<string | null>(null)
const success = ref<string | null>(null)

// Form data
const formData = ref({
  isbn: '',
  title: '',
  language: '',
  description: '',
  coverURL: '',
  authorNames: [] as string[],
  publisherNames: [] as string[],
  categoryName: '',
  totalQuantity: 1
})

// Input refs for author and publisher management
const newAuthor = ref('')
const newPublisher = ref('')

// Computed
const isEditMode = computed(() => !!props.id)
const pageTitle = computed(() => isEditMode.value ? 'Edit Book' : 'Add New Book')
const submitButtonText = computed(() => isSaving.value ? 'Saving...' : (isEditMode.value ? 'Update Book' : 'Create Book'))

// Methods
const loadBook = async () => {
  if (!props.id) return

  try {
    isLoading.value = true
    error.value = null
    
    const book = await booksService.getById(parseInt(props.id))
    
    formData.value = {
      isbn: book.isbn,
      title: book.title,
      language: book.language,
      description: book.description || '',
      coverURL: book.coverURL || '',
      authorNames: book.authors.map(author => author.name),
      publisherNames: book.publishers.map(publisher => publisher.name),
      categoryName: book.indexCategory?.name || '',
      totalQuantity: book.totalQuantity
    }
  } catch (err: any) {
    error.value = err.message || 'Failed to load book data'
  } finally {
    isLoading.value = false
  }
}

const addAuthor = () => {
  if (newAuthor.value.trim() && !formData.value.authorNames.includes(newAuthor.value.trim())) {
    formData.value.authorNames.push(newAuthor.value.trim())
    newAuthor.value = ''
  }
}

const removeAuthor = (index: number) => {
  formData.value.authorNames.splice(index, 1)
}

const addPublisher = () => {
  if (newPublisher.value.trim() && !formData.value.publisherNames.includes(newPublisher.value.trim())) {
    formData.value.publisherNames.push(newPublisher.value.trim())
    newPublisher.value = ''
  }
}

const removePublisher = (index: number) => {
  formData.value.publisherNames.splice(index, 1)
}

const validateForm = () => {
  if (!formData.value.title.trim()) {
    error.value = 'Title is required'
    return false
  }
  if (!formData.value.isbn.trim()) {
    error.value = 'ISBN is required'
    return false
  }
  if (!formData.value.language.trim()) {
    error.value = 'Language is required'
    return false
  }
  if (formData.value.authorNames.length === 0) {
    error.value = 'At least one author is required'
    return false
  }
  if (formData.value.publisherNames.length === 0) {
    error.value = 'At least one publisher is required'
    return false
  }
  if (!formData.value.categoryName.trim()) {
    error.value = 'Category is required'
    return false
  }
  if (formData.value.totalQuantity < 1) {
    error.value = 'Total quantity must be at least 1'
    return false
  }
  return true
}

const handleSubmit = async () => {
  if (!validateForm()) return

  try {
    isSaving.value = true
    error.value = null
    success.value = null

    if (isEditMode.value) {
      // Update existing book
      const updateData: BookUpdateDto = {
        title: formData.value.title,
        language: formData.value.language,
        description: formData.value.description || undefined,
        coverURL: formData.value.coverURL || undefined,
        authorNames: formData.value.authorNames,
        publisherNames: formData.value.publisherNames,
        categoryName: formData.value.categoryName
      }
      
      await booksService.update(parseInt(props.id!), updateData)
      success.value = 'Book updated successfully!'
    } else {
      // Create new book
      const createData: BookCreateDto = {
        isbn: formData.value.isbn,
        title: formData.value.title,
        language: formData.value.language,
        description: formData.value.description || undefined,
        coverURL: formData.value.coverURL || undefined,
        authorNames: formData.value.authorNames,
        publisherNames: formData.value.publisherNames,
        categoryName: formData.value.categoryName,
        totalQuantity: formData.value.totalQuantity
      }
      
      await booksService.create(createData)
      success.value = 'Book created successfully!'
    }

    // Redirect after successful operation
    setTimeout(() => {
      router.push('/admin/books')
    }, 1500)
  } catch (err: any) {
    error.value = err.message || 'Failed to save book'
  } finally {
    isSaving.value = false
  }
}

const goBack = () => {
  router.back()
}

// Lifecycle
onMounted(() => {
  if (isEditMode.value) {
    loadBook()
  }
})
</script>

<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center space-x-4">
      <Button variant="outline" size="sm" @click="goBack">
        <ArrowLeft class="h-4 w-4" />
        Back
      </Button>
      <div>
        <h1 class="text-2xl font-bold">{{ pageTitle }}</h1>
        <p class="text-muted-foreground">
          {{ isEditMode ? 'Update book information' : 'Add a new book to the library' }}
        </p>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="isLoading" class="flex items-center justify-center py-12">
      <Loader2 class="h-8 w-8 animate-spin" />
      <span class="ml-2">Loading book data...</span>
    </div>

    <!-- Form -->
    <form v-else @submit.prevent="handleSubmit" class="space-y-6">
      <!-- Alerts -->
      <Alert v-if="error" variant="destructive">
        <AlertCircle class="h-4 w-4" />
        <AlertDescription>{{ error }}</AlertDescription>
      </Alert>

      <Alert v-if="success" class="border-green-200 bg-green-50 text-green-800">
        <AlertDescription>{{ success }}</AlertDescription>
      </Alert>

      <!-- Basic Information -->
      <Card>
        <CardHeader>
          <CardTitle>Basic Information</CardTitle>
          <CardDescription>Enter the book's basic details</CardDescription>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label for="title">Title *</Label>
              <Input
                id="title"
                v-model="formData.title"
                placeholder="Enter book title"
                required
              />
            </div>
            <div class="space-y-2">
              <Label for="isbn">ISBN *</Label>
              <Input
                id="isbn"
                v-model="formData.isbn"
                placeholder="Enter ISBN"
                :disabled="isEditMode"
                required
              />
            </div>
          </div>
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label for="language">Language *</Label>
              <Input
                id="language"
                v-model="formData.language"
                placeholder="e.g., English, Spanish, etc."
                required
              />
            </div>
            <div class="space-y-2" v-if="!isEditMode">
              <Label for="totalQuantity">Total Quantity *</Label>
              <Input
                id="totalQuantity"
                v-model.number="formData.totalQuantity"
                type="number"
                min="1"
                required
              />
            </div>
          </div>

          <div class="space-y-2">
            <Label for="description">Description</Label>
            <textarea
              id="description"
              v-model="formData.description"
              class="min-h-[100px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
              placeholder="Enter book description (optional)"
            />
          </div>

          <div class="space-y-2">
            <Label for="coverURL">Cover Image URL</Label>
            <Input
              id="coverURL"
              v-model="formData.coverURL"
              type="url"
              placeholder="https://example.com/book-cover.jpg (optional)"
            />
          </div>
        </CardContent>
      </Card>

      <!-- Authors -->
      <Card>
        <CardHeader>
          <CardTitle>Authors</CardTitle>
          <CardDescription>Add the book's authors</CardDescription>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="flex space-x-2">
            <Input
              v-model="newAuthor"
              placeholder="Enter author name"
              @keyup.enter="addAuthor"
            />
            <Button type="button" @click="addAuthor" variant="outline">
              Add
            </Button>
          </div>
          
          <div v-if="formData.authorNames.length > 0" class="space-y-2">
            <div
              v-for="(author, index) in formData.authorNames"
              :key="index"
              class="flex items-center justify-between p-2 bg-muted rounded"
            >
              <span>{{ author }}</span>
              <Button
                type="button"
                variant="ghost"
                size="sm"
                @click="removeAuthor(index)"
              >
                Remove
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Publishers -->
      <Card>
        <CardHeader>
          <CardTitle>Publishers</CardTitle>
          <CardDescription>Add the book's publishers</CardDescription>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="flex space-x-2">
            <Input
              v-model="newPublisher"
              placeholder="Enter publisher name"
              @keyup.enter="addPublisher"
            />
            <Button type="button" @click="addPublisher" variant="outline">
              Add
            </Button>
          </div>
          
          <div v-if="formData.publisherNames.length > 0" class="space-y-2">
            <div
              v-for="(publisher, index) in formData.publisherNames"
              :key="index"
              class="flex items-center justify-between p-2 bg-muted rounded"
            >
              <span>{{ publisher }}</span>
              <Button
                type="button"
                variant="ghost"
                size="sm"
                @click="removePublisher(index)"
              >
                Remove
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Category -->
      <Card>
        <CardHeader>
          <CardTitle>Category</CardTitle>
          <CardDescription>Specify the book's category</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="space-y-2">
            <Label for="category">Category *</Label>
            <Input
              id="category"
              v-model="formData.categoryName"
              placeholder="Enter category name"
              required
            />
          </div>
        </CardContent>
      </Card>

      <Separator />

      <!-- Form Actions -->
      <div class="flex justify-end space-x-4">
        <Button type="button" variant="outline" @click="goBack">
          Cancel
        </Button>
        <Button type="submit" :disabled="isSaving">
          <Loader2 v-if="isSaving" class="h-4 w-4 animate-spin mr-2" />
          <Save v-else class="h-4 w-4 mr-2" />
          {{ submitButtonText }}
        </Button>
      </div>
    </form>
  </div>
</template>
