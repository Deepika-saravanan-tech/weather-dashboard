#!/bin/sh
set -eu

PORT_VALUE="${PORT:-10000}"

sed -i "s/port=\"8080\" protocol=\"HTTP\/1\.1\"/port=\"${PORT_VALUE}\" protocol=\"HTTP\/1.1\"/" /usr/local/tomcat/conf/server.xml
sed -i 's/port="8005"/port="-1"/' /usr/local/tomcat/conf/server.xml

exec catalina.sh run
