
#!/usr/bin/env bash
set -e

mvn -q -pl p2p-common spotbugs:spotbugs

python config/spotbugs/generate-spotbugs-report.py \
    p2p-common/target/spotbugsXml.xml \
    p2p-common/target/spotbugs-report.html

echo "SpotBugs report generated: p2p-common/target/spotbugs-report.html"
