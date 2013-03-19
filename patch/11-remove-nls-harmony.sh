#!/bin/bash

find org/apache/harmony -name '*.java' -exec sed -i '' 's:import org.apache.harmony.auth.internal.nls.Messages;::' '{}' ';'
find org/apache/harmony -name '*.java' -exec sed -i '' 's:Messages.getString(\("[^"]*"\)):\1:g' '{}' ';'

