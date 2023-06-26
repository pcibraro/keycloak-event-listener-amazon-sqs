# keycloak-event-listener-amazon-sqs

A Keycloak SPI that publishes events to a queue in Amazon SQS.

# Build

## Build on your local machine

```
mvn clean install
```

# Deployment with local setup

* Copy target/event-listener-aws-sqs-jar-with-dependencies.jar to {KEYCLOAK_HOME}/standalone/deployments
* Edit standalone.xml to configure the provider settings. Find the following
  section in the configuration:

```
<subsystem xmlns="urn:jboss:domain:keycloak-server:1.1">
    <web-context>auth</web-context>
```

And add below:

```
<spi name="eventsListener">
    <provider name="sqs" enabled="true">
        <properties>
            <property name="queueUrl" value=""/>
            <property name="accessKey" value=""/>
            <property name="secretKey" value=""/>
            <property name="region" value=""/>
        </properties>
    </provider>
</spi>
```
All those settings come from the credentials configured in AWS to connect to the queue.

* Restart the keycloak server.

# Deployment with Docker

* Copy target/event-listener-aws-sqs-jar-with-dependencies.jar to /opt/keycloak/providers. If you are using Docker Compose, it would be equivalent to use this line,

```
volumes:
- ./keycloak_data/event-listener-aws-sqs-jar-with-dependencies.jar:/opt/keycloak/providers/event-listener-aws-sqs-jar-with-dependencies.jar
```

That line assumes you previously copied the jar from target into a /keycloak_data directory in the same location as the Docker Compose file.

* Inject the following properties as environment variables into Docker

```
- KC_SPI_EVENTS_LISTENER_SQS_QUEUE_URL=
- KC_SPI_EVENTS_LISTENER_SQS_ACCESS_KEY=
- KC_SPI_EVENTS_LISTENER_SQS_SECRET_KEY=
- KC_SPI_EVENTS_LISTENER_SQS_REGION=
```

# Use
Add/Update a user, your listener should be called, looks at the keycloak syslog for debug.
