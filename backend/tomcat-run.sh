#!/bin/sh

docker run \
       	-dit \
       	--name myTomcatServer \
       	--env ROOT_DATA_DIR=/usr/local/tomcat/data \
       	--volume backend-data:/usr/local/tomcat/data \
       	--publish 8888:8080 \
       	tomcat:jdk17
