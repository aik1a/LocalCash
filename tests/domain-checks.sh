#!/usr/bin/env bash
set -euo pipefail
task_classes=$(mktemp -d)
trap 'rm -rf "$task_classes"' EXIT
javac -encoding UTF-8 -d "$task_classes" app/src/main/java/com/aik1a/localcash/domain/*.java tests/DomainChecks.java tests/PurchaseDomainChecks.java tests/SharedDomainChecks.java
java -cp "$task_classes" DomainChecks

java -cp "$task_classes" PurchaseDomainChecks
java -cp "$task_classes" SharedDomainChecks

