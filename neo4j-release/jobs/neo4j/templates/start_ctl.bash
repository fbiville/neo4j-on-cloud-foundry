#!/bin/bash

. /var/vcap/jobs/neo4j/bin/common.bash

mkdir -p $RUN_DIR
mkdir -p $NEO4J_LOGS
mkdir -p $NEO4J_CONF
mkdir -p /var/vcap/store/neo4j/data
chown -R vcap:vcap $NEO4J_LOGS $RUN_DIR /var/vcap/store/neo4j/data

exec /var/vcap/packages/neo4j/bin/neo4j start >> $NEO4J_LOGS/start_stdout.log 2>> $NEO4J_LOGS/start_stderr.log
