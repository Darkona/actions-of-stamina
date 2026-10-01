#!/usr/bin/env bash
# Publishes to mavenLocal the mods of our own that this repo compiles against, cloned from GitHub into deps/, so the
# build resolves them as on a dev machine. It reads gradle.properties:
#   ci_deps=repo:version_property,...  direct dependencies only; the indirect ones come from each dependency's own
#                                      ci_deps, read from its gradle.properties after cloning it
#   ci_deps_owner=Darkona              GitHub owner of those repos (default Darkona)
# dev: each dependency at its tag v<version> if it exists, else at its branch named like this repo's minecraft_label,
#      else at its default branch.
# release: the tag only; a missing tag fails the build, so dependencies are always released first.
# Each repo is cloned once and published after its own dependencies (deepest first). Without ci_deps it does nothing.
set -euo pipefail
mode=${1:-dev}
case "$mode" in dev|release) ;; *) echo "usage: $0 [dev|release]" >&2; exit 2 ;; esac
root=$PWD
prop() {   # name file: the value of a property, or nothing
    sed -n "s/^[[:space:]]*$1[[:space:]]*=[[:space:]]*//p" "$2" | tail -n 1 | tr -d '\r' | sed 's/[[:space:]]*$//'
}
label=$(prop minecraft_label gradle.properties)
declare -A seen=()

clone() {   # owner repo version: clones into deps/<repo>
    local url="https://github.com/$1/$2.git" ref="v$3"
    if ! git ls-remote --exit-code --tags "$url" "refs/tags/$ref" >/dev/null; then
        if [ "$mode" = release ]; then echo "::error::$2 has no tag $ref"; exit 1; fi
        if [ -n "$label" ] && git ls-remote --exit-code --heads "$url" "refs/heads/$label" >/dev/null; then
            ref=$label
        else
            ref=$(git ls-remote --symref "$url" HEAD | sed -n 's|^ref: refs/heads/\(.*\)\tHEAD$|\1|p')
            [ -n "$ref" ] || { echo "::error::cannot find the default branch of $url"; exit 1; }
        fi
    fi
    echo "$2 @ $ref"
    rm -rf "$root/deps/$2"
    git clone -q --depth 1 --branch "$ref" "$url" "$root/deps/$2"
}

resolve() {   # dir: clones and publishes the ci_deps of <dir>/gradle.properties, deepest first
    local dir=$1 owner entry repo vprop version
    local -a entries=()
    owner=$(prop ci_deps_owner "$dir/gradle.properties")
    IFS=, read -ra entries <<< "$(prop ci_deps "$dir/gradle.properties")"
    for entry in "${entries[@]}"; do
        entry=${entry//[[:space:]]/}
        [ -n "$entry" ] || continue
        repo=${entry%%:*} vprop=${entry#*:}
        if [ "$repo" = "$entry" ] || [ -z "$repo" ] || [ -z "$vprop" ]; then
            echo "::error::ci_deps entry '$entry' in $dir/gradle.properties is not repo:version_property"; exit 1
        fi
        version=$(prop "$vprop" "$dir/gradle.properties")
        [ -n "$version" ] || { echo "::error::$vprop is not set in $dir/gradle.properties"; exit 1; }
        if [ -n "${seen[$repo]:-}" ]; then
            [ "${seen[$repo]}" = "$version" ] || echo "::warning::$repo $version asked by $dir, using ${seen[$repo]}"
            continue
        fi
        seen[$repo]=$version
        clone "${owner:-Darkona}" "$repo" "$version"
        if grep -q '^[[:space:]]*ci_deps[[:space:]]*=' "$root/deps/$repo/gradle.properties"; then
            resolve "$root/deps/$repo"
        elif [ -f "$root/deps/$repo/.github/ci-deps.sh" ]; then
            # A dependency tagged before ci_deps existed: its own script knows what it needs.
            (cd "$root/deps/$repo" && bash .github/ci-deps.sh "$mode")
        fi
        echo "publishing $repo"
        (cd "$root/deps/$repo" && ./gradlew -q publishToMavenLocal)
    done
}

resolve "$root"
