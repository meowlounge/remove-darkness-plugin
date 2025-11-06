# Remove Darkness Plugin

A lightweight Paper plugin that prevents the Darkness status effect from affecting players unless they explicitly opt in. Ideal for servers that want to remove the blindness caused by Wardens or sculk shriekers while still letting players choose the vanilla experience.

## Features
- Cancels and removes the Darkness potion effect for opted-out players automatically.
- Lets each player toggle the effect for themselves with a single command.
- Remembers player preferences across restarts via the player persistent data container; no config edits needed.

## Requirements
- Paper 1.21.10 (or a compatible fork) — built against the Paper API.
- Java 21 for both the server runtime and building the plugin.

## Installation
1. Download or build the plugin jar (see Development).
2. Place the jar in your server's `plugins` directory.
3. Restart or reload the server; the plugin manages its configuration automatically.

## Usage
- By default, Darkness is blocked for everyone. Players who prefer the effect can opt in.
- The plugin re-evaluates preferences whenever a player joins or a Darkness effect is applied.

### Commands
- `/darkness` - toggle Darkness on or off for yourself.
- `/darkness allow` / `/darkness on` - explicitly allow Darkness.
- `/darkness deny` / `/darkness off` - keep Darkness removed.
- `/darkness toggle` - synonym for running the command without arguments.
- `/darkness status` - show your current preference.

The command is only available to players; no permissions are required.

## How It Works
The plugin stores a per-player “opt-in” flag inside the Paper persistent data container (`PersistentDataContainer`) using the key `remove-darkness:darkness_opt_in`. When a player has not opted in, the listener cancels Darkness potion applications and the service schedules a task for the next tick to strip any residual effect.

## Configuration
There is no configuration file to manage. Player choices live in their own persistent data container entry and travel with the player data.

## Development
The project uses Gradle with the Kotlin DSL and the Shadow plugin to produce a shaded jar.

```sh
# On Linux/macOS
./gradlew build

# On Windows PowerShell
.\gradlew.bat build
```

The shaded artifact is written to `build/libs/remove-darkness-plugin-<version>-all.jar`.

To spin up a local test server with the plugin already on the classpath, run:

```sh
./gradlew runServer
```

This downloads the matching Paper version (1.21.10) and launches it with the plugin included.
