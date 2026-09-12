#!/usr/bin/env bash
# Boots the real client headless (Xvfb + Mesa software GL) into a copy of a superflat test world and reports whether it
# joined without errors. Leaves a screenshot of the HUD in build/client-boot-check.png.
# Adapted from Adrift's scripts/client-boot-check.sh, through Feathers of Fatigue's 1.18.2 one. Minecraft 1.18.2 can't open
# a singleplayer world from the command line (no --quickPlaySingleplayer), so a dedicated server of its own
# (run/bootserver, a free port from 25699) hosts the world and the client connects to it on startup (--server/--port).
#
# Usage: [GRADLE_ARGS="-PwithoutFeathers -PwithCompat"] [COMMANDS='feathers spend @s 7;effect give @s minecraft:speed'] [SHOT=path.png]
#        scripts/client-boot-check.sh [TIMEOUT_SECONDS]
#   GRADLE_ARGS  extra Gradle flags (see build.gradle); COMMANDS typed into chat after joining, ';'-separated.
# Generates its own superflat world (run/bootserver/bootworld) the first time. Every run: noon, clear weather, no mob spawns.
set -u
DIR="$(cd "$(dirname "$0")/.." && pwd)"
TIMEOUT="${1:-300}"
cd "$DIR" || exit 2
RUN=run
SRV="$RUN/bootserver"
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
# A free port and a free X display, so this check can run next to other projects' checks.
PORT=25699
while ss -ltnH "sport = :$PORT" 2>/dev/null | grep -q .; do PORT=$((PORT + 1)); done
DISP=97
while [ -e "/tmp/.X$DISP-lock" ] || [ -e "/tmp/.X11-unix/X$DISP" ]; do DISP=$((DISP + 1)); done

mkdir -p build "$SRV"
# Gradle only builds and writes the launch scripts; the server and the client then run outside it, so both can run at
# once without two Gradle builds on the same project.
PREP_LOG="build/client-boot-check-prepare.log"
./gradlew classes createAosBootLaunchScript createAosBootServerLaunchScript --no-configuration-cache \
    -PbootCheckPort=$PORT ${GRADLE_ARGS:-} > "$PREP_LOG" 2>&1 \
    || { echo "BOOTCHECK: build failed (see $PREP_LOG)"; exit 2; }
SERVER_SH="build/moddev/runAosBootServer.sh"; CLIENT_SH="build/moddev/runAosBoot.sh"

echo "eula=true" > "$SRV/eula.txt"
write_props() {
    cat > "$SRV/server.properties" <<PROPS_EOF
level-name=$1
level-type=minecraft\:flat
generator-settings={"layers":[{"block":"minecraft:bedrock","height":1},{"block":"minecraft:dirt","height":2},{"block":"minecraft:grass_block","height":1}],"biome":"minecraft:plains"}
generate-structures=false
spawn-monsters=false
spawn-npcs=false
difficulty=peaceful
gamemode=survival
force-gamemode=true
spawn-protection=0
online-mode=false
enforce-secure-profile=false
server-port=$PORT
PROPS_EOF
}
# Waits until the server log says Done; fails if the server exits first.
wait_done() {
    for _ in $(seq 1 "$TIMEOUT"); do
        grep -qE 'Done \(' "$1" && return 0
        kill -0 "$2" 2>/dev/null || return 1
        sleep 1
    done
    return 1
}
# A superflat world of its own (no structures, no caves to spawn in): generated once by the boot check server.
BOOTWORLD="$SRV/bootworld"
if [ ! -f "$BOOTWORLD/level.dat" ]; then
    echo "Generating a superflat world for the boot check..."
    write_props bootworld
    GEN_LOG="build/client-boot-check-world.log"
    bash "$SERVER_SH" > "$GEN_LOG" 2>&1 &
    GEN=$!
    wait_done "$GEN_LOG" $GEN
    kill $(own_processes) 2>/dev/null
    wait $GEN 2>/dev/null
    [ -f "$BOOTWORLD/level.dat" ] || { echo "BOOTCHECK: could not generate the superflat world (see $GEN_LOG)"; exit 2; }
fi
rm -rf "$SRV/aosboot"; cp -r "$BOOTWORLD" "$SRV/aosboot"
rm -f "$SRV/aosboot/session.lock"
# A fresh player, placed at the world spawn in survival, or the HUD under test (hearts, food, stamina) is hidden.
rm -rf "$SRV/aosboot/playerdata"
# Server configs come from run/bootserver/defaultconfigs (Forge copies them into a world without its own), so a check
# can set options there, e.g. defaultconfigs/actionsofstamina-server.toml.
rm -rf "$SRV/aosboot/serverconfig"
write_props aosboot
# The client plays as AoSBoot (build.gradle), an operator so the setup commands run. Offline UUID, as the server computes it.
python3 - "$SRV/ops.json" <<'PY'
import hashlib, json, sys, uuid
name = "AoSBoot"
u = uuid.UUID(bytes=hashlib.md5(("OfflinePlayer:" + name).encode()).digest(), version=3)
json.dump([{"uuid": str(u), "name": name, "level": 4, "bypassesPlayerLimit": False}], open(sys.argv[1], "w"))
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
# No multiplayer warning screen.
sed -i "/^skipMultiplayerWarning:/d" "$OPTS"; echo "skipMultiplayerWarning:true" >> "$OPTS"

LOG="build/client-boot-check.log"; : > "$LOG"
SERVER_LOG="build/client-boot-check-server.log"; : > "$SERVER_LOG"
GAME_LOG="$RUN/logs/latest.log"; rm -f "$GAME_LOG"
SHOT="${SHOT:-build/client-boot-check.png}"; rm -f "$SHOT"

bash "$SERVER_SH" > "$SERVER_LOG" 2>&1 &
SERVER_PID=$!
if ! wait_done "$SERVER_LOG" $SERVER_PID; then
    kill $(own_processes) 2>/dev/null; sleep 3; kill -9 $(own_processes) 2>/dev/null
    echo "BOOTCHECK: FAIL (the server didn't start, see $SERVER_LOG)"
    grep -E 'Exception|Error' "$SERVER_LOG" | head -5
    exit 1
fi

export LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=llvmpipe MESA_GL_VERSION_OVERRIDE=3.3 MESA_GLSL_VERSION_OVERRIDE=330
XAUTH="$DIR/build/$BOOTCHECK_TAG.xauth"
xvfb-run -n $DISP -f "$XAUTH" -s "-screen 0 1920x1080x24" bash "$CLIENT_SH" > "$LOG" 2>&1 &
PID=$!
logs() { cat "$LOG" "$GAME_LOG" "$SERVER_LOG" 2>/dev/null; }

FAIL_RE='InvalidInjectionException|Mixin apply failed|Preparing crash report|Exception in thread "Render thread"|Failed to load builder \(actionsofstamina'
# The server let the client in (the client doesn't log chat on 1.18.2).
OK_RE='AoSBoot joined the game'
verdict="TIMEOUT after ${TIMEOUT}s"
for _ in $(seq 1 "$TIMEOUT"); do
    if logs | grep -qE "$FAIL_RE"; then verdict="FAIL"; break; fi
    if logs | grep -qE "$OK_RE"; then
        sleep 20
        X="env DISPLAY=:$DISP XAUTHORITY=$XAUTH xdotool"
        SETUP='gamerule sendCommandFeedback false;difficulty peaceful;time set noon;weather clear;gamerule doDaylightCycle false;gamerule doWeatherCycle false;gamerule doMobSpawning false'
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
                cmd:*) cmd="${step#cmd:}"; $X key t; sleep 1; $X type --delay 20 "/${cmd//_/ }"; $X key Return; sleep 1 ;;
                *) $X key "$step"; sleep 1 ;;
            esac
        done
        # F2: the game takes its own screenshot (no image tools needed).
        rm -rf "$RUN/screenshots"
        DISPLAY=:$DISP XAUTHORITY="$XAUTH" xdotool search --name "Minecraft" windowactivate --sync key F2 2>/dev/null \
            || DISPLAY=:$DISP XAUTHORITY="$XAUTH" xdotool key F2 2>/dev/null
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
echo "BOOTCHECK: $verdict"
logs | grep -E "$FAIL_RE" | head -5
logs | grep -E "$OK_RE" | head -2
[ -f "$SHOT" ] && echo "screenshot: $SHOT"
exit $([ "$verdict" = PASS ] && echo 0 || echo 1)
