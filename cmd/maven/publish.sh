#!/bin/sh

mvn clean package -Dmaven.test.skip=true;
cd target/;
for file in $(ls -rt *.tar.gz|tail -1);
do
    scp -p $file dev-rawdata:/dashu/application/;
    echo $file
done


ssh dev-rawdata << EOF
    cd /dashu/application/;
    tar -xzf $file;
    sleep 1;
    sh restart.sh;
    echo $file;
    exit;
EOF

