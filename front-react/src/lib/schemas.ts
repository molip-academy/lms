import { z } from "zod"

/** back 의 @Valid 제약과 1:1 로 맞춘다. 한쪽만 바꾸면 두 곳의 메시지가 어긋난다. */

export const signupSchema = z.object({
  username: z
    .string()
    .min(4, "아이디는 4~20자여야 합니다.")
    .max(20, "아이디는 4~20자여야 합니다.")
    .regex(/^[a-z0-9]*$/, "아이디는 영문 소문자와 숫자만 사용할 수 있습니다."),
  password: z.string().min(8, "비밀번호는 8자 이상이어야 합니다."),
  nickname: z
    .string()
    .min(2, "닉네임은 2~20자여야 합니다.")
    .max(20, "닉네임은 2~20자여야 합니다."),
})

export const loginSchema = z.object({
  username: z.string().min(1, "아이디를 입력해주세요."),
  password: z.string().min(1, "비밀번호를 입력해주세요."),
})

export const postSchema = z.object({
  title: z.string().min(1, "제목을 입력해주세요.").max(200, "제목은 1~200자여야 합니다."),
  content: z.string().min(1, "내용을 입력해주세요."),
})

export const commentSchema = z.object({
  content: z.string().min(1, "댓글 내용을 입력해주세요.").max(1000, "댓글은 1~1000자여야 합니다."),
})

export type SignupInput = z.infer<typeof signupSchema>
export type LoginInput = z.infer<typeof loginSchema>
export type PostInput = z.infer<typeof postSchema>
export type CommentInput = z.infer<typeof commentSchema>
