package com.back.domain.post.commentLike.entity

import com.back.domain.member.member.entity.Member
import com.back.domain.post.comment.entity.Comment
import com.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

/**
 * `Member` 가 `Comment` 에 표시한 추천. 한 `Member` 는 한 `Comment` 에 한 번만 추천할 수 있다.
 */
@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["member_id", "comment_id"])])
class CommentLike(
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	val member: Member,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	val comment: Comment,
) : BaseEntity()
