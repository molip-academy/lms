package com.back.global.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * cookie 의 JWT 를 읽어 SecurityContext 를 채운다.
 *
 * token 이 없거나 유효하지 않으면 **비로그인으로 통과시킨다**. 차단은 SecurityConfig 의
 * 경로 규칙이 담당하므로, 여기서 401 을 던지면 공개 endpoint 까지 막히게 된다.
 */
@Component
class JwtAuthenticationFilter(
	private val jwtProvider: JwtProvider,
	private val jwtProperties: JwtProperties,
) : OncePerRequestFilter() {

	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain,
	) {
		val token = request.cookies?.firstOrNull { it.name == jwtProperties.cookieName }?.value

		if (!token.isNullOrBlank() && SecurityContextHolder.getContext().authentication == null) {
			jwtProvider.parse(token)?.let { payload ->
				val user = SecurityUser(payload.id, payload.username, payload.nickname, payload.role)
				SecurityContextHolder.getContext().authentication =
					UsernamePasswordAuthenticationToken(user, null, user.authorities)
			}
		}

		filterChain.doFilter(request, response)
	}
}
