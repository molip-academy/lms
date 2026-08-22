package com.back.domain.member.member.entity

import com.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

/**
 * 로그인할 수 있는 사람. LMS 에서의 신분(수강생/강사)이 아니라 인증 주체를 뜻한다.
 * CONTEXT.md 의 `Member` 정의를 따른다.
 */
@Entity
class Member(
	@Column(unique = true, nullable = false, length = 20)
	var username: String,

	@Column(nullable = false)
	var password: String,

	@Column(unique = true, nullable = false, length = 20)
	var nickname: String,

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	var role: Role = Role.USER,
) : BaseEntity() {
	val isAdmin: Boolean
		get() = role == Role.ADMIN

	fun modifyNickname(nickname: String) {
		this.nickname = nickname
	}
}
