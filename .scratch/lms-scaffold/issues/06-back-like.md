# back: 추천 (Post / Comment)

Status: done
Blocked by: 05

- `POST /api/v1/posts/{id}/likes` / `DELETE /api/v1/posts/{id}/likes`
- `POST /api/v1/comments/{id}/likes` / `DELETE /api/v1/comments/{id}/likes`

toggle이 아니라 POST/DELETE로 나눈다 (재시도해도 상태가 뒤집히지 않도록).

- 이미 추천한 대상에 POST → 409
- 추천하지 않은 대상에 DELETE → 404
- `likeCount` 비정규화 counter를 같은 transaction에서 증감한다.
- 응답 DTO에 `likeCount`와 `liked`(현재 로그인 사용자의 추천 여부)를 담는다.
