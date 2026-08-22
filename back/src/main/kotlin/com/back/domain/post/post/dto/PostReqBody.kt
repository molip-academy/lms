package com.back.domain.post.post.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PostWriteReqBody(
	@field:NotBlank(message = "제목을 입력해주세요.")
	@field:Size(min = 1, max = 200, message = "제목은 1~200자여야 합니다.")
	val title: String,

	@field:NotBlank(message = "내용을 입력해주세요.")
	val content: String,
)
