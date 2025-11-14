#!/usr/bin/env bash

declare LOCATION

LOCATION=$(dirname $0)

$LOCATION/runclass.sh net.shibboleth.idp.plugin.metadatagen.impl.MetadataGenCLI --ansi --home "$LOCATION/.." "$@"
