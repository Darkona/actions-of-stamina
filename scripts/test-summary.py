#!/usr/bin/env python3
"""Condenses a Gradle GameTest or boot-check log to what matters: verdict, failed tests with their message,
the first root cause and the build result. Usage: scripts/test-summary.py LOG [LOG...]"""
import re
import sys


def summarize(path):
    try:
        lines = open(path, errors="replace").read().splitlines()
    except OSError as e:
        print(f"{path}: cannot read ({e})")
        return
    out = []
    complete = None
    for l in lines:
        m = re.search(r"(\d+) GAME TESTS COMPLETE", l)
        if m:
            complete = m.group(1)
    passed = [l for l in lines if re.search(r"All \d+ required tests passed", l)]
    failed_hdr = [l for l in lines if re.search(r"\d+ required tests failed", l)]
    verdict = "PASS" if passed and not failed_hdr else ("FAIL" if failed_hdr else "NO RESULT")
    out.append(f"{path}: {verdict}" + (f" ({complete} tests)" if complete else ""))
    for l in lines:
        if "LogTestReporter" in l and re.search(r"failed( at [^!]*)?!", l):
            out.append("  " + l.split("]: ", 1)[-1])
    for l in lines:
        if re.search(r"optional tests failed|required tests failed", l):
            out.append("  " + l.split("]: ", 1)[-1].strip())
    for i, l in enumerate(lines):
        if l.startswith("Caused by:") or re.search(r"^(\S*Exception|\S*Error)\b", l):
            out.append("  root: " + l[:220])
            break
    for l in lines:
        if "/WARN]" in l or "/DEBUG]" in l:
            continue
        if re.search(r"error: |Execution failed for task|BUILD (FAILED|SUCCESSFUL)|^BOOTCHECK:|InvalidInjectionException|Mixin apply failed", l):
            out.append("  " + l[:220])
    print("\n".join(out))


for p in sys.argv[1:]:
    summarize(p)
