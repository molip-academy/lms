import { fileURLToPath } from 'node:url'
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    // 배포에서는 Cloudflare Worker 가 /api/* 를 Railway 로 넘긴다 (ADR 0001).
    // 로컬에서도 같은 경로 모양을 재현해야 same-origin cookie 가 동일하게 동작한다.
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: false,
      },
    },
  },
})
