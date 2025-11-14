/*
 * Decompiled with CFR 0.152.
 */
package net.shibboleth.idp.plugin.authn.webauthn.exception;

public class WebAuthnAuthenticationClientException
extends Exception {
    private static final long serialVersionUID = -2380145079984333546L;

    public WebAuthnAuthenticationClientException() {
    }

    public WebAuthnAuthenticationClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebAuthnAuthenticationClientException(String message) {
        super(message);
    }

    public WebAuthnAuthenticationClientException(Throwable cause) {
        super(cause);
    }
}

