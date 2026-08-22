package com.back.global.initData

import com.back.domain.member.member.entity.Role
import com.back.domain.member.member.service.MemberService
import com.back.domain.post.comment.service.CommentService
import com.back.domain.post.post.entity.Post
import com.back.domain.post.post.service.PostService
import com.back.domain.post.postLike.repository.PostLikeRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.transaction.annotation.Transactional

/**
 * 운영에서도 재배포마다 DB 가 비므로 이 runner 가 매번 돈다 (ADR 0002). 의도된 동작이다.
 *
 * self-injection 은 `@Transactional` 이 proxy 를 거쳐 실제로 적용되게 하기 위한 것이다.
 * bean method 를 직접 호출하면 proxy 를 우회해 transaction 이 걸리지 않는다.
 */
@Configuration
class BaseInitData(
	private val memberService: MemberService,
	private val postService: PostService,
	private val commentService: CommentService,
	private val postLikeRepository: PostLikeRepository,
	@param:Lazy private val self: BaseInitData,
) {
	@Bean
	fun baseInitDataApplicationRunner(): ApplicationRunner = ApplicationRunner {
		self.work()
	}

	@Transactional
	fun work() {
		// 회원이 1명이라도 있으면 이미 초기화된 것으로 보고 중단한다.
		if (memberService.count() > 0) return

		val admin = memberService.join("admin", "password123", "관리자", Role.ADMIN)
		val user1 = memberService.join("user1", "password123", "유저1")
		val user2 = memberService.join("user2", "password123", "유저2")
		val user3 = memberService.join("user3", "password123", "유저3")
		val user4 = memberService.join("user4", "password123", "유저4")

		val post1 = postService.write(user1, "MOLIP Academy 자유게시판이 열렸습니다", "무엇이든 편하게 이야기해주세요.")
		val post2 = postService.write(user1, "스터디 같이 하실 분 구합니다", "매주 화요일 저녁에 모입니다. 관심 있으신 분 댓글 남겨주세요.")
		val post3 = postService.write(user2, "개발 환경 세팅하다가 막혔어요", "JDK 버전 때문에 빌드가 안 됩니다. 혹시 같은 문제 겪으신 분 계신가요?")
		val post4 = postService.write(user3, "오늘 배운 내용 정리해봤습니다", "정리하면서 이해가 훨씬 잘 됐습니다. 필요하신 분 참고하세요.")
		val post5 = postService.write(user4, "추천하는 참고 자료 공유합니다", "공식 문서가 제일 정확하더라고요.")

		// 댓글은 한 글에 3개, 다른 글에 2개로 몰아둔다.
		// 전부 다른 글에 하나씩 붙이면 댓글 목록과 정렬을 화면에서 확인할 수 없다.
		commentService.write(post1, user2, "드디어 열렸네요! 반갑습니다.")
		commentService.write(post1, user3, "잘 부탁드립니다.")
		commentService.write(post1, admin, "자유롭게 사용해주세요. 문의는 언제든 환영입니다.")
		commentService.write(post3, user4, "JDK 25 로 맞추니까 저는 해결됐습니다.")
		commentService.write(post3, user1, "저도 같은 문제였어요. 위 방법 추천합니다.")

		// 글마다 추천 수를 다르게 두어 정렬과 표시를 화면에서 확인할 수 있게 한다.
		like(post1, listOf(user2, user3, user4))
		like(post2, listOf(user3, user4))
		like(post3, listOf(user1))
		like(post4, emptyList())
		like(post5, listOf(admin, user1, user2))
	}

	private fun like(post: Post, members: List<com.back.domain.member.member.entity.Member>) {
		members.forEach { postLikeRepository.save(post.addLike(it)) }
	}
}
