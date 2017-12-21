#!/bin/sh
host="192.168.5.25:6789"


url="http://$host/plugin/uploadPlugin?sassEnv=dev"
gradle clean install -x test
list=`find rawdatacentral-plugin-education/build/libs/rawdatacentral-plugin-education.jar -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "file=@$file" $url
    curl  -F "file=@$file;fileName=rawdatacentral-plugin-education-$HOSTNAME.jar" $url
done

list=`find rawdatacentral-plugin-education/src/main/resources -name '*.js'`
for file in $list
do
   parent_name=`dirname $file`
   file_name=${parent_name##*/}'.'${file##*/}
   curl  -F "file=@$file;fileName=$file_name" $url
done

