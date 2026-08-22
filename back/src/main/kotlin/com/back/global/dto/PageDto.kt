package com.back.global.dto

import org.springframework.data.domain.Page

/**
 * Spring 의 `Page` 를 그대로 직렬화하면 구조가 불안정하다는 경고가 뜨므로 얇은 DTO 로 감싼다.
 */
data class PageDto<T>(
	val content: List<T>,
	val page: Int,
	val size: Int,
	val totalElements: Long,
	val totalPages: Int,
) {
	companion object {
		fun <E : Any, T : Any> of(page: Page<E>, mapper: (E) -> T): PageDto<T> = PageDto(
			content = page.content.map(mapper),
			page = page.number + 1,
			size = page.size,
			totalElements = page.totalElements,
			totalPages = page.totalPages,
		)
	}
}
