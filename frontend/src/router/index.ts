import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import { useAuthStore } from '@/stores/auth'

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
      props: true
    },
    {
      path: '/api-test',
      name: 'api-test',
      component: () => import('@/views/ApiTestView.vue'),
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
    // Admin routes (these should have additional admin checks in the components)
    {
      path: '/admin',
      name: 'admin',
      redirect: '/admin/dashboard',
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/dashboard',
      name: 'admin-dashboard',
      component: () => import('@/views/DashboardView.vue'), // Can reuse dashboard with admin features
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/books',
      name: 'admin-books',
      component: () => import('@/views/BooksView.vue'), // Can reuse books view with admin features
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/books/new',
      name: 'admin-books-new',
      component: () => import('@/views/BookFormView.vue'), // Will create this
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/books/:id/edit',
      name: 'admin-books-edit',
      component: () => import('@/views/BookFormView.vue'), // Will create this
      props: true,
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('@/views/UserManagementView.vue'), // Will create this
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/borrowing-rules',
      name: 'admin-borrowing-rules',
      component: () => import('@/views/BorrowingRulesView.vue'), // Will create this
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/fees',
      name: 'admin-fees',
      component: () => import('@/views/FeesManagementView.vue'), // Will create this
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/admin/reports',
      name: 'admin-reports',
      component: () => import('@/views/ReportsView.vue'), // Will create this
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    // Catch-all route for 404
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'), // Will create this
    }
  ],
})

// Navigation guard for authentication
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // Check if route requires authentication
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // Redirect to login if route requires auth and user is not authenticated
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

  // Redirect to dashboard if user is already authenticated and trying to access login/register
  if ((to.name === 'login' || to.name === 'register') && authStore.isAuthenticated) {
    next('/dashboard')
    return
  }

  next()
})

export default router
