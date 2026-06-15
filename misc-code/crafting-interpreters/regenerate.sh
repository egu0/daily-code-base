#!/bin/bash

# WARNING: please make sure the compilation(by `mvn compile`) is ok before run this script

# WARNING: run `chmod +x regenerate.sh` before `./regenerate.sh`

set -xe

mvn compile exec:java -Dexec.mainClass="com.craftinginterpreters.tool.GenerateAst" -Dexec.args="src/main/java/com/craftinginterpreters/lox/" && echo "Done"

