#!/bin/bash


#need to add permission for this file: chmod u+x build.sh

set -xe

g++ solution.cpp -o app && ./app
