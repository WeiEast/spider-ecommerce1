#!/bin/sh
host="spider.yfb.saas.treefinance.com.cn"

url="http://$host/plugin/uploadPlugin"
gradle clean install -x test
list=`find spider-bank-plugin/build/libs/spider-bank-plugin.jar -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "file=@$file" $url
    curl  -F "file=@$file;fileName=spider-bank-plugin-$HOSTNAME.jar;sassEnv=product" $url
done

list=`find spider-bank-plugin/src/main/resources -name '*.js'`
for file in $list
do
   parent_name=`dirname $file`
   file_name=${parent_name##*/}'.'${file##*/}
   curl  -F "file=@$file;fileName=$file_name;sassEnv=product" $url
done