package com.back.global.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * 모든 entity 가 상속한다. 작성일과 수정일은 JPA auditing 이 채운다.
 *
 * equals/hashCode 는 id 기준이다. Hibernate lazy proxy 는 실제 class 가 다르므로
 * `javaClass` 비교 대신 id 만 본다.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long = 0
		protected set

	@CreatedDate
	@Column(updatable = false)
	var createDate: LocalDateTime = LocalDateTime.MIN
		protected set

	@LastModifiedDate
	var modifyDate: LocalDateTime = LocalDateTime.MIN
		protected set

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is BaseEntity) return false
		if (id == 0L) return false
		return id == other.id
	}

	override fun hashCode(): Int = if (id == 0L) super.hashCode() else id.hashCode()
}
