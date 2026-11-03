# Achievements Expand Your World

A Minecraft Java survival-challenge mod for **Minecraft 26.2 / NeoForge 26.2**.

The playable area starts as a **1×1 square** centered on the world's initial Overworld spawn. Every unique completed advancement expands the vanilla World Border by one block on each side.

| Unique advancements | Border size |
|---:|---:|
| 0 | 1×1 |
| 1 | 3×3 |
| 2 | 5×5 |
| 3 | 7×7 |
| 4 | 9×9 |
| 5 | 11×11 |

## Gameplay

- One shared advancement counter for the whole world/server.
- Each advancement ID counts once, even if multiple players earn it.
- Recipe-book unlock advancements (`*/recipes/*`) are excluded.
- The vanilla World Border is centered on the initial Overworld spawn.
- All loaded dimensions receive the same border center and size.
- The border expands smoothly over 2 seconds.
- Players joining a server are shown the current world size.
- Every expansion is announced to all online players.
- Use `/aew status` to view the current border size and advancement count.

## Terrain and chunks

The mod deliberately keeps Minecraft's normal chunk generator. When expansion makes previously inaccessible locations reachable, Minecraft generates any missing chunks naturally using the world's selected seed, biome, structures, caves, and terrain settings. Chunks that were already generated are revealed without being rewritten. The mod does not force-load or pre-generate chunks, so it avoids unnecessary world-size growth and server load.

The vanilla World Border is the movement boundary; it does not delete terrain outside the border.

## Multiplayer and saving

- Advancement progress is stored once per world in server-level Saved Data.
- The initial border center and the set of counted advancement IDs persist across restarts.
- Vanilla World Border packets synchronize border changes to players.
- The same saved center and border size are applied to each server dimension.
- The server is authoritative; clients cannot independently award expansions.

## Build and tests

Requires **JDK 25**.

```bash
./gradlew clean build
```

The build runs the JUnit progression tests and creates the mod JAR in `build/libs/`.

GitHub Actions builds and tests every push to `main`, then uploads the JAR as a workflow artifact. Pushing a version tag such as `v1.0.0` also publishes a GitHub Release containing the built JAR.

## Mod metadata

- Mod ID: `achievementsexpandworld`
- Minecraft: `26.2`
- NeoForge: `26.2.0.90`
- Java: `25`
- License: MIT
