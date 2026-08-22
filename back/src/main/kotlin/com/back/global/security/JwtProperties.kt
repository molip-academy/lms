package com.back.global.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
	val secret: String,
	val expirationSeconds: Long,
	val cookieName: String,
	/** http://localhost 에서는 Secure cookie 가 붙지 않으므로 dev/test 는 false 다. */
	val cookieSecure: Boolean,
)
