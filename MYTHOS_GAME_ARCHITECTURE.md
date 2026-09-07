# MythOS: Hack The World

## Pipeline

Complete TPS corpus -> parser -> content manifest -> knowledge graph -> campaign mapping -> XP/progression -> Android runtime.

## Source of truth

The repository's `src/` and `api/` trees are the canonical corpus. The game discovers source files automatically instead of relying on a hand-maintained chapter list.

## Content model

Every discovered source file becomes a content entry with a stable source-derived ID, title, content type, word count, XP value, headings, detected concepts, and source path.

The generated knowledge graph contains source nodes, concept nodes, and relationships from shared concepts and explicit Markdown links.

## Progression

1. Boot
2. System Diagnostic
3. Firewall
4. Overclock
5. Defrag
6. Reboot
7. Codex

Completion is stored locally and XP is awarded once per source entry.

## Build

From repository root:

`./android/build-apk.sh`

The script regenerates the corpus, copies the complete text corpus into Android assets, generates the graph manifest, then builds the debug APK.

## Design principle

The player should experience the repository as a world to traverse, not as a directory to scrape. Source provenance remains visible inside each entry, while the campaign provides narrative order and progression.
