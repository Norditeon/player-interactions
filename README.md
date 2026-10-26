# Player Interactions

A **Minecraft Java 1.21.1 + NeoForge 21.1** mod that adds interactions between players (affectionate,
friendly, and also slaps and pranks), item gifting and an emote wheel. Both wheels can be customized
from an in-game repertoire.

## Features

### Interactions between two players (right-click with an empty hand)

A wheel opens with 8 slots of your choice (press `E` while it is open to edit them).

| Type | Interactions |
|---|---|
| Gestures (ask for consent) | hug, high-five, handshake, kiss on the cheek, kiss on the lips, head pat, fist bump, hold hands, bro greeting, kiss the hand, tickle, comfort, dance together |
| Slaps and pranks (no consent) | slap (½ ♥), punch (1 ♥), shove, kick in the butt (1 ♥), spank |

- **Ask** (default): the other player sees a prompt and answers with `Y` / `N`. It expires after 15 s.
- **Force** (center of the wheel or `Tab`): the gesture happens without asking and the other player
  **reacts differently**: force a kiss and they pull back and wipe their mouth, force a hug and they go stiff and struggle, and so on.
- **Slaps and pranks** never ask. Damage counts as your own attack, so the server's PvP setting applies;
  they are marked in red on the wheel. The kick and the spank happen **from behind**: the other player ends up facing away.

When an interaction starts, both players glide (~0.3 s) to the right distance. Each gesture is animated in phases
with particles and sound, and a hit lands exactly when the hand (or foot) touches the other player.

### Giving items and flowers (right-click while holding something)

1. The giver holds out their hand and waits. With a **flower** (`minecraft:flowers` tag) they do it shyly.
2. The receiver sees a prompt with the icon and amount ("Ana offers you 12× Diamond") and accepts (`Y`) or declines (`N`).
3. If accepted, they step closer and the item moves to the receiver's inventory as their hands meet (if it doesn't fit, it drops at their feet).

The whole stack in hand is given; flowers go one at a time. Sneak + right-click uses the item as usual.

### Emote wheel (`G` key)

8 slots of your choice: with the wheel open, `E` edits the emotes and `I` edits the **interaction wheel**
(so you don't need another player in front of you to set it up). Looping emotes keep going **even while you walk**
until you stop them: open the wheel and pick an empty slot or the center ("Stop"). Many emit particles
(tears when crying, a cloud raining on you, music notes when dancing, hearts when in love…).

Repertoire (41): dance, robot dance, twerk, jump for joy, wave, clap, cheer, laugh, cry, sit, gaze at the horizon,
lie down, meditate, get angry, stomp, pound the ground, cross arms, think, shrug, facepalm, shy, in love,
blow a kiss, beg, show off, hero pose, sexy pose, defeated in the rain, bow, salute, point, stop!, yawn, spin,
backflip, shadowbox, sneak (actually crouches you: slow and without falling off edges),
and some that use **the item in your hand**: eat, sword practice, juggle and throw (it really throws it, far).

### Languages

Follows the game language: English, Spanish (all variants), French, German, Russian, Japanese and Simplified Chinese.

## Installation

1. Install NeoForge 21.1.x for Minecraft 1.21.1.
2. Put the mod `.jar` in the `mods/` folder of **both the client and the server** (required on both).

## Configuration

**Server** — `config/interacciones-server.toml` (synced to clients):

| Key | Default | What it does |
|---|---|---|
| `permitirForzar` | `true` | If `false`, Force mode disappears from the wheel and the server rejects it. |
| `permitirGolpes` | `true` | If `false`, slaps and pranks without consent are disabled. |
| `distanciaMaxima` | `2.5` | Maximum distance in blocks to ask or give; players then step closer on their own. |
| `segundosParaResponder` | `15` | Time to accept or decline. |
| `segundosEntreInteracciones` | `3` | Minimum cooldown between requests from the same player. |

**Client** — `config/interacciones-client.toml`: `ruletaEmociones` and `ruedaInteracciones` (easier from the game with `E`).

Keys (accept, decline, emote wheel) can be changed in *Options → Controls → Player Interactions*.

## Building

Requires **JDK 21**. Uses the bundled Gradle wrapper.

```sh
./gradlew build        # builds, runs the tests and produces the jar in build/libs/
./gradlew runClient    # launches Minecraft with the mod
```

The code is written in Spanish; the developer guide (code map, data flow and how-tos) is in
[`docs/DESARROLLO.md`](docs/DESARROLLO.md).
