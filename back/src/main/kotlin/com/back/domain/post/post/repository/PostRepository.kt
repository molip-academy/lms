package com.back.domain.post.post.repository

import com.back.domain.post.post.entity.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long> {
	/** 목록에서 작성자 nickname 을 함께 쓰므로 author 를 미리 가져와 N+1 을 막는다. */
	@EntityGraph(attributePaths = ["author"])
	override fun findAll(pageable: Pageable): Page<Post>

	@EntityGraph(attributePaths = ["author"])
	fun findWithAuthorById(id: Long): Post?
}
