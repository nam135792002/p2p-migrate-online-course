
#!/usr/bin/env bash

set -e

ROOT_DIR="$(git rev-parse --show-toplevel)"
cd "$ROOT_DIR"

MERGED_XML="$ROOT_DIR/target/checkstyle-merged.xml"
FINAL_HTML="$ROOT_DIR/target/checkstyle-report.html"

echo "========================================"
echo " Running Checkstyle for all Maven modules"
echo "========================================"

# Remove previous results so the report only includes this run.
find . -type f \
    -path "*/target/checkstyle-result.xml" \
    -not -path "*/.git/*" \
    -delete

# Build reactor dependencies before running Checkstyle in each module.
if mvn -B --fail-at-end -DskipTests compile checkstyle:checkstyle; then
    mvn_status=0
else
    mvn_status=$?
    echo "WARNING: Maven Checkstyle exited with status $mvn_status after processing available modules; generating a report from available XML results."
fi

# Merge module XML reports
python config/checkstyles/merge-checkstyle-reports.py \
    "$ROOT_DIR" \
    "$MERGED_XML"

# Generate aggregated HTML report
python config/checkstyles/generate-checkstyle-report.py \
    "$MERGED_XML" \
    "$FINAL_HTML"

echo "========================================"
echo "Checkstyle report generated:"
echo "$FINAL_HTML"
echo "========================================"

exit "$mvn_status"
