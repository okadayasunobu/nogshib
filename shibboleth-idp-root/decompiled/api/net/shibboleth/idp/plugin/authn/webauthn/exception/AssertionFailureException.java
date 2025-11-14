/*
 * Decompiled with CFR 0.152.
 */
package net.shibboleth.idp.plugin.authn.webauthn.exception;

import net.shibboleth.idp.plugin.authn.webauthn.exception.WebAuthnAuthenticationClientException;

public class AssertionFailureException
extends WebAuthnAuthenticationClientException {
    private static final long serialVersionUID = 1266841324008931444L;

    public AssertionFailureException() {
    }

    public AssertionFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssertionFailureException(String message) {
        super(message);
    }

    public AssertionFailureException(Throwable cause) {
        super(cause);
    }
}

