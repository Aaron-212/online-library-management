<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold">Billing Center</h1>
      <p class="text-muted-foreground">Manage your fees and payments</p>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-8">
      <div class="text-muted-foreground">Loading billing information...</div>
    </div>

    <template v-else>
      <!-- Overdue Fees -->
      <div class="bg-background border rounded-lg shadow-sm">
        <div class="p-4 border-b">
          <h2 class="text-xl font-semibold">Overdue Fees</h2>
          <p class="text-sm text-muted-foreground">Fees for late book returns</p>
        </div>
        <div class="p-4">
          <div v-if="overdueFees.length === 0" class="text-center py-8 text-muted-foreground">
            No overdue fees found
          </div>
          <div v-else class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b">
                  <th class="text-left p-3">Borrow Record ID</th>
                  <th class="text-left p-3">Fee Amount</th>
                  <th class="text-left p-3">Status</th>
                  <th class="text-left p-3">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="fee in overdueFees" :key="fee.id" class="border-b">
                  <td class="p-3">{{ fee.borrowId }}</td>
                  <td class="p-3">${{ fee.amount.toFixed(2) }}</td>
                  <td class="p-3">
                    <span
                      v-if="fee.paid"
                      class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800"
                    >
                      Paid
                    </span>
                    <span
                      v-else
                      class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800"
                    >
                      Unpaid
                    </span>
                  </td>
                  <td class="p-3">
                    <button
                      v-if="!fee.paid"
                      class="px-3 py-1 bg-primary text-primary-foreground rounded-md text-sm hover:bg-primary/90 transition-colors"
                      @click="payFee(fee.id)"
                      :disabled="isPayingFee"
                    >
                      {{ isPayingFee ? 'Processing...' : 'Pay Fee' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Summary -->
      <div class="bg-background border rounded-lg shadow-sm">
        <div class="p-4 border-b">
          <h2 class="text-xl font-semibold">Payment Summary</h2>
        </div>
        <div class="p-4">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="text-center">
              <div class="text-2xl font-bold text-primary">{{ totalUnpaidFees }}</div>
              <div class="text-sm text-muted-foreground">Unpaid Fees</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-green-600">
                ${{ totalUnpaidAmount.toFixed(2) }}
              </div>
              <div class="text-sm text-muted-foreground">Total Unpaid Amount</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-blue-600">{{ totalPaidFees }}</div>
              <div class="text-sm text-muted-foreground">Paid Fees</div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { feesService } from '@/lib/api'
import type { FeeResponseDto } from '@/lib/api/types'
import { toast } from 'vue-sonner'

// Reactive data
const isLoading = ref(false)
const isPayingFee = ref(false)
const overdueFees = ref<FeeResponseDto[]>([])

// Computed properties
const totalUnpaidFees = computed(() => {
  return overdueFees.value.filter((fee) => !fee.paid).length
})

const totalPaidFees = computed(() => {
  return overdueFees.value.filter((fee) => fee.paid).length
})

const totalUnpaidAmount = computed(() => {
  return overdueFees.value.filter((fee) => !fee.paid).reduce((sum, fee) => sum + fee.amount, 0)
})

// Methods
const fetchFees = async () => {
  try {
    isLoading.value = true
    const response = await feesService.getUserFees({ page: 0, size: 100 })
    overdueFees.value = response.content
  } catch (error) {
    console.error('Failed to fetch fees:', error)
    toast.error('Failed to load billing information')
  } finally {
    isLoading.value = false
  }
}

const payFee = async (feeId: number) => {
  if (!confirm('Are you sure you want to pay this fee?')) {
    return
  }

  try {
    isPayingFee.value = true
    await feesService.payFee(feeId)
    toast.success('Fee paid successfully!')
    // Refresh the fees list
    await fetchFees()
  } catch (error) {
    console.error('Failed to pay fee:', error)
    toast.error('Failed to pay fee. Please try again.')
  } finally {
    isPayingFee.value = false
  }
}

// Lifecycle
onMounted(() => {
  fetchFees()
})
</script>

<style scoped>
table {
  border-collapse: collapse;
}
</style>
