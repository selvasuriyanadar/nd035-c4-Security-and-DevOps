#!/bin/sh

root_data_dir=/usr/local/tomcat/data

docker build -t mytomcat:jdk17 tomcat

docker run \
       	-dit \
       	--name myTomcatServer \
       	--env ROOT_DATA_DIR=$root_data_dir \
       	--volume backend-data:$root_data_dir \
       	--publish 8888:8080 \
       	mytomcat:jdk17
