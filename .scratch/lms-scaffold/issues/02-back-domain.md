# back: domain entity + repository

Status: done
Blocked by: 01

spec.md의 domain model대로 entity와 repository를 만든다.

- `com.back.domain.member.member.entity.Member` (username uk, nickname uk, role enum USER/ADMIN)
- `com.back.domain.post.post.entity.Post` (viewCount, likeCount)
- `com.back.domain.post.comment.entity.Comment`
- `com.back.domain.post.postLike.entity.PostLike` — uk(member, post)
- `com.back.domain.post.commentLike.entity.CommentLike` — uk(member, comment)

Kotlin + JPA 주의: entity에 `data class`를 쓰지 않는다 (equals/hashCode가 lazy proxy와 충돌).
`kotlin("plugin.jpa")`의 noArg/allOpen에 의존한다.

각 repository는 `JpaRepository`. `existsBy...`, `findBy...` 필요한 것만.
