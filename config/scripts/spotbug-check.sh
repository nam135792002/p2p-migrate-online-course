
#!/usr/bin/env bash

set -e

ROOT_DIR="$(git rev-parse --show-toplevel)"
cd "$ROOT_DIR"

echo "========================================"
echo " Running SpotBugs for all Maven modules"
echo "========================================"

MERGED_XML="$ROOT_DIR/target/spotbugs-merged.xml"
FINAL_HTML="$ROOT_DIR/target/spotbugs-report.html"

# Remove previous SpotBugs XML reports to avoid stale results
find . -type f \
    -path "*/target/spotbugsXml.xml" \
    -not -path "*/.git/*" \
    -delete

# Compile and run SpotBugs for all Maven modules. Keep processing the reactor
# after a module fails so reports from the remaining modules are still created.
if mvn -B --fail-at-end -DskipTests compile spotbugs:spotbugs; then
    mvn_status=0
else
    mvn_status=$?
    echo "WARNING: Maven exited with status $mvn_status after processing available modules; generating a report from available XML results."
fi

# Merge all module XML reports
python config/spotbugs/merge-spotbugs-reports.py \
    "$ROOT_DIR" \
    "$MERGED_XML"

# Generate one HTML report
python config/spotbugs/generate-spotbugs-report.py \
    "$MERGED_XML" \
    "$FINAL_HTML"

echo "========================================"
echo "SpotBugs Summary"
echo "Merged XML: $MERGED_XML"
echo "HTML Report: $FINAL_HTML"
echo "========================================"

exit "$mvn_status"
