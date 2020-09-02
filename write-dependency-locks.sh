#!/usr/bin/env bash

./gradlew dependencies --project-dir buildSrc --write-locks >/dev/null
mapfile -t projects < <(./gradlew projects --console plain | grep -oP "Project '\K(.+)(?=')")
for project in "${projects[@]}"; do
  ./gradlew "$project:dependencies" --write-locks >/dev/null
done
