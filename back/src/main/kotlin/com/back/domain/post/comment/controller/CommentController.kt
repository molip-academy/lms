package com.back.domain.post.comment.controller

import com.back.domain.post.comment.dto.CommentDto
import com.back.domain.post.comment.dto.CommentWriteReqBody
import com.back.domain.post.comment.entity.Comment
import com.back.domain.post.comment.service.CommentService
import com.back.domain.post.commentLike.repository.CommentLikeRepository
import com.back.domain.post.post.service.PostService
import com.back.global.rq.Rq
import com.back.global.security.SecurityUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class CommentController(
	private val commentService: CommentService,
	private val commentLikeRepository: CommentLikeRepository,
	private val postService: PostService,
	private val rq: Rq,
) {
	@GetMapping("/posts/{postId}/comments")
	@Transactional(readOnly = true)
	fun list(@PathVariable postId: Long): List<CommentDto> {
		val comments = commentService.getListByPost(postId)
		val actor = rq.actorOrNull

		// 댓글마다 추천 여부를 조회하면 N+1 이 된다. 한 번에 가져온다.
		val likedIds: Set<Long> = actor?.let {
			if (comments.isEmpty()) emptySet()
			else commentLikeRepository
				.findByMemberIdAndCommentIdIn(it.id, comments.map { c -> c.id })
				.map { like -> like.comment.id }
				.toSet()
		} ?: emptySet()

		return comments.map { toDto(it, it.id in likedIds, actor) }
	}

	@PostMapping("/posts/{postId}/comments")
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	fun write(@PathVariable postId: Long, @RequestBody @Valid body: CommentWriteReqBody): CommentDto {
		val post = postService.findById(postId)
		val comment = commentService.write(post, rq.actorRef(), body.content)
		return CommentDto(comment, liked = false, canModify = true, canDelete = true)
	}

	@PutMapping("/comments/{id}")
	@Transactional
	fun modify(@PathVariable id: Long, @RequestBody @Valid body: CommentWriteReqBody): CommentDto {
		val comment = commentService.findById(id)
		commentService.checkCanModify(comment, rq.actorOrNull)
		comment.modify(body.content)

		val actor = rq.actorOrNull
		val liked = actor != null &&
			commentLikeRepository.findByCommentIdAndMemberId(comment.id, actor.id) != null
		return toDto(comment, liked, actor)
	}

	@DeleteMapping("/comments/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	fun delete(@PathVariable id: Long) {
		val comment = commentService.findById(id)
		commentService.checkCanDelete(comment, rq.actorOrNull)
		comment.post.removeComment(comment)
		commentService.delete(comment)
	}

	private fun toDto(comment: Comment, liked: Boolean, actor: SecurityUser?) = CommentDto(
		comment = comment,
		liked = liked,
		canModify = commentService.canModify(comment, actor),
		canDelete = commentService.canDelete(comment, actor),
	)
}
