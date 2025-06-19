import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import PersonalCenter from '@/components/ui/sidebar/PersonalCenter.vue'
import PersonalProfile from '@/views/PersonalProfile.vue'
import BorrowRecords from '@/views/BorrowRecords.vue'
import Reservations from '@/views/Reservations.vue'
import Favorites from '@/views/Favorites.vue'
import BillingCenter from '@/views/BillingCenter.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
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
      path: '/personal-center',
      name: 'personal-center',
      component: PersonalCenter,
      meta: { requiresAuth: true },
      children: [
        {
          path: 'profile',
          name: 'personal-profile',
          component: PersonalProfile,
        },
        {
          path: 'borrow-records',
          name: 'borrow-records',
          component: BorrowRecords,
        },
        {
          path: 'reservations',
          name: 'reservations',
          component: Reservations,
        },
        {
          path: 'favorites',
          name: 'favorites',
          component: Favorites,
        },
        {
          path: 'billing',
          name: 'billing-center',
          component: BillingCenter,
        },
      ],
    },
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