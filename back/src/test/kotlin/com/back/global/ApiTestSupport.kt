package com.back.global

import jakarta.servlet.http.Cookie
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

/**
 * MockMvc 로 실제 요청을 보내고 test 종료 시 rollback 한다. mocking 하지 않는다.
 *
 * `BaseInitData` 는 test context 기동 시 한 번 실행되어 commit 되므로,
 * 각 test 는 admin / user1~user4 (password 전부 `password123`) 를 그대로 쓸 수 있다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
abstract class ApiTestSupport {
	@Autowired
	protected lateinit var mvc: MockMvc

	protected fun json(body: String) = body.trimIndent()

	/** 로그인해서 인증 cookie 를 얻는다. 이후 요청에 `.cookie(...)` 로 붙인다. */
	protected fun loginCookie(username: String, password: String = "password123"): Cookie {
		val response = mvc.perform(
			post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"username":"$username","password":"$password"}""")
		)
			.andExpect(status().isOk)
			.andReturn()
			.response

		return response.getCookie("token")
			?: error("로그인 응답에 token cookie 가 없습니다: ${response.getHeader("Set-Cookie")}")
	}
}
