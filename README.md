# JBoss/WildFly JMS Security

Propagate Security Context through JMS in JBoss EAP/WildFly.

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

* Usage

```java
@Stateless
public class QueueSender {

    @Inject
    private JMSContext jmsContext;

    @Resource(mappedName = "java:/jms/Queue")
    private Queue destination;

    public void sendToQueue(final MyObject myObject) {
        final SecureMessage message = new SecureMessage(myObject);
        jmsContext.createProducer().send(destination, message);
    }
}
```

```java
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType",
            propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destinationLookup",
            propertyValue = "java:/jms/Queue")
})
@Interceptors(JmsSecurityInterceptor.class)
public class QueueConsumer implements MessageListener {

    @EJB
    private SecuredEJB securedEJB;

    @Override
    public void onMessage(Message message) {
        try {
            final SecureMessage secureMessage = message.getBody(SecureMessage.class);
            final MyObject myObject = secureMessage.getContent(MyObject.class);
            securedEJB.process(myObject);
        } catch (JMSException ex) {
            ex.printStackTrace();
        }
    }

}
```

```java
@Stateless
@RolesAllowed("admin")
public class SecuredEJB {

    @Resource
    private EJBContext context;

    public void process(MyObject myObject) {
        System.out.println("Name: " + context.getCallerPrincipal().getName());
    }
}
```