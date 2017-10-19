#!/bin/sh
url="http://192.168.5.25:6789/website/uploadPluginJar"
gradle clean install -x test
list=`find rawdatacentral-plugin-operator/build/libs/rawdatacentral-plugin-operator.jar -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "jar=@$file" $url
    curl  -F "jar=@$file;fileName=rawdatacentral-plugin-operator-$HOSTNAME.jar" $url
done


