**Installing an EC2 instance**

create a EC2 instance, choose medium size and ubuntu os, configure firewall appropriately.
use ssh key to access the EC2 instance.
verify if the instance is listed in the aws EC2 instances menu.

**Installing Splunk Enterprise**

install the splunk enterprise by searching the market place in aws, go with the defaults, use the vendor recommended firewall, choose medium size and an ssh key.
follow the given instructions to login to the splunk web ui of the installed splunk enterprise instance, it is available at 8000 port.

[script paths are relative to the root of the project]
[install the following into the EC2 instace created the first instruction]

**Install Docker**

install docker by following the instructions here https://docs.docker.com/engine/install/ubuntu/
run "sudo docker run hello-world" to verify the docker installation.

**Installing Tomcat**

use backend/tomcat-run.sh to install, run and configure tomcat.
run it as

cd backend \
sudo bash tomcat-run.sh

it will be installed and run in a docker container,
and will be configured to make it ready to accept deployments.
it also configures a docker volume (backend-data) to accept the logs from the application deployed to the tomcat server.
the path to this directory (backend-data) within the tomcat server is available in the environment variable (ROOT\_DATA\_DIR) which can be used to configure the application that is to be deployed to log in that location.
the tomcat server is available at port 8888, navigate to /manager endpoint, use the username and password given in backend/tomcat/conf/tomcat-users.xml to authenticate.

**Installing Universal Forwarder**

use backend/universal-forwarder-run.sh to install, run and configure universal forwarder.
run it as

sudo bash backend/universal-forwarder-run.sh << private ip of the splunk enterprise instance >>

it requires the private ip of the splunk enterprise EC2 instance to start the process.
it monitors the (backend-data) docker volume and sends the events to the provided splunk enterprise instance at 9997 port.
the events will be available in the main index of the provided splunk enterprise instance.
navigate to Apps -> Search & Reporting, and search for 'index="main" source="/opt/splunkforwarder/data/logs/udacity\_ecom.log"' and the application logs of the ecommerce app deployed to tomcat server should be available.

**Installing Jenkins**

use the scripts in the jenkins directory to install and run jenkins.
these scripts are as per the instructions at https://www.jenkins.io/doc/book/installing/docker/
run it as

cd jenkins \
sudo bash jenkins-docker-run.sh \
sudo bash myjenkins-blueocean-run.sh

jenkins will be available at 8080 port.
enter the jenkins container and extract the pass key by running

sudo docker exec -it jenkins-blueocean bash
cat /var/jenkins\_home/secrets/initialAdminPassword

the pass key will be printed to the screen take that and provide to the jenkins ui to authenticate
click install suggested plugins, create the jenkins user
the home page of jenkins will be shown
