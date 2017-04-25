#!/bin/sh

mvn clean package -Dmaven.test.skip=true;
cd target/;
for file in $(ls -rt *.tar.gz|tail -1);
do
    scp -p $file root@172.16.2.20:/dashu/application/;
    echo $file
done


ssh root@172.16.2.20 << EOF
    cd /dashu/application/;
    tar -xzf $file;
    sleep 1;
    sh restart.sh;
    echo $file;
    exit;
EOF

