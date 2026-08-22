# front: Vite + React + Tailwind v4 + shadcn + Pretendard + 다크모드

Status: done

`front-react/`를 세운다.

- Vite + React + TypeScript
- Tailwind v4 (`@tailwindcss/vite`), shadcn/ui base color `zinc`
- 다크모드: shadcn CSS 변수 + `class` 전략 + toggle 버튼, 선택을 localStorage에 저장
- Pretendard **dynamic subset**을 jsDelivr CDN에서 로드하고 Tailwind font stack 최상단에 놓는다
- React Router v7, TanStack Query, react-hook-form, zod 설치
- dev proxy: `/api` → `http://localhost:8080` (Worker proxy와 같은 경로 모양을 로컬에서도 재현)
- `npm run build`가 통과하면 완료
