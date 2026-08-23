import { NetworkError } from "./api"

/**
 * 조회 실패를 사용자에게 보일 문구로 바꾼다.
 *
 * 연결이 끊긴 것과 서버가 답을 준 실패는 사용자가 할 수 있는 일이 다르다.
 * PWA 로 설치하면 offline 에서 앱을 여는 일이 실제로 생기는데, 그때
 * "글을 찾을 수 없습니다" 같은 문구가 뜨면 글이 지워진 것으로 오해하게 된다.
 */
export function loadErrorMessage(error: unknown, fallback: string): string {
  if (error instanceof NetworkError) {
    return "오프라인이라 불러올 수 없습니다. 연결되면 다시 보입니다."
  }
  return fallback
}
