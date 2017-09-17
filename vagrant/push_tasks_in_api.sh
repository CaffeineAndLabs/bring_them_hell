#!/bin/sh
# Push in cassandra a new monkey task with nodes created with 
# manage_testing_nodes.sh --start

NODE_0_JSON='{ "hostname": "10.42.42.42", "username": "root"}'
NODE_1_JSON='{ "hostname": "10.42.42.43", "username": "root"}'
NODE_2_JSON='{ "hostname": "10.42.42.44", "username": "root"}'
NODES_JSON="${NODE_0_JSON}, ${NODE_1_JSON}, ${NODE_2_JSON}"

curl 127.0.0.1:3000/api/monkey/task/new -X POST -H "Content-Type: application/json" \
	-d "{ \
		\"name\": \"Task genereted by ${0} \", \
		\"nodes\": [ ${NODES_JSON}], \
		\"type\": \"reboot\" \
	}"
