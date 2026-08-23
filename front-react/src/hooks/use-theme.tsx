import { createContext, useContext, useEffect, useState, type ReactNode } from "react"

type Theme = "light" | "dark"

const STORAGE_KEY = "molip-theme"

/**
 * PWA 로 설치하면 이 값이 상태바 색이 된다. header 의 `bg-background` 와 어긋나면
 * 상태바만 다른 색인 띠처럼 보이므로 `--background` 의 light/dark 값에 맞춘다.
 *
 * theme 은 prefers-color-scheme 이 아니라 사용자 토글로 정해지므로
 * `<meta media=...>` 로는 따라갈 수 없다. 여기서 직접 갱신해야 한다.
 */
const THEME_COLOR: Record<Theme, string> = {
  light: "#ffffff",
  dark: "#09090b",
}

const ThemeContext = createContext<{ theme: Theme; toggle: () => void } | null>(null)

function initialTheme(): Theme {
  const stored = localStorage.getItem(STORAGE_KEY)
  if (stored === "light" || stored === "dark") return stored
  return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light"
}

export function ThemeProvider({ children }: { children: ReactNode }) {
  const [theme, setTheme] = useState<Theme>(initialTheme)

  useEffect(() => {
    document.documentElement.classList.toggle("dark", theme === "dark")
    localStorage.setItem(STORAGE_KEY, theme)
    document
      .querySelector('meta[name="theme-color"]')
      ?.setAttribute("content", THEME_COLOR[theme])
  }, [theme])

  return (
    <ThemeContext.Provider
      value={{ theme, toggle: () => setTheme((t) => (t === "dark" ? "light" : "dark")) }}
    >
      {children}
    </ThemeContext.Provider>
  )
}

export function useTheme() {
  const context = useContext(ThemeContext)
  if (!context) throw new Error("useTheme must be used within ThemeProvider")
  return context
}
