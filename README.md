# Player Interactions

**Hug your friends. High-five your teammates. Slap that one guy who stole your diamonds.**

Player Interactions lets players actually *interact* with each other: right-click another player, pick a gesture
from a wheel, and both characters walk up to each other and act it out with full-body animations, particles and sound.
Add item gifting hand to hand and 41 emotes, and your server suddenly feels a lot more alive.

## How it works

| Do this | And this happens |
|---|---|
| **Right-click a player** with an empty hand | The interaction wheel opens: hug, kiss, high-five, slap… |
| **Right-click a player** holding an item | You hold it out as a gift; they accept and take it from your hand |
| Press **`G`** | The emote wheel opens: dance, cry, laugh, sit, backflip… |
| Press **`Y`** / **`N`** | Accept or decline what someone asks you |

Every wheel has 8 slots, and **you choose what goes in each one** (`E` while the wheel is open).

## Interactions

Pick one and both players glide to the right distance and perform it together: the hug is a real hug,
and the slap lands exactly when the hand touches the face.

| Wholesome (they ask first) | Not so wholesome (they don't) |
|---|---|
| Hug · High-five · Handshake · Fist bump · Kiss on the cheek · Kiss on the lips · Head pat · Hold hands · Bro greeting · Kiss the hand · Tickle · Comfort · Dance together | Slap (½ ♥) · Punch (1 ♥) · Shove · Kick in the butt (1 ♥) · Spank |

**Consent matters.** Gestures send a request the other player can accept or decline. Feeling bold? **Force** it
(`Tab` or the center of the wheel) and watch them react: a forced kiss makes them pull back and wipe their mouth,
a forced hug leaves them stiff and struggling to get free.

**Slaps and pranks** never ask, but their damage counts as your own attack, so they follow your server's PvP rules.
The kick and the spank sneak up **from behind**.

## Gifts hand to hand

Right-click a player while holding something. You hold it out and they see exactly what you're offering
(*"Steve offers you 12× Diamond"*). If they accept, you step closer and the item changes hands the moment your hands meet.

Flowers are special: you offer them one at a time… and a little shyly.

## 41 emotes

Dance · Robot dance · Twerk · Jump for joy · Wave · Clap · Cheer · Laugh · Cry · Sit · Gaze at the horizon ·
Lie down · Meditate · Get angry · Stomp · Pound the ground · Cross arms · Think · Shrug · Facepalm · Shy · In love ·
Blow a kiss · Beg · Show off · Hero pose · Sexy pose · Defeated in the rain · Bow · Salute · Point · Stop! · Yawn ·
Spin · Backflip · Shadowbox · Sneak

…and four that use **the item in your hand**: eat, sword practice, juggle, and throw (yes, it really throws it).

Looping emotes keep going **while you walk around**, and many come with effects: tears when you cry,
a personal rain cloud when you're defeated, music notes when you dance, hearts when you're in love.

## For server owners

The mod is required on **both the client and the server**. Everything is validated on the server,
and these rules live in `config/interacciones-server.toml`:

| Setting | Default | What it does |
|---|---|---|
| `permitirForzar` | `true` | Allow forcing gestures without consent |
| `permitirGolpes` | `true` | Allow slaps and pranks |
| `distanciaMaxima` | `2.5` | Maximum distance (blocks) to start an interaction |
| `segundosParaResponder` | `15` | Seconds to accept or decline a request |
| `segundosEntreInteracciones` | `3` | Cooldown between requests from the same player |

## Good to know

- **Languages:** English, Spanish, French, German, Russian, Japanese and Simplified Chinese.
- **Keys** can be changed in *Options → Controls → Player Interactions*.
- **Sneak + right-click** uses your item normally, so the mod never gets in the way.
- Made for **NeoForge 1.21.1**. There is no Fabric or Forge version.

<!-- modrinth_exclude.start -->
## Building

Requires **JDK 21**. Uses the bundled Gradle wrapper.

```sh
./gradlew build        # builds, runs the tests and produces build/libs/player-interactions-<version>.jar
./gradlew runClient    # launches Minecraft with the mod
```

The code is written in Spanish; the developer guide (code map, data flow and how-tos) is in
[`docs/DESARROLLO.md`](docs/DESARROLLO.md).
<!-- modrinth_exclude.end -->
