# Stamina Backends

| Backend | When | What you get |
|---|---|---|
| **Green Feathers** | Green Feathers 2.0+ is installed (and `backend` is `AUTO` or `FEATHERS`) | Green Feathers' feathers, regeneration, strain, exhaustion, effects, armor weight, climate and HUD. AoS spends under its own sources (`actionsofstamina:sprint`, `actionsofstamina:parcool/dodge`, ...). |
| **Internal** | Green Feathers isn't installed, or `backend = INTERNAL` | A small, light bar: a maximum, regeneration after a short delay, and exhaustion (once you run out, you must regain part of the bar before you can act again). It is drawn as a thin bar above the food bar. |

The server picks the backend when it starts and tells each client which one it uses. Green Feathers is an
**optional** dependency: AoS works without it.

Creative and spectator players never spend stamina.
