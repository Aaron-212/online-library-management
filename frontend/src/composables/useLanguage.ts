import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const STORAGE_KEY = 'library-language'

export interface LanguageOption {
  value: string
  label: string
}

export const AVAILABLE_LANGUAGES: LanguageOption[] = [
  { value: 'en', label: 'English' },
  { value: 'zh-CN', label: '简体中文' }
]

// Initialize language from localStorage or default to 'en'
const initializeLanguage = () => {
  const savedLanguage = localStorage.getItem(STORAGE_KEY)
  if (savedLanguage && AVAILABLE_LANGUAGES.some(lang => lang.value === savedLanguage)) {
    return savedLanguage
  }
  return 'en'
}

// Create a singleton reactive ref that's shared across all composable instances
const currentLanguage = ref(initializeLanguage())

export function useLanguage() {
  const { locale } = useI18n()
  
  // Sync vue-i18n locale with our current language on initialization
  locale.value = currentLanguage.value

  const changeLanguage = (newLanguage: string) => {
    if (AVAILABLE_LANGUAGES.some(lang => lang.value === newLanguage)) {
      currentLanguage.value = newLanguage
      locale.value = newLanguage
      localStorage.setItem(STORAGE_KEY, newLanguage)
    }
  }

  const currentLanguageLabel = computed(() => {
    const lang = AVAILABLE_LANGUAGES.find(lang => lang.value === currentLanguage.value)
    return lang?.label || 'English'
  })

  return {
    currentLanguage,
    currentLanguageLabel,
    availableLanguages: AVAILABLE_LANGUAGES,
    changeLanguage
  }
}
