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

if [ ! -d "backup" ]; then
	echo "can't not find bakup !"
	exit 1
fi

backup_package=`ls -t ./backup | grep -e ^rawdata_v | sed -n 1p`

if [ "$backup_package" = "" ]; then
	echo "can't find backup package in backup"
	exit 1
fi

ps_count=`ps ux | grep rawdata-main.jar | grep -v 'grep' | wc -l`
 
 if [ "$ps_count" != "0" ]; then
     echo "rawdata thread is runing, please stop it !"
     exit 1
 fi


echo "roll back backup package : $backup_package"

echo starting roll back

echo clean the last version

#rm -rf $install_dir/$app_name
rm -rf  $install_dir/$app_name/bin
rm -rf  $install_dir/$app_name/lib
rm -rf  $install_dir/$app_name/var
#rm -rf  $install_dir/$app_name/plugin
rm -rf  $install_dir/$app_name/conf
rm -f   $install_dir/$app_name/rawdata-main.jar
rm -f   $install_dir/$app_name/PROG_VERSION.def

cp -r backup/$backup_package/* $install_dir/$app_name/

rm -rf backup

echo roll back application completed

chmod +x $install_dir/$app_name/bin/*.sh

echo "$app_name roll back success to ${install_dir} ..."
