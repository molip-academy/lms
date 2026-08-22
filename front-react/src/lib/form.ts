import type { UseFormSetError, FieldValues, Path } from "react-hook-form"
import { ApiError } from "./api"

/**
 * back 의 ProblemDetail 을 form 에 되돌린다.
 * 필드별 메시지(`errors`)가 있으면 해당 입력칸에, 없으면 form 전체 에러로 붙인다.
 */
export function applyApiError<T extends FieldValues>(
  error: unknown,
  setError: UseFormSetError<T>,
): void {
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
