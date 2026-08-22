# front: 글 목록 / 상세 / 작성 / 수정 + 추천 + 조회수

Status: done
Blocked by: 11

- `/` 목록 — 20건 페이지네이션(shadcn Pagination), 제목/작성자 닉네임/작성일/조회수/추천수
- `/posts/:id` 상세 — 본문, 조회수, 추천 버튼(낙관적 업데이트), 작성자면 수정/삭제 버튼
- `/posts/new`, `/posts/:id/edit` — 보호 라우트, zod 검증
- 추천은 POST/DELETE 두 mutation. 낙관적 업데이트 후 실패 시 rollback.
- 삭제는 확인 dialog(shadcn AlertDialog) 후 실행, 성공 시 목록으로.

작성자 확인은 화면에서 숨기는 것이고 실제 차단은 back의 403이다.
