#!/usr/bin/env bash

rm -rf submit.zip
files=$(ls *.{h,cpp})
zip -r submit.zip $files

sum_lines=0
for f in $files ; do
    if [[ "$f" =~ nlohmann|RemoteProcessClient ]] ; then
        continue
    fi

    l=$(wc -l $f | awk '{print $1}')
    echo $(wc -l $f)
    sum_lines=$(($sum_lines + $l))
done

echo "Total number of lines: $sum_lines"