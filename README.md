# JBoss/WildFly JMS Security

Propagate Security Context to JMS bindings in JBoss EAP/WildFly

The helper method ```SecureMessage.fromRequest``` creates the following credentials: PrincipalCredential, ConfidentialityCredential and SubjectCredential based on JAAS and JACC contexts.

* Build

```mvn clean install```

* Maven

```xml
<dependency>
    <groupId>io.github.panga</groupId>
    <artifactId>jboss-jms-security</artifactId>
    <version>1.0.0</version>
</dependency>
```

* Usage (WildFly)

```java
@ApplicationScoped
public class QueueSender {

    @Inject
    private JMSContext jmsContext;

    @Inject
    private HttpServletRequest servletRequest;

    @Resource(mappedName = "java:/jms/Queue")
    private Queue destination;

    public void sendToQueue(final Serializable object) {
        final SecureMessage message = SecureMessage.fromRequest(servletRequest, object);
        jmsContext.createProducer().send(destination, message);
    }
}
```
