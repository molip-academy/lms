# back: 댓글 CRUD

Status: done
Blocked by: 04

- `GET /api/v1/posts/{id}/comments` — 작성순
- `POST /api/v1/posts/{id}/comments` — 로그인 필요, 1~1000자
- `PUT /api/v1/comments/{id}` — 작성자 본인만
- `DELETE /api/v1/comments/{id}` — 작성자 본인 또는 ADMIN

`Comment`에 `Comment`를 달 수 없다 (대댓글 없음).
