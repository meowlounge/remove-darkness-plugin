# Remove Darkness Plugin — Architecture Notes

## Goals
- Block the Darkness potion effect for players unless they explicitly opt in.
- Keep the code path short enough that new triggers or subcommands can be shipped quickly.

## Runtime Flow
1. `DarknessService` stores a per-player opt-in flag in the Paper `PersistentDataContainer` using the key `remove-darkness:darkness_opt_in`.
2. `DarknessListener` cancels Darkness potion applications for players who have not opted in and queues a task on the next tick to strip any lingering effect.
3. The `/darkness` command delegates to the service to flip or read the flag, then lets the service re-apply the preference immediately when needed.

## Command Cheatsheet
| Command | Description |
| --- | --- |
| `/darkness` | Toggle Darkness on or off for yourself. |
| `/darkness on` | Opt back into the vanilla Darkness effect. |
| `/darkness off` | Keep Darkness suppressed. |
| `/darkness status` | Show your current preference. |

## Extension Points
- Add new subcommands by adding another `SubCommand` enum entry in `DarknessCommand.kt` and pointing it at a small handler.
- Fire `DarknessService.applyPreference(player)` from other events (for example, world changes) if you need the plugin to re-enforce the preference elsewhere.
- Swap the storage layer by editing only `DarknessService`; listeners and the command consume a narrow interface and stay untouched.
