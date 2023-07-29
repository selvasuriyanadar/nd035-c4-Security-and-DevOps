# syntax=docker/dockerfile:1

FROM tomcat:jdk17

# copying manager application inside webapps.dist into webapps to deploy it to the tomcat server
RUN cp -r /usr/local/tomcat/webapps.dist/manager /usr/local/tomcat/webapps

# adding admin user to the tomcat server
COPY conf/tomcat-users.xml /usr/local/tomcat/conf

# editing out the valve declaration in the manager application to enable access to it
COPY webapps/manager/META-INF/context.xml /usr/local/tomcat/webapps/manager/META-INF
