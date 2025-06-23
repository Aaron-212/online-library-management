import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import { useAuthStore } from '@/stores/auth'
import BorrowingManagementView from '@/views/BorrowingManagementView.vue'
import AdminBorrowingView from '@/views/AdminBorrowingView.vue'
import BorrowingRulesView from '@/views/BorrowingRulesView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/books',
      name: 'books',
      component: () => import('@/views/BooksView.vue'),
    },
    {
      path: '/books/:id',
      name: 'book-detail',
      component: () => import('@/views/BookDetailView.vue'),
      props: true,
    },
    {
      path: '/books/:id/copies',
      name: 'book.copies',
      component: () => import('@/views/BookCopiesView.vue'),
      props: true,
    },
    {
      path: '/books/create',
      name: 'book.create',
      component: () => import('@/views/BookFormView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/books/:id/edit',
      name: 'book.edit',
      component: () => import('@/views/BookFormView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      props: true,
    },
    {
      path: '/borrows',
      name: 'borrows',
      component: BorrowingManagementView,
      meta: { requiresAuth: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/UserProfileView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/notices',
      name: 'notices',
      component: () => import('@/views/NoticesView.vue'),
    },
    {
      path: '/api-test',
      name: 'api-test',
      component: () => import('@/views/ApiTestView.vue'),
    },
    // Admin routes
    {
      path: '/admin/dashboard',
      name: 'admin.dashboard',
      component: () => import('@/views/AdminDashboardView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/books',
      name: 'admin.books',
      component: () => import('@/views/BooksView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/books/create',
      name: 'admin.book-create',
      component: () => import('@/views/BookFormView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/books/:id/edit',
      name: 'admin.book-edit',
      component: () => import('@/views/BookFormView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      props: true,
    },
    {
      path: '/admin/users',
      name: 'admin.users',
      component: () => import('@/views/UserManagementView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/borrowing',
      name: 'admin.borrowing',
      component: AdminBorrowingView,
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/borrowing-rules',
      name: 'admin.borrowing-rules',
      component: BorrowingRulesView,
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/fees',
      name: 'admin.fees',
      component: () => import('@/views/FeesManagementView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/reports',
      name: 'admin.reports',
      component: () => import('@/views/ReportsView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/reservations',
      name: 'reservations',
      component: () => import('@/views/ReservationsView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/favorites',
      name: 'favorites',
      component: () => import('@/views/FavoritesView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/billing',
      name: 'billing',
      component: () => import('@/views/BillingView.vue'),
      meta: { requiresAuth: true },
    },
    // Catch-all route for 404
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
    },
  ],
})

// Navigation guard with proper initialization check
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const isAuthenticated = authStore.isAuthenticated
  const isInitialized = authStore.isInitialized
  const isAdmin = authStore.isAdmin()

  // If auth state is not initialized yet, wait for it
  // This is a safety check, though with the await in main.ts this shouldn't happen
  if (!isInitialized) {
    // This should not happen with the current implementation, but provides a safety net
    console.warn('Router guard called before auth initialization completed')
    next()
    return
  }

  // Redirect authenticated users away from login/register pages
  if (isAuthenticated && (to.name === 'login' || to.name === 'register')) {
    const redirectTo = isAdmin ? '/admin/dashboard' : '/dashboard'
    next(redirectTo)
    return
  }

  // Check authentication requirement
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
    return
  }

  // Check admin requirement
  if (to.meta.requiresAdmin && (!isAuthenticated || !isAdmin)) {
    next('/dashboard')
    return
  }

  // Smart dashboard routing for authenticated users
  if (to.name === 'dashboard' && isAuthenticated && isAdmin) {
    next('/admin/dashboard')
    return
  }

  next()
})

export default router
