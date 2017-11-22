# Neo4j Bosh release

## Testing with Bosh Lite

```shell
$> bosh create-release --force &&
$> bosh upload-release &&
$> bosh -e vbox -d neo4j deploy ../manifests/neo4j-bosh-lite.yml -n
```