#!/usr/bin/env bash

./local_runner/codeball2018 \
    --p1 tcp-31002 \
    --p2 tcp-31003 \
    --p1-name 31002 \
    --p2-name 31003 \
    --results-file r.txt \
    --seed 12323 \
    --noshow \
    &
echo "LR started"
sleep 3
./release/m4 127.0.0.1 31002 0000000000000000 &
./release/m4 127.0.0.1 31003 0000000000000000
