# Neo4j on Cloud Foundry

The goal is provide Neo4j on demand on Cloud Foundry.

## File tree

### Manifests

This contains example manifest to be used for testing:

 - `neo4j-bosh-lite.yml`: test manifest of the Neo4j Bosh release, using Bosh Lite
 - `neo4j-on-demand-broker.yml`: manifest that deploys the generic on-demand service broker and our Neo4j service broker adapter to a VM. The VM will be able to provide Neo4j clusters on demand. This cannot be reused direcly as it relies on Pivotal London infrastructure. This can be used however as a starting point for a test release used in Neo4j CI.

### Neo4j Cloud Utils

`neo4j-cloud-utils` is a Neo4j admin utility that sets a password regardless of the user's previous one. This behavior has been removed 1+ year ago from Neo4j admin but is required to work with Bosh. 

### On-Demand Service Broker (ODB) Adapter

The ODB Adapter is the service providing what is necessary to provision and manage a Neo4j cluster.
See [here](http://docs.pivotal.io/svc-sdk/odb/0-17/creating.html) for further information.

### Neo4j Bosh Release

`neo4j-release` defines the [Bosh release](https://docs.pivotal.io/tiledev/1-12/bosh-release.html) for Neo4j Enterprise Edition.

### Neo4j ODB Adapter Bosh Release

`neo4j-adapter-release` is the Bosh release describing how to provision a VM that in turn is able to provision Neo4j clusters on demand. This co-locates the generic service broker and our own Neo4j adapter.