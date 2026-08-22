package com.back.domain.member

import com.back.global.ApiTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MemberControllerTest : ApiTestSupport() {

	private fun signup(username: String, password: String, nickname: String) =
		mvc.perform(
			post("/api/v1/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"username":"$username","password":"$password","nickname":"$nickname"}""")
		)

	@Test
	@DisplayName("회원가입 성공")
	fun signupSuccess() {
		signup("newbie1", "password123", "새내기")
			.andExpect(status().isCreated)
			.andExpect(jsonPath("$.username").value("newbie1"))
			.andExpect(jsonPath("$.nickname").value("새내기"))
			.andExpect(jsonPath("$.role").value("USER"))
			// 응답에 비밀번호가 절대 섞이면 안 된다.
			.andExpect(jsonPath("$.password").doesNotExist())
	}

	@Test
	@DisplayName("아이디가 중복이면 409")
	fun signupDuplicateUsername() {
		signup("user1", "password123", "겹치지않는닉네임")
			.andExpect(status().isConflict)
			.andExpect(jsonPath("$.detail").value("이미 사용 중인 아이디입니다."))
	}

	@Test
	@DisplayName("닉네임이 중복이면 409")
	fun signupDuplicateNickname() {
		signup("brandnew1", "password123", "유저1")
			.andExpect(status().isConflict)
			.andExpect(jsonPath("$.detail").value("이미 사용 중인 닉네임입니다."))
	}

	@Test
	@DisplayName("비밀번호가 8자 미만이면 400이고 필드별 메시지를 준다")
	fun signupShortPassword() {
		signup("shorty1", "1234", "짧은비번")
			.andExpect(status().isBadRequest)
			.andExpect(jsonPath("$.errors.password").exists())
	}

	@Test
	@DisplayName("아이디에 대문자가 들어가면 400")
	fun signupInvalidUsername() {
		signup("BadName", "password123", "대문자아이디")
			.andExpect(status().isBadRequest)
			.andExpect(jsonPath("$.errors.username").exists())
	}

	@Test
	@DisplayName("로그인에 성공하면 HttpOnly token cookie 를 내려준다")
	fun loginSuccess() {
		val response = mvc.perform(
			post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"username":"user1","password":"password123"}""")
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.nickname").value("유저1"))
			.andReturn()
			.response

		val cookie = response.getCookie("token")
		assertThat(cookie).isNotNull
		assertThat(cookie!!.isHttpOnly).isTrue()
		assertThat(cookie.value).isNotBlank()
		assertThat(response.getHeader("Set-Cookie")).contains("SameSite=Lax")
	}

	@Test
	@DisplayName("비밀번호가 틀리면 401이고, 계정 존재 여부를 알려주지 않는다")
	fun loginWrongPassword() {
		mvc.perform(
			post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"username":"user1","password":"wrongpassword"}""")
		)
			.andExpect(status().isUnauthorized)
			.andExpect(jsonPath("$.detail").value("아이디 또는 비밀번호가 올바르지 않습니다."))
	}

	@Test
	@DisplayName("없는 아이디도 틀린 비밀번호와 같은 응답을 준다")
	fun loginUnknownUsername() {
		mvc.perform(
			post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"username":"nosuchuser","password":"password123"}""")
		)
			.andExpect(status().isUnauthorized)
			.andExpect(jsonPath("$.detail").value("아이디 또는 비밀번호가 올바르지 않습니다."))
	}

	@Test
	@DisplayName("로그인 상태에서 내 정보를 조회한다")
	fun meAuthenticated() {
		mvc.perform(get("/api/v1/members/me").cookie(loginCookie("admin")))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.username").value("admin"))
			.andExpect(jsonPath("$.role").value("ADMIN"))
	}

	@Test
	@DisplayName("비로그인 상태로 내 정보를 조회하면 401")
	fun meAnonymous() {
		mvc.perform(get("/api/v1/members/me"))
			.andExpect(status().isUnauthorized)
	}

	@Test
	@DisplayName("로그아웃하면 cookie 를 만료시킨다")
	fun logout() {
		val response = mvc.perform(post("/api/v1/auth/logout").cookie(loginCookie("user1")))
			.andExpect(status().isOk)
			.andReturn()
			.response

		val cookie = response.getCookie("token")
		assertThat(cookie).isNotNull
		assertThat(cookie!!.maxAge).isEqualTo(0)
		assertThat(cookie.value).isEmpty()
	}
}
