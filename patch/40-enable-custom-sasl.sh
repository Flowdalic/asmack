#!/usr/bin/env bash

[ "Darwin" = $(uname) ] && SEDI="sed -i ''" || SEDI='sed -i'

find . -name '*.java' -exec $SEDI 's:import org.apache.harmony.javax.security.sasl.Sasl;:import de.measite.smack.Sasl;:g' '{}' ';'

