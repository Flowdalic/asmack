#!/usr/bin/env bash

[ "Darwin" = $(uname) ] && SEDI="sed -i ''" || SEDI='sed -i'

mkdir -p org/apache/harmony/
mv javax org/apache/harmony/
find org/apache/harmony/ -name '*.java' -exec $SEDI 's:package javax:package org.apache.harmony.javax:g' '{}' ';'
find . -name '*.java' -exec $SEDI 's:import javax.security.sasl:import org.apache.harmony.javax.security.sasl:g' '{}' ';'
find . -name '*.java' -exec $SEDI 's:import javax.security.auth:import org.apache.harmony.javax.security.auth:g' '{}' ';'

