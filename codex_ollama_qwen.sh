#!/usr/bin/env bash
set -euo pipefail

MODEL="${CODEX_OLLAMA_MODEL:-qwen2.5-coder:14b}"
OLLAMA_HOST="${OLLAMA_HOST:-http://localhost:11434}"

if ! command -v codex >/dev/null 2>&1; then
  echo "error: codex CLI not found" >&2
  exit 1
fi

if ! command -v ollama >/dev/null 2>&1; then
  echo "error: ollama not found" >&2
  exit 1
fi

# Check Ollama server
if ! curl -fsS "$OLLAMA_HOST/api/tags" >/dev/null 2>&1; then
  echo "error: Ollama not running at $OLLAMA_HOST" >&2
  echo "run: ollama serve" >&2
  exit 1
fi

# Warn if model missing
if ! ollama list | awk 'NR>1 {print $1}' | grep -Fxq "$MODEL"; then
  echo "warning: model '$MODEL' not found locally" >&2
  echo "pull it with: ollama pull $MODEL" >&2
fi

# Run Codex
if [ "$#" -eq 0 ]; then
  exec codex --oss --model "$MODEL"
else
  prompt="$*"
  exec codex exec --oss --model "$MODEL" --cd "$PWD" "$prompt"
fi
