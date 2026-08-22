import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { useMe } from "@/hooks/use-auth"
import { formatDate } from "@/lib/format"

export function MePage() {
  const { member } = useMe()

  // ProtectedRoute 를 통과했으므로 member 는 존재한다.
  if (!member) return null

  const rows = [
    { label: "닉네임", value: member.nickname },
    { label: "아이디", value: member.username },
    { label: "가입일", value: formatDate(member.createDate) },
  ]

  return (
    <Card className="mx-auto max-w-md">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          내 정보
          {member.role === "ADMIN" && <Badge variant="secondary">관리자</Badge>}
        </CardTitle>
        <CardDescription>게시판에는 닉네임만 표시됩니다.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-3">
        {rows.map((row, index) => (
          <div key={row.label}>
            {index > 0 && <Separator className="mb-3" />}
            <div className="flex items-center justify-between text-sm">
              <span className="text-muted-foreground">{row.label}</span>
              <span className="font-medium">{row.value}</span>
            </div>
          </div>
        ))}
      </CardContent>
    </Card>
  )
}
