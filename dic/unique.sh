#!/bin/zsh

#cat ./neologd-common-noun-ortho-variant-dict-seed.20150323.csv | awk -F, '!a[$11]++' > output.csv
cat ./mecab-user-dict-seed.20160616.csv | awk -F, '!a[$11]++' > output1.csv
