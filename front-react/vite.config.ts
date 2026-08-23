import { fileURLToPath } from 'node:url'
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { VitePWA } from 'vite-plugin-pwa'

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
    VitePWA({
      // 새 버전을 찾으면 그냥 갈아끼운다. 물어보는 UI 도 만들어봤지만 근거가 없었다 —
      // post-form-page 가 입력을 어디에도 저장하지 않아 지킬 초안 자체가 없고,
      // 그래서 자동 reload 의 위험이 일반 새로고침과 다르지 않다.
      registerType: 'autoUpdate',

      // service worker 는 build 산출물이라 dev server 에서는 켜지 않는다.
      // 실제 동작은 `npm run build && npm run cf:dev` 로 확인한다.
      devOptions: { enabled: false },

      manifest: {
        name: 'MOLIP Academy',
        short_name: 'MOLIP',
        description: 'MOLIP Academy 커뮤니티 게시판',
        lang: 'ko',
        start_url: '/',
        scope: '/',
        display: 'standalone',
        background_color: '#ffffff',
        // light 기준값이다. 실제 상태바 색은 use-theme 이 theme-color meta 를 갱신해 따라간다.
        theme_color: '#ffffff',
        icons: [
          { src: 'pwa-64x64.png', sizes: '64x64', type: 'image/png' },
          { src: 'pwa-192x192.png', sizes: '192x192', type: 'image/png' },
          { src: 'pwa-512x512.png', sizes: '512x512', type: 'image/png' },
          {
            src: 'maskable-icon-512x512.png',
            sizes: '512x512',
            type: 'image/png',
            purpose: 'maskable',
          },
        ],
      },

      workbox: {
        globPatterns: ['**/*.{js,css,html,svg,png,ico,woff2}'],
        // icon 생성용 원본이라 runtime 에는 쓰이지 않는다.
        globIgnores: ['**/pwa-source.svg'],

        // SPA 라 없는 경로는 index.html 로 되돌린다. 단 /api/* 는 예외다 —
        // 여기를 막지 않으면 offline 일 때 API 요청이 index.html 을 받고
        // JSON parse 에서 엉뚱하게 터진다 (ADR 0001 의 proxy 경로).
        navigateFallback: '/index.html',
        navigateFallbackDenylist: [/^\/api\//],

        // /api/* 는 의도적으로 runtimeCaching 에 넣지 않는다.
        // 인증된 응답을 캐시하면 로그아웃 후에도 남거나 다른 계정에 보일 수 있다.
        // 항상 network 로 나간다.

        cleanupOutdatedCaches: true,
      },
    }),
  ],
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
