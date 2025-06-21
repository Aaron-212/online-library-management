import './index.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createI18n } from 'vue-i18n'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'

const app = createApp(App)
const pinia = createPinia()
const i18n = createI18n({
  // Disable legacy API mode to use Composition API
  legacy: false,
  // You can add other i18n options here (locale, messages, etc.)
})

app.use(i18n)
app.use(pinia)
app.use(router)

// Initialize auth store after pinia is configured
const authStore = useAuthStore()
authStore.initAuth()

app.mount('#app')
