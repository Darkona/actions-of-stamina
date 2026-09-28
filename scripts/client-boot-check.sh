#!/usr/bin/env bash
# Boots the real client headless (Xvfb + Mesa software GL) straight into a copy of run/world and reports whether it
# joined without errors. Leaves a screenshot of the HUD in build/client-boot-check.png.
# Adapted from Adrift's scripts/client-boot-check.sh.
#
# Usage: [GRADLE_ARGS="-PwithoutFeathers -PwithCompat"] [COMMANDS='feathers spend @s 7;effect give @s minecraft:speed'] [SHOT=path.png]
#        scripts/client-boot-check.sh [TIMEOUT_SECONDS]
#   GRADLE_ARGS  extra Gradle flags (see build.gradle); COMMANDS typed into chat after joining, ';'-separated.
# Needs a world in run/world: any `./gradlew runServer` leaves one behind.
set -u
DIR="$(cd "$(dirname "$0")/.." && pwd)"
TIMEOUT="${1:-300}"
cd "$DIR" || exit 2
RUN=run
[ -d "$RUN/world" ] || { echo "BOOTCHECK: no world to join (run ./gradlew runServer once)"; exit 2; }
rm -rf "$RUN/saves/aosboot"; mkdir -p "$RUN/saves"; cp -r "$RUN/world" "$RUN/saves/aosboot"
rm -f "$RUN/session.lock" "$RUN/saves/aosboot/session.lock"
# A fresh player takes the world's game type (set to survival below) instead of whatever the last run left.
rm -rf "$RUN/saves/aosboot/playerdata"
# Pre-confirm the "Experimental Settings" screen, which waits for a click, turn cheats on, and play survival, in the copy only.
python3 - "$RUN/saves/aosboot/level.dat" <<'PY'
import gzip, sys
f = sys.argv[1]; b = bytearray(gzip.open(f).read())
for name in (b"allowCommands", b"confirmedExperimentalSettings"):
    tag = b"\x01" + len(name).to_bytes(2, "big") + name
    i = b.find(tag)
    if i >= 0: b[i + len(tag)] = 1
# Survival, or the HUD under test (hearts, food, feathers) is hidden.
tag = b"\x03" + len(b"GameType").to_bytes(2, "big") + b"GameType"
i = b.find(tag)
if i >= 0: b[i + len(tag):i + len(tag) + 4] = (0).to_bytes(4, "big")
gzip.open(f, "wb").write(bytes(b))
PY
OPTS="$RUN/options.txt"; touch "$OPTS"
for cat in master music record weather block hostile neutral player ambient voice; do
    sed -i "/^soundCategory_$cat:/d" "$OPTS"; echo "soundCategory_$cat:0.0" >> "$OPTS"
done
# A fresh game dir opens the first-launch accessibility screen, which waits for a click.
sed -i "/^onboardAccessibility:/d" "$OPTS"; echo "onboardAccessibility:false" >> "$OPTS"

LOG="build/client-boot-check.log"; mkdir -p build; : > "$LOG"
GAME_LOG="$RUN/logs/latest.log"; rm -f "$GAME_LOG"
SHOT="${SHOT:-build/client-boot-check.png}"; rm -f "$SHOT"

export LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=llvmpipe MESA_GL_VERSION_OVERRIDE=3.3 MESA_GLSL_VERSION_OVERRIDE=330
XAUTH="$DIR/build/client-boot-check.xauth"
xvfb-run -n 97 -f "$XAUTH" -s "-screen 0 1280x720x24" ./gradlew runBootCheck --no-configuration-cache ${GRADLE_ARGS:-} > "$LOG" 2>&1 &
PID=$!
logs() { cat "$LOG" "$GAME_LOG" 2>/dev/null; }

FAIL_RE='InvalidInjectionException|Mixin apply failed|Preparing crash report|Exception in thread "Render thread"|Failed to load builder \(actionsofstamina'
OK_RE='joined the game'
verdict="TIMEOUT after ${TIMEOUT}s"
for _ in $(seq 1 "$TIMEOUT"); do
    if logs | grep -qE "$FAIL_RE"; then verdict="FAIL"; break; fi
    if logs | grep -qE "$OK_RE"; then
        sleep 20
        X="env DISPLAY=:97 XAUTHORITY=$XAUTH xdotool"
        IFS=';' read -ra CMDS <<< "${COMMANDS:-}"
        for cmd in "${CMDS[@]}"; do
            [ -z "$cmd" ] && continue
            $X key t; sleep 1; $X type --delay 20 "/$cmd"; $X key Return; sleep 1
        done
        [ -n "${COMMANDS:-}" ] && sleep 4
        # F2: the game takes its own screenshot (no image tools needed).
        rm -rf "$RUN/screenshots"
        DISPLAY=:97 XAUTHORITY="$XAUTH" xdotool search --name "Minecraft" windowactivate --sync key F2 2>/dev/null \
            || DISPLAY=:97 XAUTHORITY="$XAUTH" xdotool key F2 2>/dev/null
        sleep 3
        latest=$(ls -t "$RUN"/screenshots/*.png 2>/dev/null | head -1)
        [ -n "$latest" ] && cp "$latest" "$SHOT"
        logs | grep -qE "$FAIL_RE" && verdict="FAIL" || verdict="PASS"
        break
    fi
    kill -0 $PID 2>/dev/null || { verdict="EXITED EARLY"; break; }
    sleep 1
done

pkill -f -- "bootCheckRun(Program|Vm)Args" 2>/dev/null; kill $PID 2>/dev/null; sleep 3; pkill -9 -f -- "bootCheckRun(Program|Vm)Args" 2>/dev/null
pkill -f -- "-auth $XAUTH" 2>/dev/null; rm -f "$XAUTH"
echo "BOOTCHECK: $verdict"
logs | grep -E "$FAIL_RE" | head -5
logs | grep -E "$OK_RE" | head -2
[ -f "$SHOT" ] && echo "screenshot: $SHOT"
exit $([ "$verdict" = PASS ] && echo 0 || echo 1)
