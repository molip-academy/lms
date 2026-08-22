package com.back.domain.post

import com.back.domain.post.comment.repository.CommentRepository
import com.back.domain.post.post.repository.PostRepository
import com.back.global.ApiTestSupport
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class LikeControllerTest(
	@Autowired val postRepository: PostRepository,
	@Autowired val commentRepository: CommentRepository,
) : ApiTestSupport() {

	/** BaseInitData 가 추천 0개로 둔 글. user1 이 추천한 적 없다. */
	private fun postWithNoLikes() = postRepository.findAll().first { it.likeCount == 0 }

	/** user1 이 이미 추천한 글 (BaseInitData 가 post5 를 admin/user1/user2 로 추천해둔다). */
	private fun postLikedByUser1() = postRepository.findAll().first { p ->
		p.likes.any { it.member.username == "user1" }
	}

	private fun anyComment() = commentRepository.findAll().first()

	@Test
	@DisplayName("글을 추천하면 likeCount 가 오르고 liked 가 true 가 된다")
	fun likePost() {
		mvc.perform(post("/api/v1/posts/${postWithNoLikes().id}/likes").cookie(loginCookie("user1")))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.likeCount").value(1))
			.andExpect(jsonPath("$.liked").value(true))
	}

	@Test
	@DisplayName("이미 추천한 글을 또 추천하면 409")
	fun likeTwice() {
		mvc.perform(post("/api/v1/posts/${postLikedByUser1().id}/likes").cookie(loginCookie("user1")))
			.andExpect(status().isConflict)
			.andExpect(jsonPath("$.detail").value("이미 추천한 글입니다."))
	}

	@Test
	@DisplayName("추천을 취소하면 likeCount 가 줄고 liked 가 false 가 된다")
	fun cancelLike() {
		val post = postLikedByUser1()
		val before = post.likeCount

		mvc.perform(delete("/api/v1/posts/${post.id}/likes").cookie(loginCookie("user1")))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.likeCount").value(before - 1))
			.andExpect(jsonPath("$.liked").value(false))
	}

	@Test
	@DisplayName("추천하지 않은 글의 추천을 취소하면 404")
	fun cancelLikeNotLiked() {
		mvc.perform(delete("/api/v1/posts/${postWithNoLikes().id}/likes").cookie(loginCookie("user1")))
			.andExpect(status().isNotFound)
			.andExpect(jsonPath("$.detail").value("추천하지 않은 글입니다."))
	}

	@Test
	@DisplayName("비로그인은 추천할 수 없다")
	fun likeAnonymous() {
		mvc.perform(post("/api/v1/posts/${postWithNoLikes().id}/likes"))
			.andExpect(status().isUnauthorized)
	}

	@Test
	@DisplayName("추천 후 상세 조회에 liked 와 likeCount 가 반영된다")
	fun likeReflectedInDetail() {
		val id = postWithNoLikes().id
		val cookie = loginCookie("user3")

		mvc.perform(post("/api/v1/posts/$id/likes").cookie(cookie))
			.andExpect(status().isOk)

		mvc.perform(get("/api/v1/posts/$id").cookie(cookie))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.liked").value(true))
			.andExpect(jsonPath("$.likeCount").value(1))
	}

	@Test
	@DisplayName("추천 후 목록에도 liked 가 반영된다")
	fun likeReflectedInList() {
		val id = postWithNoLikes().id
		val cookie = loginCookie("user3")

		mvc.perform(post("/api/v1/posts/$id/likes").cookie(cookie))
			.andExpect(status().isOk)

		mvc.perform(get("/api/v1/posts").cookie(cookie))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.content[?(@.id == $id)].liked").value(true))
	}

	@Test
	@DisplayName("댓글을 추천하고 취소할 수 있다")
	fun likeAndCancelComment() {
		val id = anyComment().id
		val cookie = loginCookie("user4")

		mvc.perform(post("/api/v1/comments/$id/likes").cookie(cookie))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.likeCount").value(1))
			.andExpect(jsonPath("$.liked").value(true))

		mvc.perform(post("/api/v1/comments/$id/likes").cookie(cookie))
			.andExpect(status().isConflict)

		mvc.perform(delete("/api/v1/comments/$id/likes").cookie(cookie))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.likeCount").value(0))
			.andExpect(jsonPath("$.liked").value(false))

		mvc.perform(delete("/api/v1/comments/$id/likes").cookie(cookie))
			.andExpect(status().isNotFound)
	}
}
