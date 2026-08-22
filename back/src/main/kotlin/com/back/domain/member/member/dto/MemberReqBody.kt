package com.back.domain.member.member.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/** 제약은 front 의 zod schema 와 1:1 로 맞춘다. 한쪽만 바꾸지 않는다. */
data class MemberSignupReqBody(
	@field:NotBlank(message = "아이디를 입력해주세요.")
	@field:Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
	@field:Pattern(regexp = "^[a-z0-9]*$", message = "아이디는 영문 소문자와 숫자만 사용할 수 있습니다.")
	val username: String,

	@field:NotBlank(message = "비밀번호를 입력해주세요.")
	@field:Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
	val password: String,

	@field:NotBlank(message = "닉네임을 입력해주세요.")
	@field:Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다.")
	val nickname: String,
)

data class MemberLoginReqBody(
	@field:NotBlank(message = "아이디를 입력해주세요.")
	val username: String,

	@field:NotBlank(message = "비밀번호를 입력해주세요.")
	val password: String,
)
