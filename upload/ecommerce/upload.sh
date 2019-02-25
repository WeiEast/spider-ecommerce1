#!/bin/sh
echo "input(env): $1"

host="192.168.5.131:60662"
env=""
if [[ "$1" = "prod" ]] ; then
  host="spider.yfb.saas.treefinance.com.cn"
  env="product"
elif [[ "$1" = "yfb" ]]; then
  host="spider.yfb.saas.treefinance.com.cn"
elif [[ "$1" = "approach" ]]; then
  host="spider.approach.saas.treefinance.com.cn"
elif [[ "$1" = "test" ]]; then
  host="spider.saas.test.treefinance.com.cn"
fi

echo "host: $host"

gradle clean install -x test

url="http://$host/plugin/uploadPlugin?sassEnv=$env"

list=`find spider-ecommerce-plugin/build/libs -name '*.jar' | grep -v 'sources'`

for file in $list
do
    echo "start upload plugin: $file"
    curl  -F "file=@$file;filename=spider-ecommerce-plugin.jar" $url
done