package com.back.domain.post.post.service

import com.back.domain.member.member.entity.Member
import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.repository.PostRepository
import com.back.global.exception.ServiceException
import com.back.global.security.SecurityUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PostService(
	private val postRepository: PostRepository,
) {
	fun getList(pageable: Pageable): Page<Post> = postRepository.findAll(pageable)

	fun findById(id: Long): Post = postRepository.findWithAuthorById(id)
		?: throw ServiceException.notFound("존재하지 않는 글입니다.")

	fun write(author: Member, title: String, content: String): Post =
		postRepository.save(Post(title = title, content = content, author = author))

	fun delete(post: Post) = postRepository.delete(post)

	/** 수정은 작성자 본인만 가능하다. ADMIN 도 남의 글을 고칠 수는 없다. */
	fun canModify(post: Post, actor: SecurityUser?): Boolean =
		actor != null && post.author.id == actor.id

	/** 삭제는 작성자 본인 또는 ADMIN. 부적절한 글을 조교가 지울 수 있어야 하기 때문이다. */
	fun canDelete(post: Post, actor: SecurityUser?): Boolean =
		actor != null && (post.author.id == actor.id || actor.isAdmin)

	fun checkCanModify(post: Post, actor: SecurityUser?) {
		if (!canModify(post, actor)) throw ServiceException.forbidden("작성자만 수정할 수 있습니다.")
	}

	fun checkCanDelete(post: Post, actor: SecurityUser?) {
		if (!canDelete(post, actor)) throw ServiceException.forbidden("삭제 권한이 없습니다.")
	}
}
