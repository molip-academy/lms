# Domain Docs

engineering skill이 codebase를 탐색할 때 이 repo의 domain documentation을 사용하는 방법을 정의한다.

## Before exploring, read these

- repo root의 **`CONTEXT.md`**를 읽는다.
- **`docs/adr/`**에서 작업 영역과 관련된 ADR을 읽는다.

이 file들이 없으면 **proceed silently** 한다. 부재를 알리거나 먼저 생성하자고 제안하지 않는다. term이나 decision이 실제로 해결될 때 `/domain-modeling` skill이 lazy하게 생성한다.

## File structure

Single-context repo:

```
/
├── CONTEXT.md                ← back과 front가 공유하는 domain glossary
├── docs/adr/                 ← system-wide decisions
├── back/
├── front-react/
├── front-kmp/
└── infra/
```

`docs/adr/`는 기록할 decision이 생길 때 생성한다.

## Use the glossary's vocabulary

output에서 domain concept을 명명할 때는 root `CONTEXT.md`에 정의된 term을 사용한다. glossary가 명시적으로 피하는 synonym으로 바꾸지 않는다.

필요한 concept이 아직 glossary에 없으면 terminology를 재검토하거나 `/domain-modeling`을 위해 gap을 기록한다.

## Flag ADR conflicts

output이 기존 ADR과 충돌하면 silently overriding하지 말고 충돌을 명시적으로 드러낸다.
