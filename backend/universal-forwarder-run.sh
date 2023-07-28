#!/bin/sh

docker run \
        -d \
        -p 9997:9997 \
        -e "SPLUNK_START_ARGS=--accept-license" \
        -e "SPLUNK_PASSWORD=admin@123" \
        --volume backend-data:/opt/splunkforwarder/data/ \
        --name uf \
        splunk/universalforwarder:latest
