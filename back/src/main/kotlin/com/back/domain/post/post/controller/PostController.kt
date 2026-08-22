package com.back.domain.post.post.controller

import com.back.domain.post.post.dto.PostDto
import com.back.domain.post.post.dto.PostListItemDto
import com.back.domain.post.post.dto.PostWriteReqBody
import com.back.domain.post.post.service.PostService
import com.back.domain.post.postLike.repository.PostLikeRepository
import com.back.global.dto.PageDto
import com.back.global.rq.Rq
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/posts")
class PostController(
	private val postService: PostService,
	private val postLikeRepository: PostLikeRepository,
	private val rq: Rq,
) {
	@GetMapping
	@Transactional(readOnly = true)
	fun list(
		@RequestParam(defaultValue = "1") page: Int,
		@RequestParam(defaultValue = "20") size: Int,
	): PageDto<PostListItemDto> {
		val pageable = PageRequest.of(
			(page - 1).coerceAtLeast(0),
			size.coerceIn(1, 100),
			Sort.by(Sort.Direction.DESC, "id"),
		)
		val posts = postService.getList(pageable)

		// 목록의 추천 여부를 글마다 조회하면 N+1 이 된다. 한 번에 가져와 집합으로 만든다.
		val likedIds: Set<Long> = rq.actorOrNull?.let { actor ->
			if (posts.isEmpty) emptySet()
			else postLikeRepository
				.findByMemberIdAndPostIdIn(actor.id, posts.content.map { it.id })
				.map { it.post.id }
				.toSet()
		} ?: emptySet()

		return PageDto.of(posts) { PostListItemDto(it, liked = it.id in likedIds) }
	}

	/**
	 * 조회수를 올리므로 write 가 발생한다. 따라서 readOnly = true 를 붙일 수 없다.
	 * 같은 사람의 반복 조회를 구분하지 않는 것은 의도된 정책이다.
	 */
	@GetMapping("/{id}")
	@Transactional
	fun item(@PathVariable id: Long): PostDto {
		val post = postService.findById(id)
		post.increaseViewCount()

		val actor = rq.actorOrNull
		val liked = actor != null && postLikeRepository.existsByPostIdAndMemberId(post.id, actor.id)

		return PostDto(
			post = post,
			liked = liked,
			canModify = postService.canModify(post, actor),
			canDelete = postService.canDelete(post, actor),
		)
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	fun write(@RequestBody @Valid body: PostWriteReqBody): PostDto {
		val post = postService.write(rq.actorRef(), body.title, body.content)
		return PostDto(post, liked = false, canModify = true, canDelete = true)
	}

	@PutMapping("/{id}")
	@Transactional
	fun modify(@PathVariable id: Long, @RequestBody @Valid body: PostWriteReqBody): PostDto {
		val post = postService.findById(id)
		postService.checkCanModify(post, rq.actorOrNull)
		post.modify(body.title, body.content)

		val actor = rq.actorOrNull
		return PostDto(
			post = post,
			liked = actor != null && postLikeRepository.existsByPostIdAndMemberId(post.id, actor.id),
			canModify = true,
			canDelete = postService.canDelete(post, actor),
		)
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	fun delete(@PathVariable id: Long) {
		val post = postService.findById(id)
		postService.checkCanDelete(post, rq.actorOrNull)
		postService.delete(post)
	}
}
