import { ThumbsUp } from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

export function LikeButton({
  liked,
  count,
  onToggle,
  disabled,
  size = "sm",
}: {
  liked: boolean
  count: number
  onToggle: () => void
  disabled?: boolean
  size?: "sm" | "default"
}) {
  return (
    <Button
      type="button"
      variant={liked ? "default" : "outline"}
      size={size}
      onClick={onToggle}
      disabled={disabled}
      aria-pressed={liked}
      aria-label={liked ? "추천 취소" : "추천"}
    >
      <ThumbsUp className={cn("size-4", liked && "fill-current")} />
      {count}
    </Button>
  )
}
