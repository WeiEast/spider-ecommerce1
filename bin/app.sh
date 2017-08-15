


function restartApp(){
	if test -n "$PID" ;then
		echo -e "\033[31mfind app is runing PID=$PID,APP_NAME=$APP_NAME ,kill $PID\033[0m"
		kill $PID
		NEWPID=`jps -l | grep $APP_NAME | awk '{print $1}'`
		while test "$PID" == "$NEWPID" 
		do
			echo "wait kill $PID,sleep 30ms"
			sleep 0.03
			NEWPID=`jps -l | grep $APP_NAME | awk '{print $1}'`
		done
		echo "wait kill $PID success"
	fi;
	if test ! -d "logs" ;then mkdir logs ; fi
	nohup java $jvm_opt -jar $APP_NAME > /dev/null 2>logs/log &
	echo "nohup java $jvm_opt -jar $APP_NAME > /dev/null 2>logs/log &"
	echo "jps -l | grep $APP_NAME | awk '{print \$1}'"
	PID=`jps -l | grep $APP_NAME | awk '{print $1}'`
	echo -e "\033[31mstart app PID=$PID \033[0m"
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


APP_NAME=`ls *.jar | tail -1`
APP_HOME=`pwd`
if test -z "$APP_NAME" 
then
	echo "not found any jar in $APP_HOME "
	echo "stop "
	exit;
fi
echo "APP_HOME=$APP_HOME"
echo "APP_NAME=$APP_NAME"
PID=`jps -l | grep $APP_NAME | awk '{print $1}'`

. bin/jvm.conf
. bin/service.conf





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

