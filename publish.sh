#!/bin/sh
server_ip="192.168.5.15"
server_application="/dashu/application/rawdatacentral"
server_log_path="/dashu/log/rawdatacentral"

gradle clean build installApp -x test --refresh-dependencies -Denv=dev
cd target/;
file=`ls -rt *.tar.gz|tail -1`
if [ ! -e "$file" ]
then
	echo "\033[31mnot found any *.tar.gz\033[0m"
    exit
fi

ssh root@$server_ip << EOF
    if [ ! -d "$server_application" ] ; then mkdir -p $server_application ;fi
EOF

echo "find app file $file and upload"
scp  $file root@$server_ip:$server_application/;

ssh root@$server_ip << EOF
    cd $server_application;
    rm -rf rawdatacentral
    tar -xzf $file;
    sleep 1;
    cd rawdatacentral
    sh bin/app.sh start;
    echo $file;
    tail -100f $server_log_path/sys.log
    exit;
EOF

