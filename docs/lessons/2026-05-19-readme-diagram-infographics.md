# README 다이어그램 인포그래픽

## 배경

README는 아키텍처, 클래스, 시퀀스, ERD 및 기타 다이어그램에 Mermaid 코드 블록을
사용했다. 워크스페이스 전체의 시각적 방향을 검토된 파스텔 인포그래픽 PNG로
변경하고, 재사용을 위해 SVG 원본 asset을 함께 보관하기로 했다.

## 결정

README의 Mermaid 블록을 생성한 PNG 이미지 링크로 교체하고, 대응하는 SVG 원본을
PNG 옆에 저장한다. 다이어그램 텍스트는 영어로만 표시하고, 큰 라벨에는
Architects Daughter, 세부 텍스트에는 Comic Mono를 사용한다. 아키텍처, 클래스,
시퀀스, ERD에는 각각 맞는 레이아웃을 적용한다.

## 결과

README 다이어그램을 `bluetape4k.github.io/docs/readme-diagram-samples`의
공유 2026-05-19 스타일 가이드로 렌더링했다. 루트 README asset은 저장소별
배치 규칙이 있으면 해당 규칙을 따른다.

## 검증

`rsvg-convert`로 PNG/SVG asset을 생성하고, 저장소 간 변환 과정에서 README
링크를 확인했다.

## 후속 지침

README 다이어그램은 PNG를 삽입하고 편집용 SVG 원본을 함께 유지한다. 시각적
일관성이 중요한 경우 raw Mermaid나 단순한 Mermaid 테마 색상 변경으로 되돌리지 않는다.
