#!/bin/sh

gradle clean build installApp -x test
rootPath=`pwd`
cd target/;
for file in $(ls -rt *.tar.gz|tail -1);
do
    scp -p $file root@116.62.242.180:/dashu/application/;
    scp -p $rootPath/bin/restart.sh root@116.62.242.180:/dashu/application/;
    echo $file
done


ssh root@116.62.242.180 << EOF
    cd /dashu/application/;
    tar -xzf $file;
    sleep 1;
    sh restart.sh;
    echo $file;
    tail -100f /dashu/log/rawdatacentral.log
    exit;
EOF

