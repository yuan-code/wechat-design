#!/bin/bash
cd /usr/local/src/wechat-design
mvn clean install -DskipTests
var pid = `ps -ef | grep wechat-design | grep -v grep | awk {'print $2'}`
kill -9 $pid
echo 'kill pid=$pid'
nohup java -jar -Dspring.profiles.active=www /usr/local/src/wechat-design/target/wechat-design-0.0.1-SNAPSHOT.jar
echo 'start success'