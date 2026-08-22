package com.back.domain.post.post.dto

import com.back.domain.member.member.dto.AuthorDto
import com.back.domain.post.post.entity.Post
import java.time.LocalDateTime

/** 목록용. 본문을 담지 않는다. */
data class PostListItemDto(
	val id: Long,
	val createDate: LocalDateTime,
	val modifyDate: LocalDateTime,
	val title: String,
	val author: AuthorDto,
	val viewCount: Int,
	val likeCount: Int,
	val commentCount: Int,
	val liked: Boolean,
) {
	constructor(post: Post, liked: Boolean) : this(
		id = post.id,
		createDate = post.createDate,
		modifyDate = post.modifyDate,
		title = post.title,
		author = AuthorDto(post.author),
		viewCount = post.viewCount,
		likeCount = post.likeCount,
		commentCount = post.comments.size,
		liked = liked,
	)
}

/** 상세용. */
data class PostDto(
	val id: Long,
	val createDate: LocalDateTime,
	val modifyDate: LocalDateTime,
	val title: String,
	val content: String,
	val author: AuthorDto,
	val viewCount: Int,
	val likeCount: Int,
	val liked: Boolean,
	/** front 가 수정/삭제 버튼을 그릴지 결정한다. 실제 차단은 back 의 403 이다. */
	val canModify: Boolean,
	val canDelete: Boolean,
) {
	constructor(post: Post, liked: Boolean, canModify: Boolean, canDelete: Boolean) : this(
		id = post.id,
		createDate = post.createDate,
		modifyDate = post.modifyDate,
		title = post.title,
		content = post.content,
		author = AuthorDto(post.author),
		viewCount = post.viewCount,
		likeCount = post.likeCount,
		liked = liked,
		canModify = canModify,
		canDelete = canDelete,
	)
}
