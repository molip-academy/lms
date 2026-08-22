package com.back.domain.member.member.service

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.entity.Role
import com.back.domain.member.member.repository.MemberRepository
import com.back.global.exception.ServiceException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class MemberService(
	private val memberRepository: MemberRepository,
	private val passwordEncoder: PasswordEncoder,
) {
	fun join(username: String, password: String, nickname: String, role: Role = Role.USER): Member {
		if (memberRepository.existsByUsername(username)) {
			throw ServiceException.conflict("이미 사용 중인 아이디입니다.")
		}
		if (memberRepository.existsByNickname(nickname)) {
			throw ServiceException.conflict("이미 사용 중인 닉네임입니다.")
		}

		return memberRepository.save(
			Member(
				username = username,
				password = passwordEncoder.encode(password)!!,
				nickname = nickname,
				role = role,
			)
		)
	}

	/** 아이디가 없는 경우와 비밀번호가 틀린 경우를 구분하지 않는다. 계정 존재 여부를 알려주지 않기 위해서다. */
	fun authenticate(username: String, password: String): Member {
		val member = memberRepository.findByUsername(username)
			?: throw ServiceException.unauthorized("아이디 또는 비밀번호가 올바르지 않습니다.")

		if (!passwordEncoder.matches(password, member.password)) {
			throw ServiceException.unauthorized("아이디 또는 비밀번호가 올바르지 않습니다.")
		}

		return member
	}

	fun count(): Long = memberRepository.count()

	fun findById(id: Long): Member = memberRepository.findById(id)
		.orElseThrow { ServiceException.notFound("존재하지 않는 회원입니다.") }
}
