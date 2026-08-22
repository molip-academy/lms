# Triage Labels

skill은 다섯 가지 canonical triage role을 사용한다. 이 file은 각 role을 이 repo의 issue tracker에서 실제로 사용하는 label string에 mapping한다.

| Label in mattpocock/skills | Label in our tracker | Meaning                                   |
| -------------------------- | -------------------- | ----------------------------------------- |
| `needs-triage`             | `needs-triage`       | Maintainer가 이 issue를 평가해야 함       |
| `needs-info`               | `needs-info`         | reporter의 추가 정보를 기다리는 중       |
| `ready-for-agent`          | `ready-for-agent`    | spec이 완료되어 AFK agent가 작업할 수 있음 |
| `ready-for-human`          | `ready-for-human`    | human implementation이 필요함            |
| `wontfix`                  | `wontfix`            | 작업하지 않기로 결정함                   |

skill이 role을 언급하면(예: "apply the AFK-ready triage label") 이 표에서 대응하는 label string을 사용한다.

실제로 사용하는 vocabulary에 맞게 오른쪽 column을 수정한다.
