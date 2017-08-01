#!/bin/sh
url="http://192.168.5.15:6789/website/uploadPluginJar"
gradle clean install -x test
cd rawdatacentral-plugin-operator/build/libs/
list=`find . -name '*.jar' | grep -v 'sources'`
for file in $list
do
    curl  -F "jar=@$file" $url
    echo  \n
done


