#!/bin/sh

startFunc(){
  
  if [ -e "$PIDFILE" ]; then
    pid=`cat $PIDFILE`
    if [ "$pid" != "" ]; then
      echo "$package is running stop it first!"
      exit 1
    fi
  fi
    echo "Starting $package with jvm opt : $jvm_opt with args : $args  "
    nohup java $jvm_opt -jar $package $args > logs/start.out 2>&1 &
    pid=$!
    echo "$pid"
    echo $pid > $PIDFILE
    sleep 10
  
  if [ ! -e "/dashu/log/rawdata.log" ]; then
    echo "can't find log file please check logs/start.out"
    echo "start failed"
    exit 1
  fi

  error=`cat /dashu/log/rawdata.log | grep Exception`

  if [ "$error" != "" ]; then
    echo "some excetion found in the log debug.log, please check the log file"
    echo "$error" | sed -n 9p
    exit 1
  fi

    if [ "$pid" != "" ]; then
      echo "start $package success"
    exit 0
    else
      echo "start $package failed, please contact dev"
    exit 1
    fi
}

stopFunc(){
    echo "Stopping $package "
    pid=`cat $PIDFILE`
  echo "killing $pid"
    kill -9 $pid
    > $PIDFILE
}


package=`ls *.jar`
PIDFILE=pid

. bin/service.conf
. bin/jvm.conf


case "$1" in
  start)
    startFunc
      ;;
  stop)
    stopFunc
      ;;
  restart)
    if [ ! -e "$PIDFILE" ]; then
  startFunc     
        exit 0
    fi
    pid=`cat $PIDFILE`
    if [ "$pid" != "" ]; then
       stopFunc
       startFunc
    else
       startFunc
    fi
      ;;
  *)
    echo "Usage: $0 {start|stop|restart}"
  esac
  exit 0
