#!/bin/bash

echo "preparing key file $NEXUS_KEY_FILE"
echo $NEXUS_KEY_BASE64 | base64 -d > $NEXUS_KEY_FILE

