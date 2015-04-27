package com.github.panga.jboss.security.jms;

import com.github.panga.jboss.security.SecurityActions;
import java.io.Serializable;
import java.security.Principal;
import javax.security.auth.Subject;

/**
 *
 * @author Leonardo Zanivan
 */
public final class SecureObjectMessage implements Serializable {

    private final Serializable object;
    private final Principal principal;
    private final Subject subject;
    private final Object credential;

    public SecureObjectMessage(final Serializable object, final Principal principal) {
        this.object = object;
        this.principal = principal;
        this.subject = SecurityActions.getSubject();
        this.credential = SecurityActions.getCredential();
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
