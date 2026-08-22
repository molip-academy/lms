## Development workflow

- 새 기능 → `/grill-with-docs`.
  - 하나의 작업으로 끝낼 수 있으면 → 현재 context에서 `/implement`.
  - 구현 순서는 알지만 여러 ticket으로 나눠야 하면 → 같은 context에서 `/to-spec` → `/to-tickets`.
- 말로만 결정할 수 없고 직접 실행해 보거나 UI를 확인해야 하는 설계 질문 → `/handoff` → `/prototype` → `/handoff`; 원래 기능 flow 재개.
- 다른 사람이 만든, 아직 정리되지 않은 issue → `/triage` → `/implement`.
- `/to-tickets`가 만든 ticket → `/triage` 없이 `/implement`.
- 재현이 어렵거나 원인을 바로 알 수 없는 bug → `/diagnosing-bugs`.
- 목표는 분명하지만 무엇부터 결정하고 어떤 순서로 진행할지 보이지 않으면 → `/wayfinder` → `/to-spec`.
- 특정 기능이 아니라 code structure의 개선점을 찾는 작업 → `/improve-codebase-architecture` → `/grill-with-docs`.
- `/grill-with-docs` → `/to-spec` → `/to-tickets`는 같은 context에서 이어서 실행한다. `/to-tickets` 완료 후와 각 `/implement` 사이에는 `/clear`한다.

## Agent skills

### Issue tracker

Issue는 `.scratch/` 아래의 local Markdown file로 관리한다. `docs/agents/issue-tracker.md`를 참고한다.

### Triage labels

이 repo는 기본 canonical triage label vocabulary를 사용한다. `docs/agents/triage-labels.md`를 참고한다.

### Domain docs

이 repo는 single-context domain documentation layout을 사용한다. `docs/agents/domain.md`를 참고한다.
