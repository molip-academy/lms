import type { UseFormSetError, FieldValues, Path } from "react-hook-form"
import { ApiError, NetworkError } from "./api"

/**
 * back 의 ProblemDetail 을 form 에 되돌린다.
 * 필드별 메시지(`errors`)가 있으면 해당 입력칸에, 없으면 form 전체 에러로 붙인다.
 */
export function applyApiError<T extends FieldValues>(
  error: unknown,
  setError: UseFormSetError<T>,
): void {
  // 연결 자체가 안 된 경우는 입력이 틀린 게 아니므로 필드가 아니라 form 전체에 붙인다.
  if (error instanceof NetworkError) {
    setError("root", { message: error.message })
    return
  }

  if (!(error instanceof ApiError)) {
    setError("root", { message: "요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요." })
    return
  }

  if (error.errors) {
    for (const [field, message] of Object.entries(error.errors)) {
      setError(field as Path<T>, { message })
    }
    return
  }

  setError("root", { message: error.message })
}
