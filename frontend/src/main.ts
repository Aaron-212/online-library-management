import './index.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createI18n } from 'vue-i18n'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import { messages } from './locales'

async function initializeApp() {
  const app = createApp(App)
  const pinia = createPinia()
  const i18n = createI18n({
    // Disable legacy API mode to use Composition API
    legacy: false,
    locale: 'en',
    fallbackLocale: 'en',
    messages
  })

  app.use(i18n)
  app.use(pinia)
  app.use(router)

  // Initialize auth store and wait for authentication state to be restored
  const authStore = useAuthStore()
  await authStore.initAuth()

  app.mount('#app')
}

// Initialize the app
initializeApp().catch(console.error)
