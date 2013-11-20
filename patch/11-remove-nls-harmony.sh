#!/usr/bin/env bash

[ "Darwin" = $(uname) ] && SEDI="sed -i ''" || SEDI='sed -i'

find org/apache/harmony -name '*.java' -exec $SEDI 's:import org.apache.harmony.auth.internal.nls.Messages;::' '{}' ';'
find org/apache/harmony -name '*.java' -exec $SEDI 's:Messages.getString(\("[^"]*"\)):\1:g' '{}' ';'

