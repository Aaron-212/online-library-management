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

export function useLanguage() {
  const { locale } = useI18n()
  
  // Initialize language from localStorage or default to 'en'
  const initializeLanguage = () => {
    const savedLanguage = localStorage.getItem(STORAGE_KEY)
    if (savedLanguage && AVAILABLE_LANGUAGES.some(lang => lang.value === savedLanguage)) {
      locale.value = savedLanguage
      return savedLanguage
    }
    return 'en'
  }

  const currentLanguage = ref(initializeLanguage())

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