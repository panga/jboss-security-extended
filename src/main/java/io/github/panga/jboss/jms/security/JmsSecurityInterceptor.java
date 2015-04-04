package io.github.panga.jboss.jms.security;

import java.lang.reflect.Method;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Set;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.jms.Message;
import javax.security.auth.Subject;
import org.jboss.security.SecurityContext;
import org.jboss.security.SecurityContextAssociation;
import org.jboss.security.identity.Identity;
import org.jboss.security.identity.Role;
import org.jboss.security.identity.extensions.CredentialIdentityFactory;
import org.jboss.security.identity.plugins.SimpleRoleGroup;

public class JmsSecurityInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        final Method method = ctx.getMethod();
        if ("onMessage".equals(method.getName())
                && method.getParameterCount() == 1
                && method.getParameterTypes()[0] == Message.class) {
            final Message message = (Message) ctx.getParameters()[0];

            final SecureMessage secureMessage = message.getBody(SecureMessage.class);

            Principal principal = secureMessage.getPrincipal();
            Subject subject = secureMessage.getSubject();
            Object credential = secureMessage.getCredential();
            Role roleGroup = getRoleGroup(subject);

            SecurityContext securityContext = SecurityContextAssociation.getSecurityContext();
            Identity identity = CredentialIdentityFactory.createIdentity(principal, credential, roleGroup);
            securityContext.getUtil().createSubjectInfo(identity, subject);
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
