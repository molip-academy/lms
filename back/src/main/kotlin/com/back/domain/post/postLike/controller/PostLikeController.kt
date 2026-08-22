package com.back.domain.post.postLike.controller

import com.back.domain.post.post.service.PostService
import com.back.domain.post.postLike.repository.PostLikeRepository
import com.back.global.dto.LikeResultDto
import com.back.global.exception.ServiceException
import com.back.global.rq.Rq
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * toggle 이 아니라 POST/DELETE 로 나눈다.
 * 재시도나 더블클릭에 상태가 뒤집히지 않아야 front 의 낙관적 업데이트 rollback 이 성립한다.
 */
@RestController
@RequestMapping("/api/v1/posts/{postId}/likes")
class PostLikeController(
	private val postService: PostService,
	private val postLikeRepository: PostLikeRepository,
	private val rq: Rq,
) {
	@PostMapping
	@Transactional
	fun like(@PathVariable postId: Long): LikeResultDto {
		val post = postService.findById(postId)
		val actor = rq.actor

		if (postLikeRepository.existsByPostIdAndMemberId(post.id, actor.id)) {
			throw ServiceException.conflict("이미 추천한 글입니다.")
		}

		postLikeRepository.save(post.addLike(rq.actorRef()))
		return LikeResultDto(likeCount = post.likeCount, liked = true)
	}

	@DeleteMapping
	@Transactional
	fun cancel(@PathVariable postId: Long): LikeResultDto {
		val post = postService.findById(postId)
		val actor = rq.actor

		val like = postLikeRepository.findByPostIdAndMemberId(post.id, actor.id)
			?: throw ServiceException.notFound("추천하지 않은 글입니다.")

		post.removeLike(like)
		postLikeRepository.delete(like)
		return LikeResultDto(likeCount = post.likeCount, liked = false)
	}
}
