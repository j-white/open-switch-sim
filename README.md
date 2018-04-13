# open-switch-sim

Docker image is published here:

https://cloud.docker.com/swarm/skochetkov/repository/docker/skochetkov/switch-sim-spring-boot-docker/general

You can  just run it:

docker run -e OPENNMSSERVER_ADDRESS='123.123.123.123' -p 8080:8080  -t skochetkov/switch-sim-spring-boot-docker

Just replace OPENNMSSERVER_ADDRESS argument with your openNMS server host ip

To access CONSOLE (UI for SpringBoot GateWay) for switch simulator:
http://123.123.123.123:8080/index.html

SpringBoot GateWay for managing switch sim requests:
http://123.123.123.123:8080/switchcontroller/?status=1&port=1
