# open-switch-sim

Docker image is published here: https://cloud.docker.com/swarm/skochetkov/repository/docker/skochetkov/switch-sim-spring-boot-docker/general

You can  just run it:
docker run -e OPENNMSSERVER_ADDRESS='123.456.789.000' -p 8080:8080 -t switch-sim-spring-boot-docker:0.0.1-SNAPSHOT

Just replace OPENNMSSERVER_ADDRESS argument with your openNMS server host ip
