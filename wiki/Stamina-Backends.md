# Stamina Backends

| Backend | When | What you get |
|---|---|---|
| **Feathers of Fatigue** | Feathers of Fatigue 2.0+ is installed (and `backend` is `AUTO` or `FEATHERS`) | Feathers of Fatigue's feathers, regeneration, strain, exhaustion, effects, armor weight, climate and HUD. AoS spends under its own sources (`actionsofstamina:sprint`, `actionsofstamina:walljump/wall_jump`, ...). |
| **Internal** | Feathers of Fatigue isn't installed, or `backend = INTERNAL` | A small, light bar: a maximum, regeneration after a short delay, and exhaustion (once you run out, you must regain part of the bar before you can act again). It is drawn as a thin bar above the food bar. |

The server picks the backend when it starts and tells each client which one it uses. Feathers of Fatigue is an **optional** dependency: AoS works without it.

Creative and spectator players never spend stamina.

![The internal bar, a thin line above the food bar, while sprinting](images/internal.png)

*Without Feathers of Fatigue: AoS's own thin bar above the food bar.*
