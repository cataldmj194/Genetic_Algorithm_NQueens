#!/bin/bash
echo compiling...
javac ~/421/program002/*.java

for run in {1..25}
do
  java GANQueens
  sleep 1
done
