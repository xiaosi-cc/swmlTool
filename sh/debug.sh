#!bin/sh

debugJar=$1
java -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=y -Dloader.path=lib/ -jar $debugJar