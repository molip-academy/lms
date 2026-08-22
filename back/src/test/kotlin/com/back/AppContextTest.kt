package com.back

import com.back.domain.member.member.repository.MemberRepository
import com.back.domain.post.comment.repository.CommentRepository
import com.back.domain.post.post.repository.PostRepository
import com.back.global.ApiTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AppContextTest(
	@Autowired val memberRepository: MemberRepository,
	@Autowired val postRepository: PostRepository,
	@Autowired val commentRepository: CommentRepository,
) : ApiTestSupport() {

	@Test
	@DisplayName("BaseInitData 가 sample 회원 5명 / 글 5개 / 댓글 5개를 만든다")
	fun initData() {
		assertThat(memberRepository.count()).isEqualTo(5)
		assertThat(postRepository.count()).isEqualTo(5)
		assertThat(commentRepository.count()).isEqualTo(5)

		val admin = memberRepository.findByUsername("admin")
		assertThat(admin).isNotNull
		assertThat(admin!!.isAdmin).isTrue()
		assertThat(admin.nickname).isEqualTo("관리자")
	}
}
