#!/bin/sh

gradle clean build installApp -x test
cd target/;
for file in $(ls -rt *.tar.gz|tail -1);
do
    scp -p $file root@116.62.242.180:/dashu/application/;
    echo $file
done


ssh root@116.62.242.180 << EOF
    cd /dashu/application/;
    tar -xzf $file;
    sleep 1;
    sh restart.sh;
    echo $file;
    exit;
EOF

