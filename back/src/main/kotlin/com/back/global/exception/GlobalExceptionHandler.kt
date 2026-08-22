package com.back.global.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 모든 에러 응답은 RFC 9457 ProblemDetail 이다.
 * 성공 응답은 순수 DTO 이므로, front 는 HTTP status 로 성공/실패를 판별한다.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

	@ExceptionHandler(ServiceException::class)
	fun handleServiceException(e: ServiceException): ProblemDetail =
		ProblemDetail.forStatusAndDetail(e.status, e.message)

	/** @Valid 실패를 필드별 메시지로 변환한다. front 의 zod schema 와 1:1 로 맞물린다. */
	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleValidation(e: MethodArgumentNotValidException): ProblemDetail {
		val problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다.")
		val errors = e.bindingResult.fieldErrors.associate { field ->
			field.field to (field.defaultMessage ?: "올바르지 않은 값입니다.")
		}
		problem.setProperty("errors", errors)
		return problem
	}

	@ExceptionHandler(NoSuchElementException::class)
	fun handleNoSuchElement(e: NoSuchElementException): ProblemDetail =
		ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message ?: "대상을 찾을 수 없습니다.")
}
