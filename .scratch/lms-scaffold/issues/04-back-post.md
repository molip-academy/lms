# back: 글 CRUD + 조회수 + 페이징

Status: done
Blocked by: 03

- `GET /api/v1/posts?page=&size=` — 20건, 최신순. `Page`를 그대로 직렬화하지 않고 `PageDto`로 감싼다.
- `POST /api/v1/posts` — 로그인 필요
- `GET /api/v1/posts/{id}` — **viewCount +1**. 조회 API이지만 write가 있으므로 `readOnly = true`를 붙이지 않는다.
- `PUT /api/v1/posts/{id}` — 작성자 본인만, 아니면 403
- `DELETE /api/v1/posts/{id}` — 작성자 본인 또는 ADMIN, 아니면 403. 댓글/추천은 cascade.

OSIV off이므로 DTO 변환을 transaction 안에서 끝낸다 (controller `@Transactional`이므로 실질적으로 안전하지만, DTO는 entity를 밖으로 내보내지 않는다).
