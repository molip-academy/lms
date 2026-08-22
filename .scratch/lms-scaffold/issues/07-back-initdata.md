# back: BaseInitData

Status: done
Blocked by: 06

`com.back.global.initData.BaseInitData`에 `baseInitDataApplicationRunner` bean을 만든다. `@Transactional`.

- 회원이 1명이라도 있으면 즉시 중단.
- 회원 5명: `admin`(ADMIN/`관리자`) + `user1`~`user4`(USER/`유저1`~`유저4`), password 전부 `password123`
- 글 5개를 `user1`~`user4`에 분산 (한 명이 2개)
- 댓글 5개: 한 글에 3개 + 다른 글에 2개
- `PostLike`를 글마다 0~3개씩 다르게

운영에서도 재배포마다 DB가 비므로 이 runner가 매번 돈다 (ADR 0002). 의도된 동작이다.
