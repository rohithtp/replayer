#!/usr/bin/env bash
set -euo pipefail

# -----------------------------
# Config
# -----------------------------
export OLLAMA_API_BASE="${OLLAMA_API_BASE:-http://localhost:11434}"
AIDER_MODEL="${AIDER_MODEL:-ollama_chat/qwen2.5-coder:14b}"
AIDER_EDITOR_MODEL="${AIDER_EDITOR_MODEL:-ollama_chat/qwen2.5-coder:7b}"
MAP_TOKENS="${MAP_TOKENS:-1024}"
AIDER_PYTHON="${AIDER_PYTHON:-python3.12}"

echo "🔍 Checking prerequisites..."

if ! command -v python3 >/dev/null 2>&1; then
  echo "❌ python3 is not installed."
  exit 1
fi

if ! command -v "$AIDER_PYTHON" >/dev/null 2>&1; then
  echo "❌ $AIDER_PYTHON is not installed."
  echo "   Install Python 3.12, then rerun this script."
  exit 1
fi

if ! python3 -m pip --version >/dev/null 2>&1; then
  echo "❌ pip is not available for python3."
  exit 1
fi

if ! command -v pipx >/dev/null 2>&1; then
  echo "⚙️ pipx not found. Installing pipx..."
  python3 -m pip install --user pipx
  python3 -m pipx ensurepath || true
  export PATH="$HOME/.local/bin:$PATH"
fi

if ! command -v pipx >/dev/null 2>&1; then
  echo "❌ pipx is still not available on PATH."
  echo "   Restart your shell or add: export PATH=\"\$HOME/.local/bin:\$PATH\""
  exit 1
fi

if ! command -v ollama >/dev/null 2>&1; then
  echo "❌ ollama CLI is not installed or not on PATH."
  exit 1
fi

echo "🔍 Checking Ollama..."
if ! curl -fsS "$OLLAMA_API_BASE" >/dev/null; then
  echo "❌ Ollama is not running at $OLLAMA_API_BASE."
  echo "   Start it with: ollama serve"
  exit 1
fi

echo "📦 Ensuring aider-chat is installed with Python 3.12..."
pipx install --force --python "$AIDER_PYTHON" aider-chat

echo "📦 Ensuring models are available..."
if ! ollama list | grep -q "qwen2.5-coder:7b"; then
  echo "⬇️ Pulling qwen2.5-coder:7b..."
  ollama pull qwen2.5-coder:7b
fi

if ! ollama list | grep -q "qwen2.5-coder:14b"; then
  echo "⬇️ Pulling qwen2.5-coder:14b..."
  ollama pull qwen2.5-coder:14b
fi

echo "✅ Models ready"
echo "🚀 Starting Aider..."

cmd=(
  pipx run --python "$AIDER_PYTHON" aider-chat
  --model "$AIDER_MODEL"
  --editor-model "$AIDER_EDITOR_MODEL"
  --architect
  --map-tokens "$MAP_TOKENS"
  --cache-prompts
  --no-stream
)

if (($# > 0)); then
  cmd+=("$@")
fi

"${cmd[@]}"
