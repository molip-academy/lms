package com.back.domain.post.comment.repository

import com.back.domain.post.comment.entity.Comment
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
	@EntityGraph(attributePaths = ["author"])
	fun findByPostIdOrderByIdAsc(postId: Long): List<Comment>

	@EntityGraph(attributePaths = ["author", "post"])
	fun findWithAuthorAndPostById(id: Long): Comment?
}
