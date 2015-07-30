#!/bin/bash

dir=`dirname $1`
base=`basename $1`

cd $dir

git add $dir
git commit -m "$3 updated $base"
git push origin master
