<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Bell,
  Calendar,
  ChevronLeft,
  ChevronRight,
  Edit,
  Plus,
  Search,
  Trash2,
  Clock,
} from 'lucide-vue-next'
import { noticesService } from '@/lib/api'
import type { Notice, PagedResponse } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const { t } = useI18n()
const authStore = useAuthStore()

// Data
const notices = ref<Notice[]>([])
const isLoading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(10)

// Dialog states
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const editingNotice = ref<Notice | null>(null)
const deletingNoticeId = ref<number | null>(null)
const isSubmitting = ref(false)

// Form data
const noticeForm = ref({
  title: '',
  content: '',
  publishTime: '',
  expireTime: '',
  status: 1,
})

// Computed
const isAdmin = computed(() => {
  return authStore.isAdmin()
})

const filteredNotices = computed(() => {
  if (!searchKeyword.value) return notices.value

  const keyword = searchKeyword.value.toLowerCase()
  return notices.value.filter(
    (notice) =>
      notice.title.toLowerCase().includes(keyword) ||
      notice.content.toLowerCase().includes(keyword),
  )
})

// Methods
const loadNotices = async () => {
  try {
    isLoading.value = true
    let response: PagedResponse<Notice>

    if (isAdmin.value) {
      // Admin can see all notices including unpublished ones
      response = await noticesService.getAllForAdmin({
        page: currentPage.value,
        size: pageSize.value,
      })
    } else {
      // Regular users only see published notices
      response = await noticesService.getAll({
        page: currentPage.value,
        size: pageSize.value,
      })
    }

    notices.value = response.content
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
  } catch (error) {
    console.error('Error loading notices:', error)
    toast.error(t('notices.messages.loadError'))
  } finally {
    isLoading.value = false
  }
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const getTimeSince = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (minutes < 1) return t('notices.time.justNow')
  if (minutes < 60)
    return t('notices.time.minutesAgo', { count: minutes, plural: minutes === 1 ? '' : 's' })
  if (hours < 24)
    return t('notices.time.hoursAgo', { count: hours, plural: hours === 1 ? '' : 's' })
  if (days < 7) return t('notices.time.daysAgo', { count: days, plural: days === 1 ? '' : 's' })

  return formatDate(dateString)
}

const isRecent = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  return days <= 7
}

// Helper function to format date for datetime-local input
const formatDateForInput = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day}T${hours}:${minutes}`
}

// Helper function to convert UTC date string to local datetime format
const formatUTCDateForInput = (utcDateString: string) => {
  const date = new Date(utcDateString)
  return formatDateForInput(date)
}

const openAddDialog = () => {
  const now = new Date()
  const publishTime = new Date(now.getTime() + 5 * 60000) // Default to 5 minutes from now
  noticeForm.value = {
    title: '',
    content: '',
    publishTime: formatDateForInput(publishTime), // Use local time directly
    expireTime: '',
    status: 1,
  }
  showAddDialog.value = true
}

const openEditDialog = (notice: Notice) => {
  editingNotice.value = notice
  const publishTime = formatUTCDateForInput(notice.publishTime)
  const expireTime = notice.expireTime ? formatUTCDateForInput(notice.expireTime) : ''

  noticeForm.value = {
    title: notice.title,
    content: notice.content,
    publishTime: publishTime,
    expireTime: expireTime,
    status: notice.status,
  }
  showEditDialog.value = true
}

const closeDialogs = () => {
  showAddDialog.value = false
  showEditDialog.value = false
  showDeleteDialog.value = false
  editingNotice.value = null
  deletingNoticeId.value = null
  noticeForm.value = {
    title: '',
    content: '',
    publishTime: '',
    expireTime: '',
    status: 1,
  }
}

const handleCreateNotice = async () => {
  if (
    !noticeForm.value.title.trim() ||
    !noticeForm.value.content.trim() ||
    !noticeForm.value.publishTime
  ) {
    toast.error(t('notices.form.validation.requiredFields'))
    return
  }

  try {
    isSubmitting.value = true
    await noticesService.create({
      title: noticeForm.value.title.trim(),
      content: noticeForm.value.content.trim(),
      publishTime: new Date(noticeForm.value.publishTime).toISOString(),
      expireTime: noticeForm.value.expireTime
        ? new Date(noticeForm.value.expireTime).toISOString()
        : undefined,
      status: noticeForm.value.status,
    })

    toast.success(t('notices.messages.createSuccess'))
    closeDialogs()
    await loadNotices()
  } catch (error) {
    console.error('Error creating notice:', error)
    toast.error(t('notices.messages.createError'))
  } finally {
    isSubmitting.value = false
  }
}

const handleUpdateNotice = async () => {
  if (
    !editingNotice.value ||
    !noticeForm.value.title.trim() ||
    !noticeForm.value.content.trim() ||
    !noticeForm.value.publishTime
  ) {
    toast.error(t('notices.form.validation.requiredFields'))
    return
  }

  try {
    isSubmitting.value = true
    await noticesService.update(editingNotice.value.id, {
      title: noticeForm.value.title.trim(),
      content: noticeForm.value.content.trim(),
      publishTime: new Date(noticeForm.value.publishTime).toISOString(),
      expireTime: noticeForm.value.expireTime
        ? new Date(noticeForm.value.expireTime).toISOString()
        : undefined,
      status: noticeForm.value.status,
    })

    toast.success(t('notices.messages.updateSuccess'))
    closeDialogs()
    await loadNotices()
  } catch (error) {
    console.error('Error updating notice:', error)
    toast.error(t('notices.messages.updateError'))
  } finally {
    isSubmitting.value = false
  }
}

const openDeleteDialog = (noticeId: number) => {
  deletingNoticeId.value = noticeId
  showDeleteDialog.value = true
}

const handleDeleteNotice = async () => {
  if (!deletingNoticeId.value) return

  try {
    await noticesService.delete(deletingNoticeId.value)
    toast.success(t('notices.messages.deleteSuccess'))
    closeDialogs()
    await loadNotices()
  } catch (error) {
    console.error('Error deleting notice:', error)
    toast.error(t('notices.messages.deleteError'))
  }
}

const handlePageChange = (page: number) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    loadNotices()
  }
}

// Lifecycle
onMounted(() => {
  loadNotices()
})
</script>

<template>
  <div class="notices-view space-y-6">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold">{{ t('notices.title') }}</h1>
        <p class="text-muted-foreground">{{ t('notices.description') }}</p>
      </div>
      <Button v-if="isAdmin" @click="openAddDialog">
        <Plus class="h-4 w-4 mr-2" />
        {{ t('notices.addNotice') }}
      </Button>
    </div>

    <!-- Search -->
    <Card class="p-4">
      <div class="flex items-center gap-4">
        <div class="relative flex-1">
          <Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input v-model="searchKeyword" :placeholder="t('notices.search.placeholder')" class="pl-10" />
        </div>
        <div class="text-sm text-muted-foreground">
          {{ t('notices.search.showing', { count: filteredNotices.length, total: totalElements }) }}
        </div>
      </div>
    </Card>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-8">{{ t('notices.loading') }}</div>

    <!-- Empty State -->
    <Card v-else-if="notices.length === 0">
      <CardContent class="flex flex-col items-center justify-center py-12 text-center">
        <Bell class="h-16 w-16 text-muted-foreground mb-4" />
        <h3 class="text-lg font-semibold mb-2">{{ t('notices.empty.title') }}</h3>
        <p class="text-muted-foreground mb-4">{{ t('notices.empty.description') }}</p>
        <Button v-if="isAdmin" @click="openAddDialog">
          <Plus class="h-4 w-4 mr-2" />
          {{ t('notices.empty.createFirst') }}
        </Button>
      </CardContent>
    </Card>

    <!-- Notices List -->
    <template v-else>
      <div v-if="filteredNotices.length === 0" class="text-center py-8 text-muted-foreground">
        {{ t('notices.noResults') }}
      </div>

      <div v-else class="space-y-4">
        <Card v-for="notice in filteredNotices" :key="notice.id" class="hover:shadow-md transition-shadow">
          <CardHeader class="pb-3">
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-2">
                  <CardTitle class="text-lg">{{ notice.title }}</CardTitle>
                  <Badge v-if="isRecent(notice.publishTime)" variant="secondary" class="text-xs">
                    {{ t('notices.status.new') }}
                  </Badge>
                </div>
                <div class="flex items-center gap-2 text-sm text-muted-foreground">
                  <Calendar class="h-4 w-4" />
                  <span>{{ getTimeSince(notice.publishTime) }}</span>
                  <span v-if="notice.updateTime && notice.updateTime !== notice.publishTime">
                    • {{ t('notices.time.updated', { time: getTimeSince(notice.updateTime) }) }}
                  </span>
                  <Badge v-if="notice.status === 2" variant="outline" class="text-xs ml-2">
                    {{ t('notices.status.pinned') }}
                  </Badge>
                </div>
              </div>

              <!-- Admin Actions -->
              <div v-if="isAdmin" class="flex gap-1">
                <Button variant="ghost" size="sm" @click="openEditDialog(notice)">
                  <Edit class="h-4 w-4" />
                </Button>
                <Button variant="ghost" size="sm" @click="openDeleteDialog(notice.id)">
                  <Trash2 class="h-4 w-4" />
                </Button>
              </div>
            </div>
          </CardHeader>

          <CardContent>
            <div class="prose prose-sm max-w-none">
              <div class="whitespace-pre-wrap leading-relaxed">{{ notice.content }}</div>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="flex justify-center items-center gap-2">
        <Button variant="outline" size="sm" :disabled="currentPage === 0" @click="handlePageChange(currentPage - 1)">
          <ChevronLeft class="h-4 w-4" />
          {{ t('notices.pagination.previous') }}
        </Button>

        <span class="text-sm text-muted-foreground px-4">
          {{ t('notices.pagination.page', { current: currentPage + 1, total: totalPages }) }}
        </span>

        <Button variant="outline" size="sm" :disabled="currentPage === totalPages - 1"
          @click="handlePageChange(currentPage + 1)">
          {{ t('notices.pagination.next') }}
          <ChevronRight class="h-4 w-4" />
        </Button>
      </div>
    </template>

    <!-- Add Notice Dialog -->
    <Dialog v-model:open="showAddDialog">
      <DialogContent class="sm:max-w-[600px]">
        <DialogHeader>
          <DialogTitle>{{ t('notices.form.create.title') }}</DialogTitle>
          <DialogDescription>{{ t('notices.form.create.description') }}</DialogDescription>
        </DialogHeader>

        <form @submit.prevent="handleCreateNotice" class="space-y-4">
          <div class="space-y-2">
            <Label for="add-title">{{ t('notices.form.fields.title.label') }}</Label>
            <Input id="add-title" v-model="noticeForm.title" :placeholder="t('notices.form.fields.title.placeholder')"
              required />
          </div>

          <div class="space-y-2">
            <Label for="add-content">{{ t('notices.form.fields.content.label') }}</Label>
            <textarea id="add-content" v-model="noticeForm.content"
              :placeholder="t('notices.form.fields.content.placeholder')"
              class="w-full min-h-[200px] p-3 border rounded-md resize-none" required />
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label for="add-publish-time">{{
                t('notices.form.fields.publishTime.required')
              }}</Label>
              <Input id="add-publish-time" v-model="noticeForm.publishTime" type="datetime-local" required />
            </div>

            <div class="space-y-2">
              <Label for="add-expire-time">{{ t('notices.form.fields.expireTime.label') }}</Label>
              <Input id="add-expire-time" v-model="noticeForm.expireTime" type="datetime-local" />
            </div>
          </div>

          <div class="space-y-2">
            <Label for="add-status">{{ t('notices.form.fields.status.required') }}</Label>
            <Select v-model="noticeForm.status" required>
              <SelectTrigger class="w-full">
                <SelectValue :placeholder="t('notices.form.fields.status.placeholder')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem :value="1">{{ t('notices.status.show') }}</SelectItem>
                <SelectItem :value="2">{{ t('notices.status.pinned') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div class="flex justify-end gap-2">
            <Button type="button" variant="outline" @click="closeDialogs">{{
              t('notices.form.buttons.cancel')
            }}</Button>
            <Button type="submit" :disabled="isSubmitting">
              {{
                isSubmitting ? t('notices.form.buttons.creating') : t('notices.form.buttons.create')
              }}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>

    <!-- Edit Notice Dialog -->
    <Dialog v-model:open="showEditDialog">
      <DialogContent class="sm:max-w-[600px]">
        <DialogHeader>
          <DialogTitle>{{ t('notices.form.edit.title') }}</DialogTitle>
          <DialogDescription>{{ t('notices.form.edit.description') }}</DialogDescription>
        </DialogHeader>

        <form @submit.prevent="handleUpdateNotice" class="space-y-4">
          <div class="space-y-2">
            <Label for="edit-title">{{ t('notices.form.fields.title.label') }}</Label>
            <Input id="edit-title" v-model="noticeForm.title" :placeholder="t('notices.form.fields.title.placeholder')"
              required />
          </div>

          <div class="space-y-2">
            <Label for="edit-content">{{ t('notices.form.fields.content.label') }}</Label>
            <textarea id="edit-content" v-model="noticeForm.content"
              :placeholder="t('notices.form.fields.content.placeholder')"
              class="w-full min-h-[200px] p-3 border rounded-md resize-none" required />
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
              <Label for="edit-publish-time">{{
                t('notices.form.fields.publishTime.required')
              }}</Label>
              <Input id="edit-publish-time" v-model="noticeForm.publishTime" type="datetime-local" required />
            </div>

            <div class="space-y-2">
              <Label for="edit-expire-time">{{ t('notices.form.fields.expireTime.label') }}</Label>
              <Input id="edit-expire-time" v-model="noticeForm.expireTime" type="datetime-local" />
            </div>
          </div>

          <div class="space-y-2">
            <Label for="edit-status">{{ t('notices.form.fields.status.required') }}</Label>
            <Select v-model="noticeForm.status" required>
              <SelectTrigger class="w-full">
                <SelectValue :placeholder="t('notices.form.fields.status.placeholder')" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem :value="1">{{ t('notices.status.show') }}</SelectItem>
                <SelectItem :value="2">{{ t('notices.status.pinned') }}</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <div class="flex justify-end gap-2">
            <Button type="button" variant="outline" @click="closeDialogs">{{
              t('notices.form.buttons.cancel')
            }}</Button>
            <Button type="submit" :disabled="isSubmitting">
              {{
                isSubmitting ? t('notices.form.buttons.updating') : t('notices.form.buttons.update')
              }}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>

    <!-- Delete Confirmation Dialog -->
    <AlertDialog v-model:open="showDeleteDialog">
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{{ t('notices.delete.title') }}</AlertDialogTitle>
          <AlertDialogDescription>
            {{ t('notices.delete.description') }}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel @click="closeDialogs">{{
            t('notices.delete.cancel')
          }}</AlertDialogCancel>
          <AlertDialogAction @click="handleDeleteNotice" class="bg-destructive text-white hover:bg-destructive/90">
            {{ t('notices.delete.confirm') }}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  </div>
</template>

<style scoped>
textarea {
  background: hsl(var(--background));
  border: 1px solid hsl(var(--border));
  color: hsl(var(--foreground));
}

textarea:focus {
  outline: none;
  border-color: hsl(var(--ring));
  box-shadow: 0 0 0 2px hsl(var(--ring) / 0.2);
}

.prose {
  color: hsl(var(--foreground));
}
</style>
