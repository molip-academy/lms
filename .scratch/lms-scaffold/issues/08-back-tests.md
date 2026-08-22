# back: MockMvc 통합 테스트

Status: done
Blocked by: 07

`@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional` (rollback). **mocking 하지 않는다.**
`test` profile (H2 in-memory, ddl-auto: create).

기능별 성공 1개 + 권한/검증 실패 1~2개:

- 회원가입: 성공 / username 중복 409 / nickname 중복 409 / password 8자 미만 400
- 로그인: 성공(Set-Cookie 확인) / 잘못된 비밀번호 401
- me: 로그인 상태 200 / 비로그인 401
- 로그아웃: Max-Age=0 cookie 확인
- 글: 목록 / 작성 / 상세(viewCount 증가 확인) / 수정 성공 / **남의 글 수정 403** / 삭제 / **남의 글 삭제 403** / ADMIN은 남의 글 삭제 가능
- 댓글: 작성 / 목록 / 수정 / **남의 댓글 수정 403** / 삭제
- 추천: 추천 / 중복 추천 409 / 취소 / 없는 추천 취소 404 / likeCount 반영 확인

이 테스트가 GitHub Actions 배포 gate다.
