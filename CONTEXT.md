# MOLIP Academy LMS

back과 front가 공유하는 LMS domain language를 정의한다.

LMS 본체(무엇을 가르치고 어떤 학습 workflow를 갖는지)는 **아직 정해지지 않았다**. 현재 정의된 term은 전부 LMS에 딸린 **커뮤니티 게시판**의 것이며, LMS domain term이 아니다. 학습 workflow가 정해지면 그 term들이 별도 cluster로 추가된다.

## Language

### 계정

**Member**:
로그인할 수 있는 사람. LMS에서의 신분(수강생인지 강사인지)이 아니라 **인증 주체**를 뜻한다.
_Avoid_: User, Account, 사용자

**Role**:
`Member`가 가지는 시스템 권한 등급. `USER`와 `ADMIN` 두 가지뿐이다. 학습상의 신분을 표현하지 **않는다** — 한 사람이 어떤 강의에서는 수강생이고 다른 강의에서는 강사일 수 있으므로, 학습상의 신분은 계정 속성이 아니라 강의와의 관계로 표현되어야 한다.
_Avoid_: Authority, Grade, 등급

**Nickname**:
게시판에서 `Member`를 식별하는 표시용 이름. 유일하다. `Member`의 공개 식별자는 이것뿐이며 `username`은 노출하지 않는다.
_Avoid_: DisplayName, 별명

### 커뮤니티 게시판

**Post**:
`Member`가 게시판에 작성한 글. 게시판은 하나뿐이므로 분류를 갖지 않는다.
_Avoid_: Article, Board, Notice, 게시물

**Comment**:
`Post`에 달린 `Member`의 답글. `Comment`에는 `Comment`를 달 수 없다.
_Avoid_: Reply, 대댓글

**Like**:
`Member`가 `Post` 또는 `Comment`에 표시한 추천. 한 `Member`는 한 대상에 한 번만 추천할 수 있고 취소할 수 있다. 값을 세는 counter가 아니라 **누가 무엇을 추천했는지의 기록**이다.
_Avoid_: Vote, Upvote, Recommend, 좋아요

**ViewCount**:
`Post` 상세 조회 요청이 발생한 횟수. 같은 사람의 반복 조회를 구분하지 않으므로 **고유 독자 수가 아니다**.
_Avoid_: Hits, ReadCount
