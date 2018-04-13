# Open Switch Simulator project

This project simulates **SNMP agent** for sending traps and managing snmpwalk requests. It could be deployed using Docker. There are following components:

- Switch Simulator UI (web client)
- Mock SNMP agent (Spring Boot)

Docker image is published here:

[Docker Hub](https://cloud.docker.com/swarm/skochetkov/repository/docker/skochetkov/switch-sim-spring-boot-docker/general)

You can run it:
```console
$ docker run -e OPENNMSSERVER_ADDRESS='123.123.123.123' -p 8080:8080  -t skochetkov/switch-sim-spring-boot-docker
```

Just replace OPENNMSSERVER_ADDRESS argument value with your openNMS server host ip

## Accessing

- To access CONSOLE (UI for SpringBoot GateWay) for switch simulator: `http://123.123.123.123:8080/index.html`

- SpringBoot GateWay for managing switch sim requests: `http://123.123.123.123:8080/switchcontroller/?status=1&port=1`

*Notes:*
status: 1 is up, 2 is down
