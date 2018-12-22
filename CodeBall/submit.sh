#!/usr/bin/env bash

rm -rf submit.zip
files=$(ls *.{h,cpp})
zip -r submit.zip $files