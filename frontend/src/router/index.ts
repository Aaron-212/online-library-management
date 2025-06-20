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
      path: '/books/create',
      name: 'book-create',
      component: () => import('@/views/BookFormView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/books/:id/edit',
      name: 'book-edit',
      component: () => import('@/views/BookFormView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      props: true,
    },
    {
      path: '/borrows',
      name: 'borrows',
      component: () => import('@/views/BorrowingManagementView.vue'),
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
      name: 'admin-dashboard',
      component: () => import('@/views/AdminDashboardView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/books',
      name: 'admin-books',
      component: () => import('@/views/BooksView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('@/views/UserManagementView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/borrowing',
      name: 'admin-borrowing',
      component: () => import('@/views/AdminBorrowingView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/borrowing-rules',
      name: 'admin-borrowing-rules',
      component: () => import('@/views/BorrowingRulesView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/fees',
      name: 'admin-fees',
      component: () => import('@/views/FeesManagementView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/reports',
      name: 'admin-reports',
      component: () => import('@/views/ReportsView.vue'), // Will create this
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/borrowing',
      name: 'borrowing',
      component: BorrowingManagementView,
      meta: { requiresAuth: true }
    },
    {
      path: '/borrowing-rules',
      name: 'borrowing-rules',
      component: BorrowingRulesView,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    // Catch-all route for 404
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
    },
  ],
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // Redirect authenticated users away from login/register pages
  if (authStore.isAuthenticated && (to.name === 'login' || to.name === 'register')) {
    next('/dashboard')
    return
  }

  // Check if route requires authentication
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }

  // Check if route requires admin access
  if (to.meta.requiresAdmin && authStore.isAuthenticated) {
    if (!authStore.isAdmin()) {
      // Redirect non-admin users to dashboard
      next('/dashboard')
      return
    }
  }

  // Smart dashboard routing: redirect authenticated users to appropriate dashboard
  if (to.name === 'dashboard' && authStore.isAuthenticated) {
    if (authStore.isAdmin()) {
      // Redirect admins to admin dashboard unless they specifically want user dashboard
      if (from.name !== 'admin-dashboard' && !from.path?.includes('/admin/')) {
        next('/admin/dashboard')
        return
      }
    }
    // Regular users go to regular dashboard (no redirect needed)
  }

  // Redirect to appropriate dashboard if user is already authenticated and trying to access login/register
  if ((to.name === 'login' || to.name === 'register') && authStore.isAuthenticated) {
    // Redirect admins to admin dashboard, regular users to user dashboard
    if (authStore.isAdmin()) {
      next('/admin/dashboard')
    } else {
      next('/dashboard')
    }
    return
  }

  next()
})

export default router
