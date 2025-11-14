/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.shibboleth.idp.plugin.authn.webauthn.exception;

import javax.annotation.Nullable;

public class CredentialRepositoryException
extends RuntimeException {
    private static final long serialVersionUID = -4999178337302857500L;

    public CredentialRepositoryException() {
    }

    public CredentialRepositoryException(@Nullable String message) {
        super(message);
    }

    public CredentialRepositoryException(@Nullable Exception wrappedException) {
        super(wrappedException);
    }

    public CredentialRepositoryException(@Nullable String message, @Nullable Exception wrappedException) {
        super(message, wrappedException);
    }
}

