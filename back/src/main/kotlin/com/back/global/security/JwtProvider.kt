package com.back.global.security

import com.back.domain.member.member.entity.Member
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtProvider(
	private val properties: JwtProperties,
) {
	private val key: SecretKey = Keys.hmacShaKeyFor(properties.secret.toByteArray())

	/**
	 * 매 요청마다 DB 를 조회하지 않도록 표시에 필요한 값을 token 에 담는다.
	 * 그래서 nickname 변경은 다음 로그인부터 반영된다.
	 */
	fun generate(member: Member): String {
		val now = Date()
		return Jwts.builder()
			.subject(member.id.toString())
			.claim("username", member.username)
			.claim("nickname", member.nickname)
			.claim("role", member.role.name)
			.issuedAt(now)
			.expiration(Date(now.time + properties.expirationSeconds * 1000))
			.signWith(key)
			.compact()
	}

	/** 서명/만료가 유효하지 않으면 null 을 반환한다. 인증 실패는 예외가 아니라 비로그인으로 다룬다. */
	fun parse(token: String): JwtPayload? = runCatching {
		val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
		JwtPayload(
			id = claims.subject.toLong(),
			username = claims["username"] as String,
			nickname = claims["nickname"] as String,
			role = claims["role"] as String,
		)
	}.getOrNull()
}

data class JwtPayload(
	val id: Long,
	val username: String,
	val nickname: String,
	val role: String,
)
