package com.back.domain.post.commentLike.repository

import com.back.domain.post.commentLike.entity.CommentLike
import org.springframework.data.jpa.repository.JpaRepository

interface CommentLikeRepository : JpaRepository<CommentLike, Long> {
	fun findByCommentIdAndMemberId(commentId: Long, memberId: Long): CommentLike?
	fun findByMemberIdAndCommentIdIn(memberId: Long, commentIds: Collection<Long>): List<CommentLike>
}
