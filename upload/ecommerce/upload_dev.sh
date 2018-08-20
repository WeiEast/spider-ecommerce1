#!/bin/sh
host="192.168.5.12:6789"

url="http://$host/plugin/uploadPlugin"
gradle clean install -x test
list=`find spider-ecommerce-plugin/build/libs/spider-ecommerce-plugin.jar -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "file=@$file" $url
    curl  -F "file=@$file;fileName=spider-ecommerce-plugin-$HOSTNAME.jar" $url
done

list=`find spider-ecommerce-plugin/src/main/resources -name '*.js'`
for file in $list
do
   parent_name=`dirname $file`
   file_name=${parent_name##*/}'.'${file##*/}
   curl  -F "file=@$file;fileName=$file_name" $url
done