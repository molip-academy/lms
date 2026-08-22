package com.back.global.exception

import org.springframework.http.HttpStatus

/** 도메인 규칙 위반을 HTTP status 와 함께 표현한다. GlobalExceptionHandler 가 ProblemDetail 로 변환한다. */
class ServiceException(
	val status: HttpStatus,
	override val message: String,
) : RuntimeException(message) {
	companion object {
		fun notFound(message: String) = ServiceException(HttpStatus.NOT_FOUND, message)
		fun conflict(message: String) = ServiceException(HttpStatus.CONFLICT, message)
		fun forbidden(message: String) = ServiceException(HttpStatus.FORBIDDEN, message)
		fun unauthorized(message: String) = ServiceException(HttpStatus.UNAUTHORIZED, message)
		fun badRequest(message: String) = ServiceException(HttpStatus.BAD_REQUEST, message)
	}
}
