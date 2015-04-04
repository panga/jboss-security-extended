package io.github.panga.jboss.jms.security;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import javax.jms.ObjectMessage;

class ObjectMessageProxy implements InvocationHandler {

    private final ObjectMessage message;

    public ObjectMessageProxy(final ObjectMessage message) {
        this.message = message;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getObject".equals(method.getName())) {
            final SecureObjectMessage secureMessage = (SecureObjectMessage) ((ObjectMessage) message).getObject();
            return secureMessage.getBody(Serializable.class);
        } else if ("getBody".equals(method.getName())) {
            final SecureObjectMessage secureMessage = (SecureObjectMessage) message.getObject();
            return secureMessage.getBody((Class) args[0]);
        }

        return method.invoke(message, args);
    }

}
