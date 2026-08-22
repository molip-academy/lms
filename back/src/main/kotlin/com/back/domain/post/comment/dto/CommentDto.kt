package com.back.domain.post.comment.dto

import com.back.domain.member.member.dto.AuthorDto
import com.back.domain.post.comment.entity.Comment
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CommentDto(
	val id: Long,
	val createDate: LocalDateTime,
	val modifyDate: LocalDateTime,
	val content: String,
	val author: AuthorDto,
	val likeCount: Int,
	val liked: Boolean,
	val canModify: Boolean,
	val canDelete: Boolean,
) {
	constructor(comment: Comment, liked: Boolean, canModify: Boolean, canDelete: Boolean) : this(
		id = comment.id,
		createDate = comment.createDate,
		modifyDate = comment.modifyDate,
		content = comment.content,
		author = AuthorDto(comment.author),
		likeCount = comment.likeCount,
		liked = liked,
		canModify = canModify,
		canDelete = canDelete,
	)
}

data class CommentWriteReqBody(
	@field:NotBlank(message = "댓글 내용을 입력해주세요.")
	@field:Size(min = 1, max = 1000, message = "댓글은 1~1000자여야 합니다.")
	val content: String,
)
