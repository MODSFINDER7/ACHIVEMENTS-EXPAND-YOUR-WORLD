# Achievements Expand Your World

A Minecraft Java mod for **NeoForge 26.2**.

The playable area starts as a 1×1 square centered on the Overworld spawn. Every unique completed advancement expands the vanilla World Border by one block on each side:

| Unique advancements | Border size |
|---:|---:|
| 0 | 1×1 |
| 1 | 3×3 |
| 2 | 5×5 |
| 3 | 7×7 |
| 4 | 9×9 |

## Current implementation

- NeoForge 26.2 / Java 25 project setup.
- Server-side advancement completion detection.
- One global progression counter per world: an advancement ID only counts once, even in multiplayer.
- Recipe-unlock advancements (`*/recipes/*`) are excluded so recipe book unlocks do not trivialize the challenge.
- Vanilla World Border is centered on the initial Overworld spawn and applied to every dimension.
- Border progress and the fixed center are saved in server Saved Data.
- Border expands smoothly over 2 seconds.
- A world-wide chat notification announces each expansion.

## Build

Requires **JDK 25**.

```bash
./gradlew build
```

The mod JAR is created in `build/libs/`. GitHub Actions also builds the project on every push to `main` and uploads the JAR as an artifact.

## Mod metadata

- Mod ID: `achievementsexpandworld`
- Minecraft: `26.2`
- NeoForge: `26.2.0.93`
- Java: `25`

Terrain generation inside newly unlocked space is handled in the next development phase.
