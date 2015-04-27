package com.github.panga.jboss.security.jms;

import com.github.panga.jboss.security.SecurityActions;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.Principal;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.security.auth.Subject;

/**
 *
 * @author Leonardo Zanivan
 */
public class JmsSecurityInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        final Method method = ctx.getMethod();
        if ("onMessage".equals(method.getName())
                && method.getParameterCount() == 1
                && method.getParameterTypes()[0] == Message.class) {
            final ObjectMessage message = (ObjectMessage) ctx.getParameters()[0];
            final SecureObjectMessage secureMessage = (SecureObjectMessage) message.getObject();

            final Principal principal = secureMessage.getPrincipal();
            final Subject subject = secureMessage.getSubject();
            final Object credential = secureMessage.getCredential();

            if (principal != null && subject != null) {
                SecurityActions.setSubjectInfo(principal, subject, credential);
            }

            final ObjectMessage proxiedMessage = (ObjectMessage) Proxy.newProxyInstance(
                    ObjectMessage.class.getClassLoader(),
                    new Class[]{ObjectMessage.class},
                    new ObjectMessageProxy(message));
            ctx.setParameters(new Object[]{proxiedMessage});
        }

        return ctx.proceed();
    }
}
