package com.back.domain.member.member.dto

import com.back.domain.member.member.entity.Member
import java.time.LocalDateTime

data class MemberDto(
	val id: Long,
	val createDate: LocalDateTime,
	val modifyDate: LocalDateTime,
	val username: String,
	val nickname: String,
	val role: String,
) {
	constructor(member: Member) : this(
		id = member.id,
		createDate = member.createDate,
		modifyDate = member.modifyDate,
		username = member.username,
		nickname = member.nickname,
		role = member.role.name,
	)
}

/** 글/댓글에 붙는 작성자 표기. `Member` 의 공개 식별자는 nickname 뿐이므로 username 을 노출하지 않는다. */
data class AuthorDto(
	val id: Long,
	val nickname: String,
) {
	constructor(member: Member) : this(member.id, member.nickname)
}
