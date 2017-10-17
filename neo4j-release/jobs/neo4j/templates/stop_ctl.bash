#!/bin/bash

. /var/vcap/jobs/neo4j/bin/common.bash

set -x
exec /var/vcap/packages/neo4j/bin/neo4j stop >> $NEO4J_LOGS/stop_stdout.log 2>> $NEO4J_LOGS/stop_stderr.log
