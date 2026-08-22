package com.back.domain.post.postLike.repository

import com.back.domain.post.postLike.entity.PostLike
import org.springframework.data.jpa.repository.JpaRepository

interface PostLikeRepository : JpaRepository<PostLike, Long> {
	fun findByPostIdAndMemberId(postId: Long, memberId: Long): PostLike?
	fun existsByPostIdAndMemberId(postId: Long, memberId: Long): Boolean
	fun findByMemberIdAndPostIdIn(memberId: Long, postIds: Collection<Long>): List<PostLike>
}
