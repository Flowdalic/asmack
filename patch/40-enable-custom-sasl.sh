#!/bin/bash

find . -name '*.java' -exec sed -i '' 's:import org.apache.harmony.javax.security.sasl.Sasl;:import de.measite.smack.Sasl;:g' '{}' ';'

