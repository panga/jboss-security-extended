package io.github.panga.jboss.jms.security;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import javax.jms.Message;
import javax.jms.ObjectMessage;

class MessageBodyProxy implements InvocationHandler {

    private final Message message;

    public MessageBodyProxy(final Message message) {
        this.message = message;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getObject".equals(method.getName())) {
            final SecureMessage secureMessage = (SecureMessage) ((ObjectMessage) message).getObject();
            return secureMessage.getContent(Serializable.class);
        } else if ("getBody".equals(method.getName())) {
            final SecureMessage secureMessage = message.getBody(SecureMessage.class);
            return secureMessage.getContent((Class) args[0]);
        }

        return method.invoke(message, args);
    }

}
