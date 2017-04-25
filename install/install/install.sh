#!/bin/bash


def=`java -version 2>&1 | grep 1.7`
if [[ $def == "java version"* ]]
then
        echo "Java1.7=====================OK"
else
        echo "Java1.7=====================Fail"
	exit 1
fi

#check dependencies
dependencies="jar python mysql"

for dependency in $dependencies
do
	echo "Check dependency of $dependency"
	if ! which $dependency > /dev/null; then
		echo "$dependency is not installed. please check the release guide and use apt install it and run this scripts again"
		exit 1;
	fi
done

install_dir=/opt/rawdata
app_name=rawdata
install_config=install.properties

#check rabbit mq configure

# echo "checking rabbit mq configure"

# ./rabbit_check.sh $install_config 2>&1
	
# if [ "$?" = "1" ]; then
# 	echo "rabbit mq check faild.."
# 	exit 1
# fi

#check db configure

echo "checking db configure"

./db_check.sh $install_config 2>&1
	
if [ "$?" = "1" ]; then
	echo "db check faild.."
	exit 1
fi

# echo "checking hbase configure"

# ./hbase_check.sh $install_config 2>&1

# if [ "$?" = "1" ]; then
# 	echo "hbase check faild.."
# 	exit 1
# fi

if [ $# = 1 ]; then
	install_dir=$1
	echo "using install dir $install_dir"
else
        echo "using default install dir $install_dir"
fi

if [ ! -d "$install_dir" ]; then
	echo $install_dir does not exists create it...
	mkdir -p $install_dir
fi

if [ ! -w "$install_dir" ]; then
	echo "permission denied, have no permission to modify the directory $install_dir..."
	exit 1
fi

if [ -d "$install_dir/$app_name" ]; then
	echo "$app_name exists in $install_dir, this applcation had been installed before"
	exit 1
fi

echo installing...

echo ${app_name} will be installed into $install_dir

mkdir $install_dir/$app_name
mkdir -p $install_dir/$app_name/var/run

cp -r ../../conf  $install_dir/$app_name/conf
cp -r ../../lib  $install_dir/$app_name/lib
cp -r ../../bin  $install_dir/$app_name/bin
cp -r ../../logs  $install_dir/$app_name/logs
#cp -r ../../plugin  $install_dir/$app_name/plugin
cp -r ../../PROG_VERSION.def $install_dir/$app_name
cp -r ../../*.jar $install_dir/$app_name

echo "start to change config..."

python change_config.py $install_config $install_dir/$app_name/conf 2>&1

if [ "$?" != "0" ]; then
	echo "install failed, some error when change configure" 
	rm -rf $install_dir/$app_name
	exit 1
fi

(
cd $install_dir/$app_name/lib
for item in `ls colander*.jar` 
do
name=`echo $item | sed -e 's/-[0-9]\{8\}\.[0-9]*-[0-9]*.jar/-SNAPSHOT.jar/g'`
mv -v $item $name 2>&1
done
echo "fix dependency name completed"
)

chmod +x $install_dir/$app_name/bin/*.sh

(
cd $install_dir
cmd=`pwd`
cmd="@reboot $cmd/$app_name/bin/service.sh"
crontab -l > /tmp/crontab_tmp
sed -i "/no crontab/d" /tmp/crontab_tmp
flag=`cat /tmp/crontab_tmp | grep "$cmd"`
if [ "$flag" = "" ]; then
	echo "$cmd start" >> /tmp/crontab_tmp
	crontab -i /tmp/crontab_tmp
else
	echo "crontab had been added"
fi
 
)





echo "$app_name installed success to ${install_dir} ..."

echo "please start application manually";
