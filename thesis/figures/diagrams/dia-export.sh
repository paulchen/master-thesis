#!/bin/bash

FACTOR=4
#FILES="turtlestore.dia"
FILES="*.dia"

for file in $FILES; do
	dia --export blubb.png $file > /dev/null 2>&1
	HEIGHT=`identify blubb.png |sed -e "s/^[^x]*x//g;s/ .*//g"`
	rm blubb.png
	NEW_HEIGHT=$((HEIGHT*FACTOR))
	dia --export ${file/dia/png} -s x$NEW_HEIGHT $file
done

