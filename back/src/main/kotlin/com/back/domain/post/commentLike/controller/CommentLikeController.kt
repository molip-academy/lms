package com.back.domain.post.commentLike.controller

import com.back.domain.post.comment.service.CommentService
import com.back.domain.post.commentLike.repository.CommentLikeRepository
import com.back.global.dto.LikeResultDto
import com.back.global.exception.ServiceException
import com.back.global.rq.Rq
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comments/{commentId}/likes")
class CommentLikeController(
	private val commentService: CommentService,
	private val commentLikeRepository: CommentLikeRepository,
	private val rq: Rq,
) {
	@PostMapping
	@Transactional
	fun like(@PathVariable commentId: Long): LikeResultDto {
		val comment = commentService.findById(commentId)
		val actor = rq.actor

		if (commentLikeRepository.findByCommentIdAndMemberId(comment.id, actor.id) != null) {
			throw ServiceException.conflict("이미 추천한 댓글입니다.")
		}

		commentLikeRepository.save(comment.addLike(rq.actorRef()))
		return LikeResultDto(likeCount = comment.likeCount, liked = true)
	}

	@DeleteMapping
	@Transactional
	fun cancel(@PathVariable commentId: Long): LikeResultDto {
		val comment = commentService.findById(commentId)
		val actor = rq.actor

		val like = commentLikeRepository.findByCommentIdAndMemberId(comment.id, actor.id)
			?: throw ServiceException.notFound("추천하지 않은 댓글입니다.")

		comment.removeLike(like)
		commentLikeRepository.delete(like)
		return LikeResultDto(likeCount = comment.likeCount, liked = false)
	}
}
