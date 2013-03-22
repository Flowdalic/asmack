#!/usr/bin/env bash

[ "Darwin" = $(uname) ] && SEDI="sed -i ''" || SEDI='sed -i'

find org/jivesoftware -name '*.java' -exec $SEDI 's:import org.xmlpull.mxp1.MXParser:import org.xmlpull.v1.XmlPullParserFactory:' '{}' ';'
find org/jivesoftware -name '*.java' -exec $SEDI 's:new MXParser():XmlPullParserFactory.newInstance().newPullParser():g' '{}' ';'

