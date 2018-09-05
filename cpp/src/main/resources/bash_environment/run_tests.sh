#!/usr/bin/env bash

project_path=$PWD
src_dir=$project_path/src/main/cpp
output_dir=$project_path/build/cpp
executable=$output_dir/test

echo "Compiling everything under $src_dir"
mkdir -p $output_dir
`cd $src_dir && g++ * -o $executable -std=c++11`

if [ $? != 0 ]
then
    exit 1
fi

echo
echo 'Tests:'

test_groups=$@

failed=false
for test_group in $test_groups
do
    echo
    echo "> $test_group:"
    ./run_test.sh $test_group
    if [ $? != 0 ]
    then
        failed=true
    fi
done

echo
if [ $failed = true ]
then
    echo Summary: FAILURE
    exit 1
else
    echo Summary: SUCCESS
    exit 0
fi
