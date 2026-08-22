package com.back.domain.post

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

class PostControllerTest(
	@Autowired val postRepository: PostRepository,
) : ApiTestSupport() {

	/** user1 이 쓴 첫 글. BaseInitData 가 만든다. */
	private fun postOfUser1() = postRepository.findAll().first { it.author.username == "user1" }

	private fun postOfUser2() = postRepository.findAll().first { it.author.username == "user2" }

	@Test
	@DisplayName("글 목록은 최신순 20건이고 비로그인도 볼 수 있다")
	fun list() {
		mvc.perform(get("/api/v1/posts"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.content.length()").value(5))
			.andExpect(jsonPath("$.page").value(1))
			.andExpect(jsonPath("$.size").value(20))
			.andExpect(jsonPath("$.totalElements").value(5))
			// 목록에는 본문을 담지 않는다.
			.andExpect(jsonPath("$.content[0].content").doesNotExist())
			.andExpect(jsonPath("$.content[0].author.nickname").exists())
			// 작성자의 username 은 노출하지 않는다.
			.andExpect(jsonPath("$.content[0].author.username").doesNotExist())
	}

	@Test
	@DisplayName("페이지 크기를 지정할 수 있다")
	fun listPaged() {
		mvc.perform(get("/api/v1/posts").param("page", "2").param("size", "2"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.page").value(2))
			.andExpect(jsonPath("$.totalPages").value(3))
	}

	@Test
	@DisplayName("상세 조회는 조회수를 올린다")
	fun itemIncreasesViewCount() {
		val id = postOfUser1().id

		mvc.perform(get("/api/v1/posts/$id"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.viewCount").value(1))

		mvc.perform(get("/api/v1/posts/$id"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.viewCount").value(2))
	}

	@Test
	@DisplayName("비로그인 상세 조회는 canModify/canDelete 가 false 다")
	fun itemAnonymous() {
		mvc.perform(get("/api/v1/posts/${postOfUser1().id}"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.canModify").value(false))
			.andExpect(jsonPath("$.canDelete").value(false))
			.andExpect(jsonPath("$.liked").value(false))
	}

	@Test
	@DisplayName("작성자가 보면 canModify/canDelete 가 true 다")
	fun itemAsAuthor() {
		mvc.perform(get("/api/v1/posts/${postOfUser1().id}").cookie(loginCookie("user1")))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.canModify").value(true))
			.andExpect(jsonPath("$.canDelete").value(true))
	}

	@Test
	@DisplayName("ADMIN 은 남의 글을 수정할 수 없지만 삭제할 수는 있다")
	fun itemAsAdmin() {
		mvc.perform(get("/api/v1/posts/${postOfUser1().id}").cookie(loginCookie("admin")))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.canModify").value(false))
			.andExpect(jsonPath("$.canDelete").value(true))
	}

	@Test
	@DisplayName("없는 글은 404")
	fun itemNotFound() {
		mvc.perform(get("/api/v1/posts/99999"))
			.andExpect(status().isNotFound)
	}

	@Test
	@DisplayName("로그인하면 글을 쓸 수 있다")
	fun write() {
		mvc.perform(
			post("/api/v1/posts")
				.cookie(loginCookie("user1"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"title":"새 글입니다","content":"본문입니다."}""")
		)
			.andExpect(status().isCreated)
			.andExpect(jsonPath("$.title").value("새 글입니다"))
			.andExpect(jsonPath("$.author.nickname").value("유저1"))
			.andExpect(jsonPath("$.viewCount").value(0))
			.andExpect(jsonPath("$.likeCount").value(0))

		assertThat(postRepository.count()).isEqualTo(6)
	}

	@Test
	@DisplayName("비로그인은 글을 쓸 수 없다")
	fun writeAnonymous() {
		mvc.perform(
			post("/api/v1/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"title":"제목","content":"본문"}""")
		)
			.andExpect(status().isUnauthorized)
	}

	@Test
	@DisplayName("제목이 비면 400")
	fun writeBlankTitle() {
		mvc.perform(
			post("/api/v1/posts")
				.cookie(loginCookie("user1"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"title":"","content":"본문"}""")
		)
			.andExpect(status().isBadRequest)
			.andExpect(jsonPath("$.errors.title").exists())
	}

	@Test
	@DisplayName("작성자는 자기 글을 수정할 수 있다")
	fun modify() {
		mvc.perform(
			put("/api/v1/posts/${postOfUser1().id}")
				.cookie(loginCookie("user1"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"title":"수정된 제목","content":"수정된 본문"}""")
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.title").value("수정된 제목"))
	}

	@Test
	@DisplayName("남의 글은 수정할 수 없다 (403)")
	fun modifyOthersPost() {
		mvc.perform(
			put("/api/v1/posts/${postOfUser1().id}")
				.cookie(loginCookie("user2"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"title":"가로채기","content":"가로채기"}""")
		)
			.andExpect(status().isForbidden)
			.andExpect(jsonPath("$.detail").value("작성자만 수정할 수 있습니다."))
	}

	@Test
	@DisplayName("ADMIN 도 남의 글을 수정할 수는 없다 (403)")
	fun adminCannotModifyOthersPost() {
		mvc.perform(
			put("/api/v1/posts/${postOfUser1().id}")
				.cookie(loginCookie("admin"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"title":"관리자수정","content":"관리자수정"}""")
		)
			.andExpect(status().isForbidden)
	}

	@Test
	@DisplayName("작성자는 자기 글을 삭제할 수 있고 댓글도 함께 지워진다")
	fun delete_() {
		val id = postOfUser1().id
		mvc.perform(delete("/api/v1/posts/$id").cookie(loginCookie("user1")))
			.andExpect(status().isNoContent)

		assertThat(postRepository.findById(id)).isEmpty
	}

	@Test
	@DisplayName("남의 글은 삭제할 수 없다 (403)")
	fun deleteOthersPost() {
		mvc.perform(delete("/api/v1/posts/${postOfUser1().id}").cookie(loginCookie("user2")))
			.andExpect(status().isForbidden)
			.andExpect(jsonPath("$.detail").value("삭제 권한이 없습니다."))
	}

	@Test
	@DisplayName("ADMIN 은 남의 글을 삭제할 수 있다")
	fun adminCanDeleteOthersPost() {
		val id = postOfUser2().id
		mvc.perform(delete("/api/v1/posts/$id").cookie(loginCookie("admin")))
			.andExpect(status().isNoContent)

		assertThat(postRepository.findById(id)).isEmpty
	}
}
