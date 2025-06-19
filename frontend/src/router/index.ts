import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import { useAuthStore } from '@/stores/auth'
// @ts-ignore
import BooksView from '@/views/BooksView.vue'
// @ts-ignore
import BookDetail from '@/views/BookDetail.vue'

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
      name: 'Books',
      component: BooksView
    },
    {
      path: '/books/:id',
      name: 'BookDetail',
      component: BookDetail,
      props: true
    }
  ],
})

// Navigation guard for authentication
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // Redirect to login if route requires auth and user is not authenticated
    next('/login')
  } else if (to.name === 'login' && authStore.isAuthenticated) {
    // Redirect to dashboard if user is already authenticated and trying to access login
    next('/dashboard')
  } else {
    next()
  }
})

export default router
