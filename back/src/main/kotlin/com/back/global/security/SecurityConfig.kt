package com.back.global.security

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfig(
	private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {
	@Bean
	fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		http
			.authorizeHttpRequests { auth ->
				auth
					.requestMatchers(HttpMethod.POST, "/api/v1/members").permitAll()
					.requestMatchers("/api/v1/auth/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/v1/posts").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
					.requestMatchers("/h2-console/**").permitAll()
					.requestMatchers("/api/v1/**").authenticated()
					.anyRequest().permitAll()
			}
			// token cookie + SameSite=Lax + JSON API 이므로 CSRF token 을 쓰지 않는다.
			.csrf { it.disable() }
			.formLogin { it.disable() }
			.httpBasic { it.disable() }
			.logout { it.disable() }
			.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
			// h2-console 은 frame 을 쓴다. dev profile 에서만 열린다.
			.headers { it.frameOptions { frame -> frame.sameOrigin() } }
			.exceptionHandling { ex ->
				// 필터 단계의 인증/인가 실패도 controller 와 같은 ProblemDetail 로 응답한다.
				ex.authenticationEntryPoint { _, response, _ ->
					writeProblem(response, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.")
				}
				ex.accessDeniedHandler { _, response, _ ->
					writeProblem(response, HttpStatus.FORBIDDEN, "권한이 없습니다.")
				}
			}
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

		return http.build()
	}

	private fun writeProblem(response: jakarta.servlet.http.HttpServletResponse, status: HttpStatus, detail: String) {
		val problem = ProblemDetail.forStatusAndDetail(status, detail)
		response.status = status.value()
		response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
		response.characterEncoding = "UTF-8"
		response.writer.write(
			"""{"type":"about:blank","title":"${status.reasonPhrase}","status":${status.value()},"detail":"${problem.detail}"}"""
		)
	}
}
