#!/bin/bash

find org/jivesoftware -name '*.java' -exec sed -i '' 's:import org.xmlpull.mxp1.MXParser:import org.xmlpull.v1.XmlPullParserFactory:' '{}' ';'
find org/jivesoftware -name '*.java' -exec sed -i '' 's:new MXParser():XmlPullParserFactory.newInstance().newPullParser():g' '{}' ';'

