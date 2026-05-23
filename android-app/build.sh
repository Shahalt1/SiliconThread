#!/usr/bin/env bash
# Build a signed debug APK for SiliconThread using only the Android SDK build-tools.
# No Gradle. Produces android-app/dist/SiliconThread-debug.apk.

set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

ANDROID_HOME="${ANDROID_HOME:-/Users/shahalthayyil/Library/Android/sdk}"
BUILD_TOOLS_VERSION="${BUILD_TOOLS_VERSION:-36.0.0}"
PLATFORM_VERSION="${PLATFORM_VERSION:-android-36.1}"

BT="${ANDROID_HOME}/build-tools/${BUILD_TOOLS_VERSION}"
PLATFORM="${ANDROID_HOME}/platforms/${PLATFORM_VERSION}"
ANDROID_JAR="${PLATFORM}/android.jar"

AAPT2="${BT}/aapt2"
D8="${BT}/d8"
ZIPALIGN="${BT}/zipalign"
APKSIGNER="${BT}/apksigner"

KEYSTORE="${HOME}/.android/debug.keystore"
KS_PASS="android"
KS_ALIAS="androiddebugkey"

for tool in "$AAPT2" "$D8" "$ZIPALIGN" "$APKSIGNER"; do
  [[ -x "$tool" ]] || { echo "Missing build tool: $tool"; exit 1; }
done
[[ -f "$ANDROID_JAR" ]] || { echo "Missing android.jar at $ANDROID_JAR"; exit 1; }
[[ -f "$KEYSTORE" ]] || { echo "Missing debug keystore at $KEYSTORE"; exit 1; }

BUILD="${ROOT}/build"
DIST="${ROOT}/dist"
rm -rf "$BUILD"
mkdir -p "$BUILD/compiled-res" "$BUILD/gen" "$BUILD/classes" "$DIST"

echo "[1/6] Compiling resources..."
"$AAPT2" compile --dir "${ROOT}/res" -o "${BUILD}/compiled-res.zip" >/dev/null

echo "[2/6] Linking resources and generating R.java..."
"$AAPT2" link \
  -I "$ANDROID_JAR" \
  --manifest "${ROOT}/AndroidManifest.xml" \
  -A "${ROOT}/assets" \
  -o "${BUILD}/app-unsigned.apk" \
  --java "${BUILD}/gen" \
  --min-sdk-version 24 \
  --target-sdk-version 34 \
  --no-version-vectors \
  --auto-add-overlay \
  -R "${BUILD}/compiled-res.zip"

echo "[3/6] Compiling Java sources..."
SOURCES_LIST="${BUILD}/sources.txt"
find "${ROOT}/src" -name "*.java" > "$SOURCES_LIST"
find "${BUILD}/gen" -name "*.java" >> "$SOURCES_LIST"
echo "    -> $(wc -l < "$SOURCES_LIST") files"
javac --release 17 \
  -classpath "$ANDROID_JAR" \
  -d "${BUILD}/classes" \
  -encoding UTF-8 \
  -Xlint:-options \
  @"$SOURCES_LIST"

echo "[4/6] Dexing classes (d8)..."
CLASS_LIST="${BUILD}/classes.txt"
( cd "${BUILD}/classes" && find . -name "*.class" | sed 's|^\./||' > "$CLASS_LIST" )
( cd "${BUILD}/classes" && "$D8" --release --min-api 24 --lib "$ANDROID_JAR" --output "${BUILD}" $(cat "$CLASS_LIST") )

echo "[5/6] Assembling unsigned APK..."
# aapt2 link already produced app-unsigned.apk with resources + manifest + assets.
# Add classes.dex into it.
cp "${BUILD}/app-unsigned.apk" "${BUILD}/app-tmp.apk"
( cd "${BUILD}" && /usr/bin/zip -j -q "${BUILD}/app-tmp.apk" classes.dex )

echo "    Aligning..."
"$ZIPALIGN" -f -p 4 "${BUILD}/app-tmp.apk" "${BUILD}/app-aligned.apk"

echo "[6/6] Signing with debug keystore..."
"$APKSIGNER" sign \
  --ks "$KEYSTORE" \
  --ks-pass "pass:${KS_PASS}" \
  --key-pass "pass:${KS_PASS}" \
  --ks-key-alias "$KS_ALIAS" \
  --v1-signing-enabled true \
  --v2-signing-enabled true \
  --out "${DIST}/SiliconThread-debug.apk" \
  "${BUILD}/app-aligned.apk"

"$APKSIGNER" verify --verbose "${DIST}/SiliconThread-debug.apk" | head -5

SIZE=$(/usr/bin/stat -f%z "${DIST}/SiliconThread-debug.apk")
echo
echo "✓ Built: dist/SiliconThread-debug.apk ($SIZE bytes)"
