package com.back.domain.member.member.controller

import com.back.domain.member.member.dto.MemberDto
import com.back.domain.member.member.dto.MemberSignupReqBody
import com.back.domain.member.member.service.MemberService
import com.back.global.rq.Rq
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
	private val memberService: MemberService,
	private val rq: Rq,
) {
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	fun signup(@RequestBody @Valid body: MemberSignupReqBody): MemberDto {
		val member = memberService.join(body.username, body.password, body.nickname)
		return MemberDto(member)
	}

	/**
	 * cookie 가 HttpOnly 라 front 는 token 을 읽을 수 없다.
	 * 로그인 여부 판정은 이 endpoint 의 200/401 로만 이뤄진다.
	 */
	@GetMapping("/me")
	@Transactional(readOnly = true)
	fun me(): MemberDto = MemberDto(rq.actorEntity())
}
