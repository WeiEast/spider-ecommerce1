#!/bin/sh
url="http://192.168.5.25:6789/redis/uploadFile"
#gradle clean install -x test
list=`find rawdatacentral-plugin-operator/src/main/resources -name '*.js'`
for file in $list
do
   parent_name=`dirname $file`
   file_name=${parent_name##*/}'.'${file##*/}
   curl  -F "cache_file=@$file;fileName=$file_name" $url
   echo \n
done


