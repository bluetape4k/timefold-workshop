# README Diagram Quality

## Context

The workspace-wide README diagram pass regenerated Timefold workshop diagrams
with the current infographic style and the latest overlap and arrow-spacing
rules.

## Decision

Keep the generated SVG sources in the repository and render matching PNG files
for README consumption.

## Outcome

The README image asset pair now follows the shared pastel infographic style and
remains reusable for future diagram updates.

## Verification

- Ran the workspace README diagram quality audit and confirmed zero critical
  findings for text overlap, canvas clipping, sequence label clipping, missing
  README coverage, and zero-length arrows.

## Future Guidance

Preserve both SVG and PNG assets. Use the SVG as the editable source and update
the PNG whenever the source changes.
