#!/bin/bash

echo "preparing key file... $MAVEN_SIGN_KEY_FILE"
cat $MAVEN_SIGN_SECRET | base64 -D -o $MAVEN_SIGN_KEY_FILE

