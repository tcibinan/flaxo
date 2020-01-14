#!/bin/bash

set -eu

if [[ "$POSTGRES_DBS" ]]; then
	for DB in $(echo $POSTGRES_DBS | tr ',' ' '); do
    psql -U "$POSTGRES_USER" <<-EOSQL
      CREATE DATABASE $DB;
EOSQL
	done
fi
