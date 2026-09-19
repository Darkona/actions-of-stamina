#!/usr/bin/env bash
# Boots the real client headless (Xvfb + Mesa software GL) straight into a copy of a superflat test world and reports whether it
# joined without errors. Leaves a screenshot of the HUD in build/client-boot-check.png.
# Adapted from Adrift's scripts/client-boot-check.sh.
#
# Usage: [GRADLE_ARGS="-PwithoutFeathers -PwithCompat"] [COMMANDS='feathers spend @s 7;effect give @s minecraft:speed'] [SHOT=path.png]
#        scripts/client-boot-check.sh [TIMEOUT_SECONDS]
#   GRADLE_ARGS  extra Gradle flags (see build.gradle); COMMANDS typed into chat after joining, ';'-separated.
# Generates its own superflat world (run/bootworld) the first time. Every run: noon, clear weather, no mob spawns.
# The client runs on X display :97; while another check holds it (/tmp/.X97-lock), this one waits. Log lines listed in
# scripts/boot-check-known.txt are not counted as errors.
set -u
DIR="$(cd "$(dirname "$0")/.." && pwd)"
TIMEOUT="${1:-300}"
cd "$DIR" || exit 2
RUN=run
# Every process this run starts carries this tag in its environment, and cleanup kills only those: other checks,
# other projects' clients and the shared Gradle daemon are never touched.
export BOOTCHECK_TAG="$(basename "$DIR")-bootcheck-$$-$(date +%s)"
own_processes() {
    for d in /proc/[0-9]*; do
        p=${d#/proc/}
        [ "$p" = "$$" ] && continue
        grep -qzx "BOOTCHECK_TAG=$BOOTCHECK_TAG" "$d/environ" 2>/dev/null || continue
        tr '\0' ' ' < "$d/cmdline" 2>/dev/null | grep -q GradleDaemon && continue
        echo "$p"
    done
}
# A superflat world of its own (no structures, no caves to spawn in): generated once by a dedicated server run.
BOOTWORLD="$RUN/bootworld"
if [ ! -f "$BOOTWORLD/level.dat" ]; then
    echo "Generating a superflat world for the boot check..."
    PROPS="$RUN/server.properties"; mkdir -p "$RUN"; [ -f "$PROPS" ] && cp "$PROPS" "$PROPS.bootcheck-backup"
    echo "eula=true" > "$RUN/eula.txt"
    cat > "$PROPS" <<'PROPS_EOF'
level-name=bootworld
level-type=minecraft\:flat
generator-settings={"layers":[{"block":"minecraft:bedrock","height":1},{"block":"minecraft:dirt","height":2},{"block":"minecraft:grass_block","height":1}],"biome":"minecraft:plains"}
generate-structures=false
spawn-monsters=false
online-mode=false
server-port=25699
PROPS_EOF
    GEN_LOG="build/client-boot-check-world.log"; mkdir -p build
    ./gradlew runServer --no-configuration-cache ${GRADLE_ARGS:-} > "$GEN_LOG" 2>&1 &
    GEN=$!
    for _ in $(seq 1 300); do grep -qE 'Done \(' "$GEN_LOG" && break; kill -0 $GEN 2>/dev/null || break; sleep 1; done
    kill $(own_processes) 2>/dev/null
    wait $GEN 2>/dev/null
    if [ -f "$PROPS.bootcheck-backup" ]; then mv "$PROPS.bootcheck-backup" "$PROPS"; else rm -f "$PROPS"; fi
    [ -f "$BOOTWORLD/level.dat" ] || { echo "BOOTCHECK: could not generate the superflat world (see $GEN_LOG)"; exit 2; }
fi
rm -rf "$RUN/saves/aosboot"; mkdir -p "$RUN/saves"; cp -r "$BOOTWORLD" "$RUN/saves/aosboot"
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
# Interface scale 2 (not auto) and fullscreen-sized window: screenshots meant for people to look at.
sed -i "/^guiScale:/d" "$OPTS"; echo "guiScale:${GUI_SCALE:-2}" >> "$OPTS"
# No tutorial toast ("Move with W, A, S and D") over the screenshot.
sed -i "/^tutorialStep:/d" "$OPTS"; echo "tutorialStep:none" >> "$OPTS"
# NeoForge opens a screen that waits for a click when mods load with warnings, and quick play never starts. The
# warnings are still logged.
NEO_CLIENT="$RUN/config/neoforge-client.toml"; mkdir -p "$RUN/config"; touch "$NEO_CLIENT"
sed -i "/^showLoadWarnings *=/d" "$NEO_CLIENT"; echo "showLoadWarnings = false" >> "$NEO_CLIENT"

LOG="build/client-boot-check.log"; mkdir -p build; : > "$LOG"
GAME_LOG="$RUN/logs/latest.log"; rm -f "$GAME_LOG"
SHOT="${SHOT:-build/client-boot-check.png}"; rm -f "$SHOT"

# One check at a time on display :97: wait for the one holding it to end.
for _ in $(seq 1 1800); do [ -e /tmp/.X97-lock ] || break; sleep 1; done
[ -e /tmp/.X97-lock ] && { echo "BOOTCHECK: display :97 still taken after 30 minutes"; exit 2; }

export LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=llvmpipe MESA_GL_VERSION_OVERRIDE=3.3 MESA_GLSL_VERSION_OVERRIDE=330
XAUTH="$DIR/build/$BOOTCHECK_TAG.xauth"
xvfb-run -n 97 -f "$XAUTH" -s "-screen 0 1920x1080x24" ./gradlew runBootCheck --no-configuration-cache ${GRADLE_ARGS:-} > "$LOG" 2>&1 &
PID=$!
logs() { cat "$LOG" "$GAME_LOG" 2>/dev/null; }

# Fatal log lines, including a chat command the game rejected: the setup and COMMANDS must all run.
FAIL_RE='InvalidInjectionException|Mixin apply failed|Preparing crash report|Exception in thread "Render thread"|Failed to load .*actionsofstamina|ClientModLoader/LOADING\]: Mod actionsofstamina |\[CHAT\] (Incorrect argument for command|Unknown or incomplete command)'
OK_RE='joined the game'
verdict="TIMEOUT after ${TIMEOUT}s"
for _ in $(seq 1 "$TIMEOUT"); do
    if logs | grep -qE "$FAIL_RE"; then verdict="FAIL"; break; fi
    if logs | grep -qE "$OK_RE"; then
        sleep 20
        X="env DISPLAY=:97 XAUTHORITY=$XAUTH xdotool"
        SETUP='gamerule send_command_feedback false;difficulty peaceful;time set noon;weather clear;gamerule advance_time false;gamerule advance_weather false;gamerule spawn_mobs false'
        IFS=';' read -ra CMDS <<< "$SETUP;${COMMANDS:-}"
        for cmd in "${CMDS[@]}"; do
            [ -z "$cmd" ] && continue
            $X key t; sleep 1; $X type --delay 20 "/$cmd"; $X key Return; sleep 1
        done
        # Chat messages fade after 10 s; wait them out (and a margin) so no chat shows in the screenshot.
        sleep 13
        # PRE_SHOT: what to do right before the screenshot, in order: a key to press ("F5" for the third-person view),
        # "down:KEY" / "up:KEY" to hold and release one, "click:N" / "hold:N" / "release:N" for mouse button N,
        # "sleep:S" to wait S seconds, "cmd:COMMAND" to run a command then (underscores for spaces; no chat feedback).
        for step in ${PRE_SHOT:-}; do
            case "$step" in
                down:*) $X keydown "${step#down:}" ;;
                up:*) $X keyup "${step#up:}" ;;
                click:*) $X click "${step#click:}" ;;
                hold:*) $X mousedown "${step#hold:}" ;;
                release:*) $X mouseup "${step#release:}" ;;
                sleep:*) sleep "${step#sleep:}" ;;
                cmd:*) cmd="${step#cmd:}"; $X key t; sleep 0.5; $X type --delay 10 "/${cmd//_/ }"; $X key Return; sleep 0.5 ;;
                *) $X key "$step"; sleep 1 ;;
            esac
        done
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

kill $(own_processes) 2>/dev/null; sleep 3; kill -9 $(own_processes) 2>/dev/null
rm -f "$XAUTH"

# Suspicious lines: ERROR level, an exception or error class starting a line, uncaught exceptions; minus known ones.
KNOWN="$DIR/scripts/boot-check-known.txt"
problems=$(logs | grep -E '/ERROR\]|^[A-Za-z_$][A-Za-z0-9_$.]*(Exception|Error)(:|$)|Exception in thread' \
    | { if [ -f "$KNOWN" ]; then grep -vEf <(grep -vE '^[[:space:]]*(#|$)' "$KNOWN"); else cat; fi; } | sort -u)
[ "$verdict" = PASS ] && [ -n "$problems" ] && verdict="FAIL (errors in the log)"
echo "BOOTCHECK: $verdict"
[ -n "$problems" ] && echo "$problems" | head -20
logs | grep -E "$OK_RE" | head -2
[ -f "$SHOT" ] && echo "screenshot: $SHOT"
exit $([ "$verdict" = PASS ] && echo 0 || echo 1)
