#!/bin/bash
set -e
## Add to elk data
# Wait for Elasticsearch to be ready
until curl -s -u "elastic:${ELASTIC_PASSWORD}" -I http://localhost:9200 > /dev/null 2>&1; do
  echo "Waiting for Elasticsearch to start..."
  sleep 5
done

ELASTIC_PASSWORD="myelasticpass"
KIBANA_PASSWORD="kibanapass"

curl -X POST -u "elastic:${ELASTIC_PASSWORD}" -H "Content-Type: application/json" http://localhost:9200/_security/user/kibana_system/_password -d "{ \"password\": \"${KIBANA_PASSWORD}\" }"

echo "Successfully updated password for kibana_system user."
