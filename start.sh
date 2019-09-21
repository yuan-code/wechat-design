#!/bin/bash
dir=`pwd`
cd /usr/local/src/wechat-design
git pull origin master
mvn clean install -DskipTests
pid=`ps -ef | grep wechat-design | grep -v grep | awk {'print $2'}`
kill -9 $pid
echo "kill pid=$pid"
nohup java -jar  -Xms1024m -Xmx1024m -Xss256k -XX:MetaspaceSize=128M -XX:+UseConcMarkSweepGC -XX:+DisableExplicitGC -XX:+PrintGCDetails -Xloggc:/home/logs/wechat-design/gc.log  -Dspring.profiles.active=www -Djava.rmi.server.hostname=62.234.24.11 -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8000 -Dcom.sun.management.jmxremote.authenticate=true -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.pwd.file=/usr/local/jdk/jre/lib/management/jmxremote.password /usr/local/src/wechat-design/target/wechat-design-0.0.1-SNAPSHOT.jar &
echo 'start success'
cd $dir
