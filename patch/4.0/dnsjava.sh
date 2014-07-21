#!/bin/bash

echo $PWD
cp -r ../../src/smack/smack-resolver-dnsjava/src/main/java/ .
rm -rf org/jivesoftware/smack/util/dns/minidns
