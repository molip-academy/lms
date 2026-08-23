import { useEffect } from "react"
import { useRegisterSW } from "virtual:pwa-register/react"
import { toast } from "sonner"

/**
 * service worker 를 등록하고, 새 버전이 준비되면 toast 로 알린다.
 *
 * 자동 reload 하지 않는 이유는 글/댓글을 쓰는 도중에 화면이 갈아엎히면
 * 입력하던 내용이 그대로 날아가기 때문이다. 새로고침 시점은 사용자가 고른다.
 */
export function PwaUpdatePrompt() {
  const {
    needRefresh: [needRefresh, setNeedRefresh],
    updateServiceWorker,
  } = useRegisterSW()

  useEffect(() => {
    if (!needRefresh) return

    toast("새 버전이 있습니다", {
      description: "새로고침하면 최신 버전으로 바뀝니다.",
      duration: Infinity,
      action: {
        label: "새로고침",
        // true 를 넘기면 새 worker 가 활성화된 뒤 페이지를 다시 불러온다.
        onClick: () => void updateServiceWorker(true),
      },
      onDismiss: () => setNeedRefresh(false),
    })
  }, [needRefresh, setNeedRefresh, updateServiceWorker])

  return null
}
