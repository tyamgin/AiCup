#!/usr/bin/env bash

rm local_runner/$1

./local_runner/codeball2018 \
    --p1 tcp-31002 \
    --p2 tcp-31003 \
    --p1-name 31002 \
    --p2-name 31003 \
    --results-file $1 \
    --seed 3443 \
    --duration 100000 \
    --noshow \
    &
echo "LR started"
sleep 3
./cmake-build-release/CodeBall 127.0.0.1 31002 0000000000000000 &
./release/m6 127.0.0.1 31003 0000000000000000

cat local_runner/$1