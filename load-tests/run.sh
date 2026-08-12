#!/usr/bin/env bash
set -euo pipefail

readonly DEFAULT_BASE_URL='http://140.245.70.133'
readonly SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

profile="${1:-load}"
base_url="${BASE_URL:-$DEFAULT_BASE_URL}"

if (( $# > 1 )); then
  echo "Usage: $0 [smoke|load|stress]" >&2
  exit 2
fi

case "$profile" in
  smoke|load|stress) ;;
  *)
    echo "Invalid profile: $profile (allowed: smoke, load, stress)" >&2
    exit 2
    ;;
esac

if ! command -v k6 >/dev/null 2>&1; then
  echo 'k6 is required. Install k6 and make sure it is available on PATH.' >&2
  exit 127
fi

if [[ ! "$base_url" =~ ^(http|https)://([^/?#:]+)(:([0-9]+))?/?$ ]]; then
  echo 'Invalid BASE_URL. Use only http(s)://allowed-host[:port] with no path, query, fragment, or credentials.' >&2
  exit 2
fi

scheme="${BASH_REMATCH[1]}"
host="${BASH_REMATCH[2]}"
port="${BASH_REMATCH[4]:-}"

case "$host" in
  140.245.70.133|localhost|127.0.0.1) ;;
  *)
    echo "Disallowed BASE_URL host: $host" >&2
    exit 2
    ;;
esac

if [[ -n "$port" ]] && { (( ${#port} > 5 )) || (( 10#$port < 1 || 10#$port > 65535 )); }; then
  echo "Invalid BASE_URL port: $port" >&2
  exit 2
fi

base_url="${scheme}://${host}${port:+:$port}"

echo "Profile : $profile"
echo "Base URL: $base_url"
echo 'Requests: public community GET endpoints only'
echo 'Thresholds: failed < 1%, p95 < 1000ms, checks > 99%'

exec k6 run \
  --env "PROFILE=$profile" \
  --env "BASE_URL=$base_url" \
  "$SCRIPT_DIR/k6/community-read.js"
