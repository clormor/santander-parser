#!/bin/bash

echo "preparing key file... $MAVEN_SIGN_KEY_FILE"
echo $MAVEN_SIGN_SECRET | base64 -d > $MAVEN_SIGN_KEY_FILE

