package io.github.panga.jboss.jms.security;

import java.io.Serializable;
import java.security.Principal;
import javax.security.auth.Subject;
import org.jboss.security.SecurityContextAssociation;

public final class SecureObjectMessage implements Serializable {

    private final Serializable object;
    private final Principal principal;
    private final Subject subject;
    private final Object credential;

    public SecureObjectMessage(final Serializable object, final Principal principal) {
        this.object = object;
        this.principal = principal;
        this.subject = SecurityContextAssociation.getSubject();
        this.credential = SecurityContextAssociation.getCredential();
    }

    public <T extends Serializable> T getBody(Class<T> clazz) {
        return (T) object;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public Subject getSubject() {
        return subject;
    }

    public Object getCredential() {
        return credential;
    }

}
