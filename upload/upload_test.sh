#!/bin/sh
host="rawdatacentral.saas.test.treefinance.com.cn"


url="http://$host/plugin/uploadPlugin"
gradle clean install -x test
list=`find spider-operator-plugin/build/libs/spider-operator-plugin.jar -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "file=@$file" $url
    curl  -F "file=@$file;fileName=spider-operator-plugin-$HOSTNAME.jar" $url
done

list=`find spider-operator-plugin/src/main/resources -name '*.js'`
for file in $list
do
   parent_name=`dirname $file`
   file_name=${parent_name##*/}'.'${file##*/}
   curl  -F "file=@$file;fileName=$file_name" $url
done