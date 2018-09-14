#!/bin/sh
host="spider.yfb.saas.treefinance.com.cn"

url="http://$host/plugin/uploadPlugin"
gradle clean install -x test
list=`find spider-extra-plugin/build/libs/spider-extra-plugin.jar -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "file=@$file" $url
    curl  -F "file=@$file;fileName=spider-extra-plugin-$HOSTNAME.jar" $url
done

list=`find spider-extra-plugin/src/main/resources -name '*.js'`
for file in $list
do
   parent_name=`dirname $file`
   file_name=${parent_name##*/}'.'${file##*/}
   curl  -F "file=@$file;fileName=$file_name" $url
done