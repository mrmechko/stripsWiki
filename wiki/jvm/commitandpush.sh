#!/bin/bash

dir=`dirname $1`
base=`basename $1`

cd $dir

git -C $dir commit -am "$3 updated $base"
git push origin master
