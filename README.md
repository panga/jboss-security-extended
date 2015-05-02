# JBoss Security Extended

Propagate Security Context through JMS and WebSocket endpoint in JBoss/WildFly container.

* Maven

```xml
<dependency>
    <groupId>com.github.panga</groupId>
    <artifactId>jboss-security-extended</artifactId>
    <version>1.0.0</version>
</dependency>
```

* Usage for JMS

```java
@Stateless
public class QueueSender {

    @Inject
    private JMSContext jmsContext;

    @Resource(mappedName = "java:/jms/Queue")
    private Queue destination;

    @Resource
    private EJBContext context;

    public void sendToQueue(final MyObject myObject) {
        final SecureObjectMessage message = new SecureObjectMessage(myObject, context.getCallerPrincipal());
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
            securedEJB.process(message.getBody(MyObject.class));
        } catch (JMSException ex) {
            ex.printStackTrace();
        }
    }

}
```

* Usage for WebSocket (must have a Session parameter)

```java
@Interceptors({WebsocketSecurityInterceptor.class})
@ServerEndpoint(value = "/echo", configurator = WebsocketSecurityConfigurator.class)
public class EchoEndpoint {

    @Inject
    private SecuredEJB securedEJB;

    @OnOpen
    public void open(Session session) {
        securedEJB.process(null);
    }

    @OnMessage
    public void message(String message, Session session) {
        securedEJB.process(null);
    }

    @OnClose
    public void close(Session session) {
        securedEJB.process(null);
    }

}
```

* Secured EJB

```java
@Stateless
@RolesAllowed("admin")
public class SecuredEJB {

    @Resource
    private EJBContext context;

    public void process(MyObject myObject) {
        System.out.println("User: " + context.getCallerPrincipal().getName());
    }
}
```
