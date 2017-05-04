#!/bin/sh
SHUTDOWN_WAIT=40
#pid=`jps|grep -v Jps|awk '{print $1}'`

APP_HOME=/dashu/application/rawdatacentral
JAVA_APP=`cd $APP_HOME;ls *.jar`
echo $JAVA_APP
pid=`$JAVA_HOME/bin/jps -l |grep $JAVA_APP |awk '{print $1}'`
echo $pid
echo "启动脚本本地上传"
if [-n "$pid"]
then
        echo -e "\e[00;31mStoping java\e[00m"
        kill $pid
        let kwait=$SHUTDOWN_WAIT
        count=0;
        until  [ `ps -p $pid | grep -c $pid` = '0' ] || [ $count -gt $kwait ]
        do
                echo -n -e "\n\e[00;31mwaiting for processes to exit\e[00m";
                sleep 1
                let count=$count+1;
        done
        if [ $count -gt $kwait ]; then
                echo -n -e "\n\e[00;31mkilling -9 processes which didn't stop after $SHUTDOWN_WAIT seconds\e[00m"
                kill -9 $pid
        fi
fi
echo -n -e "\n\e[00;31mSTART PROCESS\e[00m\n";


JAVA_OPTS="-Xmx256m -Xms256m -XX:PermSize=64m -XX:MaxPermSize=128m -XX:+HeapDumpOnOutOfMemoryError"
#JAVA_CMD="nohup $JAVA_HOME/bin/java $JAVA_OPTS_MEM $JAVA_OPTS_GC $JAVA_OPTS_ERROR -jar $JAVA_APP >/dev/null 2>&1  &"
echo "JAVA_OPTS:" $JAVA_OPTS
cd $APP_HOME
nohup $JAVA_HOME/bin/java $JAVA_OPTS -jar $JAVA_APP >$APP_HOME/logs/start.out 2>&1 &
pid=`$JAVA_HOME/bin/jps -l |grep $JAVA_APP |awk '{print $1}'`
if [ -n "$pid" ]
then
        echo $pid
        echo -e "\e[00;31mStart java success\e[00m"
else
        echo -e "\e[00;31mStart java failed\e[00m"
fi