<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold">My Reservations</h1>
      <p class="text-muted-foreground">Manage your book reservations</p>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-8">
      <div class="text-muted-foreground">Loading reservations...</div>
    </div>

    <template v-else>
      <!-- Reservations List -->
      <div class="bg-background border rounded-lg shadow-sm">
        <div class="p-4 border-b">
          <h2 class="text-xl font-semibold">Current Reservations</h2>
          <p class="text-sm text-muted-foreground">
            {{ reservations.length }} reservation{{ reservations.length !== 1 ? 's' : '' }} found
          </p>
        </div>
        <div class="p-4">
          <div v-if="reservations.length === 0" class="text-center py-12 text-muted-foreground">
            <div class="mb-4">
              <svg
                class="mx-auto h-12 w-12 text-muted-foreground/50"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            </div>
            <h3 class="text-lg font-semibold mb-2">No reservations</h3>
            <p>You haven't made any book reservations yet.</p>
          </div>
          <div v-else class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b">
                  <th class="text-left p-3">Book Title</th>
                  <th class="text-left p-3">Author(s)</th>
                  <th class="text-left p-3">Reservation Date</th>
                  <th class="text-left p-3">Expiration Date</th>
                  <th class="text-left p-3">Status</th>
                  <th class="text-left p-3">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="reservation in reservations"
                  :key="reservation.id"
                  class="border-b hover:bg-muted/50"
                >
                  <td class="p-3">
                    <div class="font-medium">{{ reservation.book.title }}</div>
                    <div class="text-sm text-muted-foreground">
                      ISBN: {{ reservation.book.isbn }}
                    </div>
                  </td>
                  <td class="p-3">
                    <div class="text-sm">
                      {{ reservation.book.authors.map((a) => a.name).join(', ') }}
                    </div>
                  </td>
                  <td class="p-3 text-sm">
                    {{ formatDate(reservation.reservationDate) }}
                  </td>
                  <td class="p-3 text-sm">
                    {{ formatDate(reservation.expirationDate) }}
                  </td>
                  <td class="p-3">
                    <span
                      :class="getStatusClass(reservation)"
                      class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium"
                    >
                      {{ getStatusText(reservation) }}
                    </span>
                  </td>
                  <td class="p-3">
                    <div class="flex gap-2">
                      <button
                        class="px-3 py-1 bg-primary text-primary-foreground rounded-md text-sm hover:bg-primary/90 transition-colors"
                        @click="viewBook(reservation.book.id)"
                      >
                        View Book
                      </button>
                      <button
                        v-if="reservation.isActive"
                        class="px-3 py-1 bg-destructive text-destructive-foreground rounded-md text-sm hover:bg-destructive/90 transition-colors"
                        @click="cancelReservation(reservation.id)"
                        :disabled="isCancelling"
                      >
                        {{ isCancelling ? 'Cancelling...' : 'Cancel' }}
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { reservationsService } from '@/lib/api'
import type { Reservation } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const router = useRouter()

// Reactive data
const isLoading = ref(false)
const isCancelling = ref(false)
const reservations = ref<Reservation[]>([])

// Methods
const fetchReservations = async () => {
  try {
    isLoading.value = true
    const response = await reservationsService.getUserReservations({ page: 0, size: 100 })
    reservations.value = response.content
  } catch (error) {
    console.error('Failed to fetch reservations:', error)
    toast.error('Failed to load reservations')
  } finally {
    isLoading.value = false
  }
}

const cancelReservation = async (reservationId: number) => {
  if (!confirm('Are you sure you want to cancel this reservation?')) {
    return
  }

  try {
    isCancelling.value = true
    await reservationsService.cancelReservation(reservationId)
    toast.success('Reservation cancelled successfully!')
    // Refresh the reservations list
    await fetchReservations()
  } catch (error) {
    console.error('Failed to cancel reservation:', error)
    toast.error('Failed to cancel reservation. Please try again.')
  } finally {
    isCancelling.value = false
  }
}

const viewBook = (bookId: number) => {
  router.push(`/books/${bookId}`)
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

const getStatusClass = (reservation: Reservation) => {
  if (!reservation.isActive) {
    return 'bg-gray-100 text-gray-800'
  }

  const expirationDate = new Date(reservation.expirationDate)
  const now = new Date()

  if (expirationDate < now) {
    return 'bg-red-100 text-red-800'
  }

  const daysUntilExpiration = Math.ceil(
    (expirationDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24),
  )
  if (daysUntilExpiration <= 2) {
    return 'bg-yellow-100 text-yellow-800'
  }

  return 'bg-green-100 text-green-800'
}

const getStatusText = (reservation: Reservation) => {
  if (!reservation.isActive) {
    return 'Cancelled'
  }

  const expirationDate = new Date(reservation.expirationDate)
  const now = new Date()

  if (expirationDate < now) {
    return 'Expired'
  }

  const daysUntilExpiration = Math.ceil(
    (expirationDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24),
  )
  if (daysUntilExpiration <= 2) {
    return 'Expiring Soon'
  }

  return 'Active'
}

// Lifecycle
onMounted(() => {
  fetchReservations()
})
</script>

<style scoped>
table {
  border-collapse: collapse;
}
</style>
