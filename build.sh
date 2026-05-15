#!/bin/bash
# ChatApp Build Script — works flat (all .java files in same folder)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT="$SCRIPT_DIR/out"
mkdir -p "$OUT"

echo "🔨 Compiling..."

javac -d "$OUT" \
  "$SCRIPT_DIR/Message.java" \
  "$SCRIPT_DIR/Server.java" \
  "$SCRIPT_DIR/ServerGUI.java" \
  "$SCRIPT_DIR/Client.java" \
  "$SCRIPT_DIR/ClientGUI.java"

if [ $? -ne 0 ]; then
  echo "❌ Compilation failed."
  exit 1
fi

echo "✅ Done! Run with:"
echo "   Server: java -cp \"$OUT\" ServerGUI"
echo "   Client: java -cp \"$OUT\" ClientGUI"
