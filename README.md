# Champion Of Sudoku

A feature-rich Android Sudoku game with real-time multiplayer, intelligent hints, and a progression system — published on Google Play.

<!-- If you have screenshots, add them here:
![Screenshot](docs/screenshot1.png)
-->

## Features

### Gameplay
- **60,000+ puzzles** across six difficulty levels — Beginner, Easy, Medium, Hard, Expert, and Master
- **Smart hint system** powered by 9 real Sudoku-solving strategies including Naked Pairs, Hidden Singles, X-Wing, Y-Wing, and Singles Chains
- **Art Mode** — an alternative visual mode replacing numbers with colors
- **Undo/redo** and pencil mark support
- **Save & resume** with autosave

### Multiplayer
- **Real-time multiplayer** via Google Play Games Services (2-4 players)
- **Three game modes** — Race (first to finish), Battle (competitive scoring), and Co-op
- **Bot opponents** with 10 skill levels, each with tunable accuracy and consistency for realistic play

### Progression
- **XP and leveling** with 52 rank tiers
- **14 achievements** — speed challenges, accuracy milestones, and multiplayer feats
- **9 leaderboards** — fastest times per difficulty, multiplayer rank, highest score, and total XP

## Technical Highlights

- **Dual-solver architecture** — a backtracking solver (most-constrained-variable heuristic) for solution validation, and a strategy-based solver for difficulty classification and hints
- **Strategy-based difficulty scoring** — puzzles are classified by which human-solving strategies are required, not just by clue count
- **Single-activity, multi-fragment architecture** with responsive layouts across 4 screen-size breakpoints
- **Cloud backends** — AWS DynamoDB + Cognito for persistent game history; Firebase for real-time features; Google Play Games for matchmaking and leaderboards

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java |
| Platform | Android (API 19-25) |
| Multiplayer | Google Play Games Services |
| Database | SQLite (bundled), AWS DynamoDB |
| Auth | AWS Cognito, Firebase Auth |
| Social | Facebook SDK |
| Image Loading | Glide, Picasso |

## Building

```bash
git clone https://github.com/Licktopia/ChampionsOfSudoku2.git
cd ChampionsOfSudoku2
./gradlew assembleDebug
```

> **Note:** Multiplayer and cloud features require Google Play Games, AWS, and Firebase credentials not included in this repository.
