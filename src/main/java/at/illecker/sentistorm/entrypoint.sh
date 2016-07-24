#!/usr/bin/env bash
set -e

/usr/share/zookeeper/bin/zkServer.sh start /usr/share/zookeeper/conf/zoo.cfg
/usr/bin/supervisord -c /etc/supervisor/conf.d/supervisord.conf