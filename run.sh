#!/usr/bin/env bash
# 一键运行图书管理控制台程序（需要 JDK 17+）
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT="$DIR/out-app"
rm -rf "$OUT"
mkdir -p "$OUT"
find "$DIR/src" -name '*.java' > "$OUT/sources.txt"
javac -encoding UTF-8 -d "$OUT" @"$OUT/sources.txt"
exec java -Dfile.encoding=UTF-8 -cp "$OUT" Main
