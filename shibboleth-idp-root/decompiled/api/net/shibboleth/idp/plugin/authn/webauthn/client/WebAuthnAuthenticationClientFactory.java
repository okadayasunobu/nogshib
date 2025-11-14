/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.shibboleth.idp.plugin.authn.webauthn.client;

import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.client.WebAuthnAuthenticationClient;
import net.shibboleth.idp.plugin.authn.webauthn.exception.WebAuthnAuthenticationClientException;

public interface WebAuthnAuthenticationClientFactory {
    @Nonnull
    public WebAuthnAuthenticationClient createInstance() throws WebAuthnAuthenticationClientException;
}

