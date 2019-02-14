#!/bin/bash

echo "preparing key file... $MAVEN_SIGN_KEY_FILE"
echo $MAVEN_SIGN_SECRET > test.txt
base64 -d test.txt > $MAVEN_SIGN_KEY_FILE
ls test.txt
ls $MAVEN_SIGN_KEY_FILE
diff test.txt $MAVEN_SIGN_KEY_FILE

