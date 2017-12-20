#!/bin/sh
sh install.sh
app_name="rawdatacentral-main"
server_ip="192.168.5.25"
server_application="/dashu/application/rawdatacentral"
server_log_path="/dashu/log/rawdatacentral"


cd $app_name/build/libs;
file=`ls -rt *.jar | grep -v 'source' |tail -1`
if [ ! -e "$file" ]
then
	echo "\033[31mnot found any *.tar.gz\033[0m"
    exit
fi
echo $file

ssh root@$server_ip << EOF
    if [ ! -d "$server_application" ] ; then mkdir -p $server_application ;fi
EOF


echo "find app file $file and upload"
scp  $file root@$server_ip:$server_application/;
scp ../../../bin/* root@$server_ip:$server_application/;

ssh root@$server_ip << EOF
    cd $server_application
    ./app.sh start;
    echo $file;
#    tail -100f $server_log_path/rawdatacentral.log
    exit;
EOF

