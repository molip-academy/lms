package com.back.domain.post.postLike.entity

import com.back.domain.member.member.entity.Member
import com.back.domain.post.post.entity.Post
import com.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

/**
 * `Member` 가 `Post` 에 표시한 추천. 한 `Member` 는 한 `Post` 에 한 번만 추천할 수 있다.
 * 값을 세는 counter 가 아니라 누가 무엇을 추천했는지의 기록이다.
 */
@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["member_id", "post_id"])])
class PostLike(
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	val member: Member,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	val post: Post,
) : BaseEntity()
