package com.back.domain.member.auth.controller

import com.back.domain.member.member.dto.MemberDto
import com.back.domain.member.member.dto.MemberLoginReqBody
import com.back.domain.member.member.service.MemberService
import com.back.global.rq.Rq
import jakarta.validation.Valid
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
	private val memberService: MemberService,
	private val rq: Rq,
) {
	@PostMapping("/login")
	@Transactional(readOnly = true)
	fun login(@RequestBody @Valid body: MemberLoginReqBody): MemberDto {
		val member = memberService.authenticate(body.username, body.password)
		rq.setLoginCookie(member)
		return MemberDto(member)
	}

	/** stateless JWT 이므로 token 자체는 만료까지 유효하다. cookie 삭제가 곧 로그아웃이다. */
	@PostMapping("/logout")
	fun logout() {
		rq.clearLoginCookie()
	}
}
