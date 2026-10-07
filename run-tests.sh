#!/usr/bin/env bash
# 一键运行全部测试（需要 JDK 17+）
# 编译 src/ 与 tests/ 下所有 .java，运行 TestRunner。
# 测试类命名约定：类名以 Test 结尾；测试方法为无参 public void 且以 test 开头。
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT="$DIR/out"
rm -rf "$OUT"
mkdir -p "$OUT"
find "$DIR/src" "$DIR/tests" -name '*.java' > "$OUT/sources.txt"
javac -encoding UTF-8 -d "$OUT" @"$OUT/sources.txt"
java -Dfile.encoding=UTF-8 -cp "$OUT" TestRunner
