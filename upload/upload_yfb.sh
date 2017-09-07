#!/bin/sh
url="http://rawdatecentral.yfb.saas.treefinance.com.cn/website/uploadPluginJar"
gradle clean install -x test
list=`find rawdatacentral-plugin-operator/build/libs/rawdatacentral-plugin-operator.jar -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "jar=@$file" $url
    curl  -F "jar=@$file;fileName=rawdatacentral-plugin-operator-$HOSTNAME.jar" $url
done


