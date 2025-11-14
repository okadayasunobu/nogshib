/*
 * Decompiled with CFR 0.152.
 */
package net.shibboleth.idp.plugin.authn.webauthn.exception;

import net.shibboleth.idp.plugin.authn.webauthn.exception.WebAuthnAuthenticationClientException;

public class RegistrationFailureException
extends WebAuthnAuthenticationClientException {
    private static final long serialVersionUID = 1266841324008931444L;

    public RegistrationFailureException() {
    }

    public RegistrationFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistrationFailureException(String message) {
        super(message);
    }

    public RegistrationFailureException(Throwable cause) {
        super(cause);
    }
}

