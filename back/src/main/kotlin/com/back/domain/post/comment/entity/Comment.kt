package com.back.domain.post.comment.entity

import com.back.domain.member.member.entity.Member
import com.back.domain.post.commentLike.entity.CommentLike
import com.back.domain.post.post.entity.Post
import com.back.global.jpa.entity.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

/**
 * `Post` 에 달린 `Member` 의 답글. `Comment` 에는 `Comment` 를 달 수 없다.
 */
@Entity
class Comment(
	@Column(nullable = false, length = 1000)
	var content: String,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	val author: Member,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	val post: Post,
) : BaseEntity() {
	@Column(nullable = false)
	var likeCount: Int = 0
		protected set

	@OneToMany(mappedBy = "comment", cascade = [CascadeType.ALL], orphanRemoval = true)
	val likes: MutableList<CommentLike> = mutableListOf()

	fun modify(content: String) {
		this.content = content
	}

	fun addLike(member: Member): CommentLike {
		val like = CommentLike(member = member, comment = this)
		likes.add(like)
		likeCount++
		return like
	}

	fun removeLike(like: CommentLike) {
		likes.remove(like)
		likeCount--
	}
}
