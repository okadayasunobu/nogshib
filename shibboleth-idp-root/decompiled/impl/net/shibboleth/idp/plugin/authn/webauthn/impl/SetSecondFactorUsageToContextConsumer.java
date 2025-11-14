/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.BaseWebAuthnAuthenticationContextConsumer;

public class SetSecondFactorUsageToContextConsumer
extends BaseWebAuthnAuthenticationContextConsumer {
    @Override
    protected void doAccept(WebAuthnAuthenticationContext webAuthnContext) {
        webAuthnContext.setSecondFactor(true);
    }
}

