#!/bin/bash
set -e
rm -rf *.zip
./gradlew clean check assemble

EXIT_STATUS=0

exit $EXIT_STATUS