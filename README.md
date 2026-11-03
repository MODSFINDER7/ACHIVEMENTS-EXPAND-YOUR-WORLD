# Achievements Expand Your World

A Minecraft Java survival-challenge mod for **Minecraft 26.2 / NeoForge 26.2**.

The playable area starts as a **1×1 square** centered on the world's initial Overworld spawn. Advancements award points according to their type, and each point adds one block to every side of the playable square.

| Advancement type | Points |
|---|---:|
| Normal task | 1 |
| Goal | 10 |
| Challenge | 50 |
| Hidden (takes priority over frame) | 100 |

For example, 10 points produce a 21×21 border; 50 points produce a 101×101 border.

## Gameplay

- One shared advancement progression for the whole world/server.
- Each advancement ID awards points once, even if multiple players earn it.
- Hidden advancements award 100 points regardless of their frame.
- Recipe-book unlock advancements (`*/recipes/*`) are excluded.
- The vanilla World Border is centered on the initial Overworld spawn.
- All loaded dimensions receive the same border center and size.
- The border expands smoothly over 2 seconds.
- Players joining a server are shown the current world size.
- Every expansion is announced to all online players.
- Use `/aew status` to view the current border size, point total, and unique advancement count.

## Terrain and chunks

The mod keeps Minecraft's normal chunk generator. When expansion makes previously inaccessible locations reachable, Minecraft generates missing chunks naturally using the world's selected seed, biome, structures, caves, and terrain settings. Previously generated chunks are preserved. The mod does not force-load or pre-generate chunks.

The vanilla World Border is the movement boundary; it does not itself hide terrain outside the border. The outside-border void visual is not implemented yet.

## Multiplayer and saving

- Advancement IDs, weighted points, and the initial border center persist in server-level Saved Data.
- Existing saves from the previous one-point-per-advancement version are migrated with their current advancement count as their starting point total.
- Vanilla World Border packets synchronize border changes to players.
- The server is authoritative; clients cannot independently award expansions.

## Build and tests

Requires **JDK 25**.

```bash
./gradlew clean build
```

GitHub Actions builds and tests every push to `main`, then uploads the JAR as a workflow artifact. Pushing a version tag such as `v1.0.0` also publishes a GitHub Release containing the built JAR.

## Mod metadata

- Mod ID: `achievementsexpandworld`
- Minecraft: `26.2`
- NeoForge: `26.2.0.90`
- Java: `25`
- License: MIT
