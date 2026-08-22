package com.back.global.dto

/** 추천/취소 후의 상태. front 는 이 값으로 낙관적 업데이트를 확정하거나 되돌린다. */
data class LikeResultDto(
	val likeCount: Int,
	val liked: Boolean,
)
