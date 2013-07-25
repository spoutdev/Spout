@echo off

java -server -XX:+UseG1GC -jar spout*.jar -platform CLIENT --debug --rendermode GL30