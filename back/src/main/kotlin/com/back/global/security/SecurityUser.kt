package com.back.global.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

/** SecurityContext 에 담기는 principal. JWT payload 에서 만들어지며 DB 조회를 하지 않는다. */
class SecurityUser(
	val id: Long,
	username: String,
	val nickname: String,
	val role: String,
) : User(username, "", listOf<GrantedAuthority>(SimpleGrantedAuthority("ROLE_$role"))) {
	val isAdmin: Boolean
		get() = role == "ADMIN"
}
