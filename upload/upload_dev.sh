#!/bin/sh
host="192.168.5.25:6789"


url="http://$host/plugin/uploadPlugin?sassEnv=dev"
gradle clean install -x test
list=`find rawdatacentral-plugin-operator/build/libs/rawdatacentral-plugin-operator.jar -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "file=@$file" $url
    curl  -F "file=@$file;fileName=rawdatacentral-plugin-operator-$HOSTNAME.jar" $url
done

list=`find rawdatacentral-plugin-operator/src/main/resources -name '*.js'`
for file in $list
do
   parent_name=`dirname $file`
   file_name=${parent_name##*/}'.'${file##*/}
   curl  -F "file=@$file;fileName=$file_name" $url
done

list=`find rawdatacentral-plugin-common/build/libs/rawdatacentral-plugin-common.jar -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "file=@$file" $url
    curl  -F "file=@$file;fileName=rawdatacentral-plugin-common-$HOSTNAME.jar" $url
done