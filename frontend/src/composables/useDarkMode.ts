import { onMounted, onUnmounted } from 'vue'

export function useDarkMode() {
  const applyDarkMode = (isDark: boolean) => {
    if (isDark) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  const checkSystemDarkMode = () => {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    return mediaQuery.matches
  }

  const handleDarkModeChange = (e: MediaQueryListEvent) => {
    applyDarkMode(e.matches)
  }

  const initDarkMode = () => {
    // Apply initial dark mode based on system preference
    const isDark = checkSystemDarkMode()
    applyDarkMode(isDark)

    // Listen for system dark mode changes
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    mediaQuery.addEventListener('change', handleDarkModeChange)

    return () => {
      mediaQuery.removeEventListener('change', handleDarkModeChange)
    }
  }

  onMounted(() => {
    const cleanup = initDarkMode()
    
    onUnmounted(() => {
      cleanup()
    })
  })

  return {
    initDarkMode
  }
} 
