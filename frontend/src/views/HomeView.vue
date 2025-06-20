<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { 
  Card, 
  CardContent, 
  CardDescription, 
  CardHeader, 
  CardTitle 
} from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { 
  BookOpen, 
  Users, 
  Search, 
  LogIn, 
  UserPlus,
  Clock,
  Star,
  TrendingUp,
  Bell,
  ArrowRight,
  Library,
  Heart,
  Eye
} from 'lucide-vue-next'
import { booksService, noticesService, statisticsService } from '@/lib/api'
import type { Book, Notice, BookStatisticsDto } from '@/lib/api/types'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()

// Data
const isLoading = ref(false)
const featuredBooks = ref<Book[]>([])
const popularBooks = ref<Book[]>([])
const recentNotices = ref<Notice[]>([])
const statistics = ref<BookStatisticsDto | null>(null)

// Computed
const heroTitle = computed(() => {
  if (authStore.isAuthenticated) {
    return `Welcome back, ${authStore.user?.firstName || authStore.user?.username || 'Reader'}!`
  }
  return 'Welcome to Your Digital Library'
})

const heroSubtitle = computed(() => {
  if (authStore.isAuthenticated) {
    return 'Discover new worlds, continue your reading journey, and explore our vast collection.'
  }
  return 'Discover thousands of books, manage your reading journey, and connect with a community of readers.'
})

// Quick action buttons for different user states
const quickActions = computed(() => {
  if (authStore.isAuthenticated) {
    return [
      {
        title: 'Browse Books',
        description: 'Explore our collection',
        icon: Search,
        action: () => router.push('/books'),
        variant: 'default' as const,
        featured: true
      },
      {
        title: 'My Borrows',
        description: 'View borrowed books',
        icon: Clock,
        action: () => router.push('/borrows'),
        variant: 'outline' as const
      },
      {
        title: 'Dashboard',
        description: 'Your reading stats',
        icon: TrendingUp,
        action: () => router.push('/dashboard'),
        variant: 'outline' as const
      }
    ]
  } else {
    return [
      {
        title: 'Sign In',
        description: 'Access your account',
        icon: LogIn,
        action: () => router.push('/login'),
        variant: 'default' as const,
        featured: true
      },
      {
        title: 'Register',
        description: 'Join our community',
        icon: UserPlus,
        action: () => router.push('/register'),
        variant: 'outline' as const,
        featured: true
      },
      {
        title: 'Browse Books',
        description: 'Explore our collection',
        icon: Search,
        action: () => router.push('/books'),
        variant: 'outline' as const
      }
    ]
  }
})

// Library features showcase
const libraryFeatures = [
  {
    icon: BookOpen,
    title: 'Vast Collection',
    description: 'Access thousands of books across all genres and categories.',
    color: 'text-blue-600',
    bgColor: 'bg-blue-50'
  },
  {
    icon: Users,
    title: 'Community',
    description: 'Connect with fellow readers and share your reading experience.',
    color: 'text-green-600',
    bgColor: 'bg-green-50'
  },
  {
    icon: Clock,
    title: 'Easy Management',
    description: 'Track your borrowed books and manage your reading schedule.',
    color: 'text-purple-600',
    bgColor: 'bg-purple-50'
  },
  {
    icon: Heart,
    title: 'Personalized',
    description: 'Get recommendations based on your reading preferences.',
    color: 'text-red-600',
    bgColor: 'bg-red-50'
  }
]

// Methods
const loadFeaturedBooks = async () => {
  try {
    const response = await booksService.getAll({ 
      page: 0, 
      size: 6, 
      sort: 'id,desc' 
    })
    featuredBooks.value = response.content
  } catch (error) {
    console.error('Error loading featured books:', error)
  }
}

const loadPopularBooks = async () => {
  try {
    // This would ideally be a separate endpoint for popular books
    // For now, we'll use a different sort or the same endpoint
    const response = await booksService.getAll({ 
      page: 0, 
      size: 4, 
      sort: 'title,asc' 
    })
    popularBooks.value = response.content
  } catch (error) {
    console.error('Error loading popular books:', error)
  }
}

const loadRecentNotices = async () => {
  try {
    const response = await noticesService.getAll({ 
      page: 0, 
      size: 3 
    })
    recentNotices.value = response.content
  } catch (error) {
    console.error('Error loading recent notices:', error)
  }
}

const loadStatistics = async () => {
  try {
    statistics.value = await statisticsService.getBookStatistics()
  } catch (error) {
    console.error('Error loading statistics:', error)
  }
}

const loadHomeData = async () => {
  isLoading.value = true
  try {
    await Promise.all([
      loadFeaturedBooks(),
      loadPopularBooks(),
      loadRecentNotices(),
      loadStatistics()
    ])
  } catch (error) {
    toast.error('Failed to load some content')
  } finally {
    isLoading.value = false
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString()
}

const goToBookDetail = (bookId: number) => {
  router.push(`/books/${bookId}`)
}

// Lifecycle
onMounted(() => {
  loadHomeData()
})
</script>

<template>
  <div class="home-view space-y-8">
    <!-- Hero Section -->
    <section class="relative overflow-hidden rounded-lg bg-gradient-to-br from-primary/10 via-primary/5 to-background p-8 md:p-12">
      <div class="relative z-10">
        <div class="flex items-center gap-3 mb-4">
          <Library class="h-8 w-8 text-primary" />
          <h1 class="text-4xl md:text-5xl font-bold tracking-tight">
            {{ heroTitle }}
          </h1>
        </div>
        <p class="text-xl text-muted-foreground mb-8 max-w-2xl">
          {{ heroSubtitle }}
        </p>
        
        <!-- Quick Actions -->
        <div class="flex flex-wrap gap-4">
          <Button
            v-for="action in quickActions.filter(a => a.featured)"
            :key="action.title"
            :variant="action.variant"
            size="lg"
            class="shadow-lg hover:shadow-xl transition-all"
            @click="action.action"
          >
            <component :is="action.icon" class="h-5 w-5 mr-2" />
            {{ action.title }}
          </Button>
        </div>

        <!-- Statistics (if available) -->
        <div v-if="statistics" class="mt-8 grid grid-cols-2 md:grid-cols-4 gap-4">
          <div class="text-center">
            <div class="text-2xl font-bold text-primary">{{ statistics.totalBooks?.toLocaleString() || 0 }}</div>
            <div class="text-sm text-muted-foreground">Total Books</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-green-600">{{ statistics.availableBooks?.toLocaleString() || 0 }}</div>
            <div class="text-sm text-muted-foreground">Available</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-orange-600">{{ statistics.activeBorrows?.toLocaleString() || 0 }}</div>
            <div class="text-sm text-muted-foreground">Active Borrows</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-purple-600">{{ featuredBooks.length }}</div>
            <div class="text-sm text-muted-foreground">Featured Today</div>
          </div>
        </div>
      </div>
      
      <!-- Decorative background elements -->
      <div class="absolute top-0 right-0 w-64 h-64 bg-primary/5 rounded-full -translate-y-32 translate-x-32"></div>
      <div class="absolute bottom-0 left-0 w-48 h-48 bg-primary/3 rounded-full translate-y-24 -translate-x-24"></div>
    </section>

    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-12">
      <div class="animate-spin inline-block w-6 h-6 border-[3px] border-current border-t-transparent text-primary rounded-full" />
      <p class="mt-2 text-muted-foreground">Loading library content...</p>
    </div>

    <template v-else>
      <!-- Featured Books Section -->
      <section v-if="featuredBooks.length > 0">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-2xl font-bold">Featured Books</h2>
            <p class="text-muted-foreground">Recently added to our collection</p>
          </div>
          <Button variant="outline" @click="router.push('/books')">
            View All
            <ArrowRight class="h-4 w-4 ml-2" />
          </Button>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <Card
            v-for="book in featuredBooks"
            :key="book.id"
            class="group cursor-pointer hover:shadow-lg transition-all duration-200 hover:-translate-y-1"
            @click="goToBookDetail(book.id)"
          >
            <CardHeader class="pb-3">
              <div class="flex items-start justify-between">
                <div class="flex-1 min-w-0">
                  <CardTitle class="text-lg line-clamp-2 group-hover:text-primary transition-colors">
                    {{ book.title }}
                  </CardTitle>
                  <CardDescription class="mt-1">
                    {{ book.authors.map(a => a.name).join(', ') }}
                  </CardDescription>
                </div>
                <Badge 
                  :variant="book.availableQuantity > 0 ? 'success' : 'destructive'" 
                  class="ml-2 shrink-0"
                >
                  {{ book.availableQuantity > 0 ? 'Available' : 'Out of Stock' }}
                </Badge>
              </div>
            </CardHeader>
            <CardContent>
              <div v-if="book.description" class="text-sm text-muted-foreground line-clamp-3 mb-3">
                {{ book.description }}
              </div>
              <div class="flex items-center justify-between text-xs text-muted-foreground">
                <span>{{ book.category?.name || 'General' }}</span>
                <span>{{ book.availableQuantity }}/{{ book.totalQuantity }} available</span>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      <!-- Quick Actions Grid (for authenticated users) -->
      <section v-if="authStore.isAuthenticated">
        <h2 class="text-2xl font-bold mb-6">Quick Actions</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Button
            v-for="action in quickActions"
            :key="action.title"
            :variant="action.variant"
            class="h-auto p-6 flex flex-col items-center gap-3 text-center"
            @click="action.action"
          >
            <component :is="action.icon" class="h-8 w-8" />
            <div>
              <div class="font-semibold">{{ action.title }}</div>
              <div class="text-sm opacity-80">{{ action.description }}</div>
            </div>
          </Button>
        </div>
      </section>

      <!-- Library Features (for non-authenticated users) -->
      <section v-if="!authStore.isAuthenticated">
        <div class="text-center mb-8">
          <h2 class="text-2xl font-bold mb-2">Why Choose Our Library?</h2>
          <p class="text-muted-foreground">Discover the benefits of joining our digital library community</p>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <Card v-for="feature in libraryFeatures" :key="feature.title" class="text-center p-6">
            <div :class="[feature.bgColor, 'w-12 h-12 rounded-lg flex items-center justify-center mx-auto mb-4']">
              <component :is="feature.icon" :class="[feature.color, 'h-6 w-6']" />
            </div>
            <h3 class="font-semibold mb-2">{{ feature.title }}</h3>
            <p class="text-sm text-muted-foreground">{{ feature.description }}</p>
          </Card>
        </div>
      </section>

      <!-- Notices Section -->
      <section v-if="recentNotices.length > 0">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-2xl font-bold">Latest Notices</h2>
            <p class="text-muted-foreground">Stay updated with library announcements</p>
          </div>
          <Button variant="outline" @click="router.push('/notices')">
            <Bell class="h-4 w-4 mr-2" />
            View All Notices
          </Button>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <Card
            v-for="notice in recentNotices"
            :key="notice.id"
            class="hover:shadow-md transition-shadow cursor-pointer"
            @click="router.push('/notices')"
          >
            <CardHeader>
              <CardTitle class="text-lg line-clamp-2">{{ notice.title }}</CardTitle>
              <CardDescription>{{ formatDate(notice.publishDate) }}</CardDescription>
            </CardHeader>
            <CardContent>
              <p class="text-sm text-muted-foreground line-clamp-3">{{ notice.content }}</p>
            </CardContent>
          </Card>
        </div>
      </section>
    </template>
  </div>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
