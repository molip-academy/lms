package com.back.domain.post.comment.service

import com.back.domain.member.member.entity.Member
import com.back.domain.post.comment.entity.Comment
import com.back.domain.post.comment.repository.CommentRepository
import com.back.domain.post.post.entity.Post
import com.back.global.exception.ServiceException
import com.back.global.security.SecurityUser
import org.springframework.stereotype.Service

@Service
class CommentService(
	private val commentRepository: CommentRepository,
) {
	fun getListByPost(postId: Long): List<Comment> = commentRepository.findByPostIdOrderByIdAsc(postId)

	fun findById(id: Long): Comment = commentRepository.findWithAuthorAndPostById(id)
		?: throw ServiceException.notFound("존재하지 않는 댓글입니다.")

	fun write(post: Post, author: Member, content: String): Comment {
		val comment = Comment(content = content, author = author, post = post)
		post.addComment(comment)
		return commentRepository.save(comment)
	}

	fun delete(comment: Comment) = commentRepository.delete(comment)

	fun canModify(comment: Comment, actor: SecurityUser?): Boolean =
		actor != null && comment.author.id == actor.id

	fun canDelete(comment: Comment, actor: SecurityUser?): Boolean =
		actor != null && (comment.author.id == actor.id || actor.isAdmin)

	fun checkCanModify(comment: Comment, actor: SecurityUser?) {
		if (!canModify(comment, actor)) throw ServiceException.forbidden("작성자만 수정할 수 있습니다.")
	}

	fun checkCanDelete(comment: Comment, actor: SecurityUser?) {
		if (!canDelete(comment, actor)) throw ServiceException.forbidden("삭제 권한이 없습니다.")
	}
}
