# 운영 DB로 volume 없는 H2 file DB를 쓴다

운영(Railway)에서도 별도 DB 없이 H2 file DB를 쓰기로 했다. Railway container의 filesystem은 재배포마다 초기화되므로, **배포할 때마다 회원·글·댓글이 전부 사라지고 `BaseInitData`의 sample data가 다시 깔린 초기 상태로 돌아간다**. Railway Postgres addon이나 volume mount 둘 다 가능했지만, 지금 단계의 목표가 "인증 + CRUD + 배포 pipeline이 end-to-end로 도는 것"이라 데이터 영속성을 의도적으로 뒤로 미뤘다.

## Consequences

- `BaseInitData`의 "회원이 1명이라도 있으면 중단" guard가 재배포마다 통과한다. 이건 버그가 아니라 이 결정의 정상 동작이다.
- **인증 설계가 이 결정에 의존한다.** access token 단독 7일 만료(refresh token 없음)를 택한 이유가 여기 있다 — 재배포마다 계정 자체가 사라지는데 2주짜리 refresh token을 회수할 대상이 없기 때문이다. 실제 DB를 붙이는 시점이 refresh token 도입 시점이다.
- sample 계정 password를 `password123`으로 통일한 것도 이 결정의 귀결이다. 공개된 데모에 같은 관리자 계정이 매번 다시 깔리므로 `1234` 같은 값을 쓸 수 없다.
- 되돌리는 비용 자체는 낮다(`application-prod.yml` 교체 + Railway Postgres addon). 다만 H2와 Postgres의 방언 차이(예약어, identity 전략)를 그때 한 번 처리해야 한다.
