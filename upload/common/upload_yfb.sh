#!/bin/sh
host="rawdatecentral.yfb.saas.treefinance.com.cn"


url="http://$host/plugin/uploadPlugin"
gradle clean install -x test
list=`find rawdatacentral-plugin-common/build/libs/rawdatacentral-plugin-common.jar -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "file=@$file" $url
    curl  -F "file=@$file;fileName=rawdatacentral-plugin-common-$HOSTNAME.jar" $url
done



url="http://$host/plugin/uploadPlugin?sassEnv=product"
gradle clean install -x test
list=`find rawdatacentral-plugin-common/build/libs/rawdatacentral-plugin-common.jar -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "file=@$file" $url
    curl  -F "file=@$file;fileName=rawdatacentral-plugin-common-$HOSTNAME.jar" $url
done

