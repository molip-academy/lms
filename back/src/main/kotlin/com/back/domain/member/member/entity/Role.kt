package com.back.domain.member.member.entity

/**
 * `Member` 의 시스템 권한 등급. 학습상의 신분을 표현하지 않는다.
 *
 * 한 사람이 어떤 강의에서는 수강생이고 다른 강의에서는 강사일 수 있으므로,
 * 학습상의 신분은 계정 속성이 아니라 강의와의 관계로 표현되어야 한다.
 * LMS 본체가 정해질 때 그 개념이 별도로 추가된다. 여기에 STUDENT/INSTRUCTOR 를 넣지 않는다.
 */
enum class Role {
	USER,
	ADMIN,
}
