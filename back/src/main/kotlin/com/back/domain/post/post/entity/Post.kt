package com.back.domain.post.post.entity

import com.back.domain.member.member.entity.Member
import com.back.domain.post.comment.entity.Comment
import com.back.domain.post.postLike.entity.PostLike
import com.back.global.jpa.entity.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

/**
 * `Member` 가 게시판에 작성한 글. 게시판은 하나뿐이므로 분류를 갖지 않는다.
 *
 * `likeCount` 는 비정규화 counter 다. `PostLike` 를 매번 세지 않고 추천/취소 시 함께 증감하며,
 * 그래야 목록 조회에서 N+1 이 생기지 않는다. `viewCount` 와 같은 구조를 유지한다.
 */
@Entity
class Post(
	@Column(nullable = false, length = 200)
	var title: String,

	@Column(nullable = false, columnDefinition = "TEXT")
	var content: String,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	val author: Member,
) : BaseEntity() {
	@Column(nullable = false)
	var viewCount: Int = 0
		protected set

	@Column(nullable = false)
	var likeCount: Int = 0
		protected set

	@OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
	val comments: MutableList<Comment> = mutableListOf()

	@OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
	val likes: MutableList<PostLike> = mutableListOf()

	fun modify(title: String, content: String) {
		this.title = title
		this.content = content
	}

	/** 상세 조회마다 호출된다. 같은 사람의 반복 조회를 구분하지 않는다. */
	fun increaseViewCount() {
		viewCount++
	}

	fun addLike(member: Member): PostLike {
		val like = PostLike(member = member, post = this)
		likes.add(like)
		likeCount++
		return like
	}

	fun removeLike(like: PostLike) {
		likes.remove(like)
		likeCount--
	}

	fun addComment(comment: Comment) {
		comments.add(comment)
	}

	fun removeComment(comment: Comment) {
		comments.remove(comment)
	}
}
