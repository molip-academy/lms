# front: 로그인 / 회원가입 / 내 정보 화면

Status: done
Blocked by: 10

- `/signup` — username, password, nickname. zod 검증. 서버 400/409를 필드 에러로 표시.
- `/login` — username, password. 실패 시 401 메시지.
- `/me` — 보호 라우트. nickname, username, 가입일, role 표시.

react-hook-form + zodResolver + shadcn Form/Input/Button.
