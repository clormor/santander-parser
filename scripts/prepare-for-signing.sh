#!/bin/bash

echo "preparing key file... $MAVEN_SIGN_KEY_FILE"
echo $MAVEN_SIGN_SECRET
echo $MAVEN_SIGN_SECRET > test.txt
echo base64 -D test.txt > $MAVEN_SIGN_KEY_FILE

