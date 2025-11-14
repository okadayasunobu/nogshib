/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnAuditExtractor
 *  net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.audit.impl;

import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnAuditExtractor;
import net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import org.opensaml.profile.context.ProfileRequestContext;

public class WebAuthnFlowModeAuditExtractor
extends AbstractWebAuthnAuditExtractor<String> {
    protected WebAuthnFlowModeAuditExtractor(@Nonnull Function<ProfileRequestContext, BaseWebAuthnContext> strategy) {
        super(strategy);
    }

    protected String doLookup(@Nonnull BaseWebAuthnContext context) {
        if (context instanceof WebAuthnAuthenticationContext) {
            WebAuthnAuthenticationContext authnContext = (WebAuthnAuthenticationContext)context;
            if (authnContext.isPasswordless()) {
                return "passwordless";
            }
            if (authnContext.isUsernameless()) {
                return "usernameless";
            }
            if (authnContext.isSecondFactor()) {
                return "second-factor";
            }
        }
        return null;
    }
}

