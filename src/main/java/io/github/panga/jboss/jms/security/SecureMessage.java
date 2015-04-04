package io.github.panga.jboss.jms.security;

import java.io.Serializable;
import java.security.Principal;
import javax.security.auth.Subject;
import org.jboss.security.SecurityContextAssociation;

public class SecureMessage implements Serializable {

    private final Serializable originalMessage;
    private final Principal principal;
    private final Subject subject;
    private final Object credential;

    public SecureMessage(final Serializable message) {
        this.originalMessage = message;
        this.principal = SecurityContextAssociation.getPrincipal();
        this.subject = SecurityContextAssociation.getSubject();
        this.credential = SecurityContextAssociation.getCredential();
    }

    public <T extends Serializable> T getContent(Class<T> clazz) {
        return (T) originalMessage;
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
