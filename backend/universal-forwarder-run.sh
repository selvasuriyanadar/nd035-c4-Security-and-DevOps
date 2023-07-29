#!/bin/sh

if [[ -z "$1" ]]; then
  echo "splunk host is required."
  exit 1
fi

backend_data_dir=/opt/splunkforwarder/data

docker run \
        -d \
        -p 9997:9997 \

        # accepting the splunk licence, and providing the username and password of splunk via environment variables

        -e "SPLUNK_START_ARGS=--accept-license" \
        -e "SPLUNK_USERNAME=admin" \
        -e "SPLUNK_PASSWORD=admin@123" \

        --volume backend-data:$backend_data_dir \
        --name uf \
        splunk/universalforwarder:latest

echo "please wait for 50 seconds.."
sleep 50

sudo docker exec -it -u splunk uf /opt/splunkforwarder/bin/splunk add monitor $backend_data_dir
sudo docker exec -it -u splunk uf /opt/splunkforwarder/bin/splunk add forward-server $1:9997
