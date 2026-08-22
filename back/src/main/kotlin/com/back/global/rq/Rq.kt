package com.back.global.rq

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.repository.MemberRepository
import com.back.global.exception.ServiceException
import com.back.global.security.JwtProperties
import com.back.global.security.JwtProvider
import com.back.global.security.SecurityUser
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * 현재 요청의 로그인 주체와 인증 cookie 를 다룬다.
 *
 * SecurityContextHolder 는 ThreadLocal 이므로 singleton 으로 두어도 안전하다.
 */
@Component
class Rq(
	private val memberRepository: MemberRepository,
	private val jwtProvider: JwtProvider,
	private val jwtProperties: JwtProperties,
	private val response: HttpServletResponse,
) {
	val actorOrNull: SecurityUser?
		get() = SecurityContextHolder.getContext().authentication?.principal as? SecurityUser

	val actor: SecurityUser
		get() = actorOrNull ?: throw ServiceException.unauthorized("로그인이 필요합니다.")

	/** 연관관계 설정 전용 proxy. DB 를 조회하지 않는다. */
	fun actorRef(): Member = memberRepository.getReferenceById(actor.id)

	/** 실제 값이 필요할 때만 조회한다. */
	fun actorEntity(): Member = memberRepository.findById(actor.id)
		.orElseThrow { ServiceException.unauthorized("존재하지 않는 회원입니다.") }

	fun setLoginCookie(member: Member) {
		val cookie = ResponseCookie.from(jwtProperties.cookieName, jwtProvider.generate(member))
			.httpOnly(true)
			.secure(jwtProperties.cookieSecure)
			.sameSite("Lax")
			.path("/")
			.maxAge(jwtProperties.expirationSeconds)
			.build()
		response.addHeader("Set-Cookie", cookie.toString())
	}

	/** stateless 이므로 서버 측 무효화는 없다. cookie 를 지우는 것이 로그아웃이다. */
	fun clearLoginCookie() {
		val cookie = ResponseCookie.from(jwtProperties.cookieName, "")
			.httpOnly(true)
			.secure(jwtProperties.cookieSecure)
			.sameSite("Lax")
			.path("/")
			.maxAge(0)
			.build()
		response.addHeader("Set-Cookie", cookie.toString())
	}
}
