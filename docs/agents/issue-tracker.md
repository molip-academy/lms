# Issue tracker: Local Markdown

이 repo의 issue와 spec은 `.scratch/` 안의 Markdown file로 관리한다.

## Conventions

- feature 하나당 하나의 directory를 사용한다: `.scratch/<feature-slug>/`
- spec은 `.scratch/<feature-slug>/spec.md`에 둔다.
- Implementation issue는 `.scratch/<feature-slug>/issues/<NN>-<slug>.md`에 ticket당 file 하나로 작성하고 `01`부터 번호를 매긴다. 여러 ticket을 하나의 file로 합치지 않는다.
- Triage state는 각 issue file 상단 부근의 `Status:` line에 기록한다. role string은 `triage-labels.md`를 참고한다.
- comment와 conversation history는 file 하단의 `## Comments` heading 아래에 이어서 기록한다.

## When a skill says "publish to the issue tracker"

`.scratch/<feature-slug>/` 아래에 새 file을 생성한다. 필요한 경우 directory도 생성한다.

## When a skill says "fetch the relevant ticket"

참조된 path의 file을 읽는다. 일반적으로 사용자가 path나 issue number를 직접 전달한다.

## Wayfinding operations

`/wayfinder`에서 사용한다. **map**은 ticket마다 하나의 **child** file을 가지는 file이다.

- **Map**: `.scratch/<effort>/map.md`이며 Notes / Decisions-so-far / Fog body를 담는다.
- **Child ticket**: `.scratch/<effort>/issues/NN-<slug>.md`이며 `01`부터 번호를 매기고 질문을 body에 기록한다. `Type:` line에는 ticket type(`research`/`prototype`/`grilling`/`task`)을, `Status:` line에는 `claimed`/`resolved`를 기록한다.
- **Blocking**: 상단 부근의 `Blocked by: NN, NN` line에 기록한다. 나열된 모든 file이 `resolved`이면 ticket이 unblocked 상태가 된다.
- **Frontier**: `.scratch/<effort>/issues/`에서 open, unblocked, unclaimed 상태인 file을 찾는다. 번호가 가장 빠른 file을 우선한다.
- **Claim**: 작업을 시작하기 전에 `Status: claimed`로 설정하고 저장한다.
- **Resolve**: `## Answer` heading 아래에 답을 추가하고 `Status: resolved`로 설정한 다음, `map.md`의 Decisions-so-far에 context pointer(gist + link)를 추가한다.
