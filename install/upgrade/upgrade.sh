#!/bin/bash -e

install_dir=/opt/Rawdata
app_name=rawdata
change_config=changes.properties

if [ $# = 1 ]; then
	install_dir=$1
	echo "using install dir $install_dir"
else
        echo "using default install dir $install_dir"
fi

if [ ! -d "$install_dir" ]; then
	echo $install_dir does not exists 
	exit 1
fi

if [ ! -w "$install_dir" ]; then
	echo "permission denied, have no permission to modify the directory $install_dir..."
	exit 1
fi

if [ ! -d "$install_dir/$app_name" ]; then
	echo "can't not find $app_name in $install_dir !"
	exit 1
fi

if [ -d backup ]; then
    echo "upgraded,please rollback first !";
    exit 1

fi

ps_count=`ps ux | grep colander-main.jar | grep -v 'grep' | wc -l`
if [ "$ps_count" != "0" ]; then
  echo "colander thread is runing, please stop it !"
  exit 1

fi
 


echo upgrading...
echo back up the last version to backup
mkdir -p backup
back_name=${app_name}_v`date +%Y%m%d%H%M`
#cp -r $install_dir/$app_name ./backup/$back_name
#rm -rf ./backup/$back_name/logs
rsync -avze ssh --exclude=logs --exclude=tmp $install_dir/$app_name/* ./backup/$back_name
echo back up completed

echo clean the last version

rm -rf $install_dir/$app_name/lib
#rm -rf $install_dir/$app_name/bin
#rm -rf $install_dir/$app_name/plugin
rm -rf $install_dir/$app_name/*.jar
rm -rf $install_dir/$app_name/PROG_VERSION.def

echo upgrading application

cp -r ../../lib  $install_dir/$app_name/lib
#cp -r ../../bin  $install_dir/$app_name/bin
#cp -r ../../plugin  $install_dir/$app_name/plugin
#cp -ru ../../conf $install_dir/$app_name

#cp ../../conf/refresh.task.properties $install_dir/$app_name/conf/
#cp ../../conf/application.conf $install_dir/$app_name/conf/

cp -r ../../PROG_VERSION.def $install_dir/$app_name
cp -r ../../*.jar $install_dir/$app_name

(
cd $install_dir/$app_name/lib
for item in `ls colander*.jar` 
do
name=`echo $item | sed -e 's/-[0-9]\{8\}\.[0-9]*-[0-9]*.jar/-SNAPSHOT.jar/g'`
if [ $item != $name ]; then
mv -v $item $name 2>&1
fi
done
echo "fix dependency name completed"
)

echo "upgrade the configure..."
./add_config.sh ../../conf $install_dir/$app_name/conf
python change_config.py $change_config $install_dir/$app_name/conf 2>&1

if [ "$?" != "0" ]; then
	echo "upgrade failed, some error when change configure" 
	rm -rf $install_dir/$app_name
	exit 1
fi

chmod +x $install_dir/$app_name/bin/*.sh

echo "$app_name upgrade success to ${install_dir} ..."
