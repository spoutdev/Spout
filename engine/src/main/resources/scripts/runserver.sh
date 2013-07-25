#/bin/sh

java -server -XX:+UseG1GC  -jar spout.*.jar --debug
