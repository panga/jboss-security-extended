package io.github.panga.jboss.jms.security;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Set;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.security.auth.Subject;
import org.jboss.security.SecurityContext;
import org.jboss.security.SecurityContextAssociation;
import org.jboss.security.identity.Identity;
import org.jboss.security.identity.Role;
import org.jboss.security.identity.extensions.CredentialIdentityFactory;
import org.jboss.security.identity.plugins.SimpleRoleGroup;

public final class JmsSecurityInterceptor {

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
            final Role roleGroup = getRoleGroup(subject);

            final SecurityContext securityContext = SecurityContextAssociation.getSecurityContext();
            final Identity identity = CredentialIdentityFactory.createIdentity(principal, credential, roleGroup);
            securityContext.getUtil().createSubjectInfo(identity, subject);

            final ObjectMessage proxiedMessage = (ObjectMessage) Proxy.newProxyInstance(
                    ObjectMessage.class.getClassLoader(),
                    new Class[]{ObjectMessage.class},
                    new ObjectMessageProxy(message));
            ctx.setParameters(new Object[]{proxiedMessage});
        }

        return ctx.proceed();
    }

    private Role getRoleGroup(final Subject subject) {
        final Set<Group> groups = subject.getPrincipals(Group.class);
        for (Group group : groups) {
            if ("Roles".equals(group.getName())) {
                return new SimpleRoleGroup(group);
            }
        }
        return null;
    }

}
