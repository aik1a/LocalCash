#!/usr/bin/env bash
set -euo pipefail
for ((i=0; i<120; i++)); do
    adb emu geo fix -70.66 -33.45
    sleep 1
done
