
#!/usr/bin/env bash

ROOT_DIR="$(git rev-parse --show-toplevel)" || exit 1
cd "$ROOT_DIR" || exit 1

mvn -q -pl p2p-common checkstyle:checkstyle
mvn_status=$?

python config/checkstyles/generate-checkstyle-report.py \
    p2p-common/target/checkstyle-result.xml \
    p2p-common/target/checkstyle-report.html

exit $mvn_status
