APP_NAME='spider-ecommerce-web'
echo "预设项目名称:$APP_NAME"
JAR_NAME=`ls $APP_NAME*.jar | tail -1`
APP_HOME=`pwd`
if test -z "$APP_NAME"
then
	echo "没有找到任何启动包(jar),搜索路径:$APP_HOME "
	echo "stop "
	exit;
fi
echo "找到启动文件:$JAR_NAME"
PID=`jps -l | grep $APP_NAME | awk '{print $1}'`
OLD_JAR_NAME=`jps -l | grep $APP_NAME | awk '{print $2}'`


. jvm.conf
. service.conf


function restartApp(){
	if test -n "$PID" ;then
		echo -e "\033[31m发现服务已经存在,正在停止服务$OLD_JAR_NAME($PID) \033[0m"
		kill $PID
		NEWPID=`jps -l | grep $APP_NAME | awk '{print $1}'`
		while test "$PID" == "$NEWPID" 
		do
			echo "正在停止服务$OLD_JAR_NAME($PID),等待30ms"
			sleep 0.03
			NEWPID=`jps -l | grep $APP_NAME | awk '{print $1}'`
		done
		echo "服务$OLD_JAR_NAME($PID)停止完成"
	fi;
	if test ! -d "logs" ;then mkdir logs ; fi
	echo jvm_opt=$jvm_opt
	echo jar=$JAR_NAME
	nohup java $jvm_opt -jar $JAR_NAME > /dev/null 2>logs/log &
	echo "正在启动服务$JAR_NAME"
	echo "jps -l | grep $APP_NAME | awk '{print \$1}'"
	PID=`jps -l | grep $APP_NAME | awk '{print $1}'`
	echo -e "\033[31m服务启动完成:$JAR_NAME($PID) \033[0m"
	exit;
}

function stopApp(){
	if test -n "$PID" ;then
		echo -e "\033[31mfind app is runing PID=$PID,APP_NAME=$APP_NAME ,kill $PID\033[0m"
		kill $PID
	else
		echo -e "\033[31mnot find app is runing PID=$PID,APP_NAME=$APP_NAME \033[0m"
	fi;
	exit;
}


case "$1" in
start)
	restartApp
;;
stop)
	stopApp
;;
restart)
	restartApp
;;
*)
	echo "Usage: $0 {start|stop|restart}"
;;
esac
exit 0

