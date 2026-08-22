package com.back.domain.post

import com.back.domain.post.comment.repository.CommentRepository
import com.back.domain.post.post.repository.PostRepository
import com.back.global.ApiTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CommentControllerTest(
	@Autowired val postRepository: PostRepository,
	@Autowired val commentRepository: CommentRepository,
) : ApiTestSupport() {

	/** BaseInitData 가 댓글 3개를 몰아둔 글. */
	private fun postWithThreeComments() = postRepository.findAll()
		.first { it.comments.size == 3 }

	private fun commentOfUser2() = commentRepository.findAll().first { it.author.username == "user2" }

	@Test
	@DisplayName("댓글 목록은 작성순이고 비로그인도 볼 수 있다")
	fun list() {
		mvc.perform(get("/api/v1/posts/${postWithThreeComments().id}/comments"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.length()").value(3))
			.andExpect(jsonPath("$[0].author.nickname").value("유저2"))
			.andExpect(jsonPath("$[2].author.nickname").value("관리자"))
	}

	@Test
	@DisplayName("댓글이 없는 글은 빈 배열을 준다")
	fun listEmpty() {
		val postWithoutComments = postRepository.findAll().first { it.comments.isEmpty() }
		mvc.perform(get("/api/v1/posts/${postWithoutComments.id}/comments"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.length()").value(0))
	}

	@Test
	@DisplayName("로그인하면 댓글을 쓸 수 있다")
	fun write() {
		val before = commentRepository.count()

		mvc.perform(
			post("/api/v1/posts/${postWithThreeComments().id}/comments")
				.cookie(loginCookie("user4"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"content":"새 댓글입니다."}""")
		)
			.andExpect(status().isCreated)
			.andExpect(jsonPath("$.content").value("새 댓글입니다."))
			.andExpect(jsonPath("$.author.nickname").value("유저4"))
			.andExpect(jsonPath("$.likeCount").value(0))

		assertThat(commentRepository.count()).isEqualTo(before + 1)
	}

	@Test
	@DisplayName("비로그인은 댓글을 쓸 수 없다")
	fun writeAnonymous() {
		mvc.perform(
			post("/api/v1/posts/${postWithThreeComments().id}/comments")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"content":"익명 댓글"}""")
		)
			.andExpect(status().isUnauthorized)
	}

	@Test
	@DisplayName("댓글이 1000자를 넘으면 400")
	fun writeTooLong() {
		mvc.perform(
			post("/api/v1/posts/${postWithThreeComments().id}/comments")
				.cookie(loginCookie("user1"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"content":"${"가".repeat(1001)}"}""")
		)
			.andExpect(status().isBadRequest)
			.andExpect(jsonPath("$.errors.content").exists())
	}

	@Test
	@DisplayName("없는 글에는 댓글을 쓸 수 없다 (404)")
	fun writeToMissingPost() {
		mvc.perform(
			post("/api/v1/posts/99999/comments")
				.cookie(loginCookie("user1"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"content":"내용"}""")
		)
			.andExpect(status().isNotFound)
	}

	@Test
	@DisplayName("작성자는 자기 댓글을 수정할 수 있다")
	fun modify() {
		mvc.perform(
			put("/api/v1/comments/${commentOfUser2().id}")
				.cookie(loginCookie("user2"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"content":"수정된 댓글"}""")
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.content").value("수정된 댓글"))
	}

	@Test
	@DisplayName("남의 댓글은 수정할 수 없다 (403)")
	fun modifyOthersComment() {
		mvc.perform(
			put("/api/v1/comments/${commentOfUser2().id}")
				.cookie(loginCookie("user3"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"content":"가로채기"}""")
		)
			.andExpect(status().isForbidden)
	}

	@Test
	@DisplayName("작성자는 자기 댓글을 삭제할 수 있다")
	fun delete_() {
		val id = commentOfUser2().id
		mvc.perform(delete("/api/v1/comments/$id").cookie(loginCookie("user2")))
			.andExpect(status().isNoContent)

		assertThat(commentRepository.findById(id)).isEmpty
	}

	@Test
	@DisplayName("남의 댓글은 삭제할 수 없다 (403)")
	fun deleteOthersComment() {
		mvc.perform(delete("/api/v1/comments/${commentOfUser2().id}").cookie(loginCookie("user3")))
			.andExpect(status().isForbidden)
	}

	@Test
	@DisplayName("ADMIN 은 남의 댓글을 삭제할 수 있다")
	fun adminCanDeleteOthersComment() {
		val id = commentOfUser2().id
		mvc.perform(delete("/api/v1/comments/$id").cookie(loginCookie("admin")))
			.andExpect(status().isNoContent)

		assertThat(commentRepository.findById(id)).isEmpty
	}
}
