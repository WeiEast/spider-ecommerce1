#!/bin/sh
host="192.168.5.25:6789"


url="http://$host/plugin/uploadPlugin?sassEnv=dev"
gradle clean install -x test
list=`find rawdatacentral-plugin-common/build/libs/rawdatacentral-plugin-common.jar -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "file=@$file" $url
    curl  -F "file=@$file;fileName=rawdatacentral-plugin-common-$HOSTNAME.jar" $url
done