#!/bin/bash

RUN_DIR=/var/vcap/sys/run/neo4j

export JAVA_HOME=/var/vcap/packages/openjdk_1.8.0/jre
export NEO4J_HOME=/var/vcap/packages/neo4j
export NEO4J_PIDFILE=${RUN_DIR}/neo4j.pid
export NEO4J_LOGS=/var/vcap/sys/log/neo4j
export NEO4J_CONF=/var/vcap/jobs/neo4j/conf
