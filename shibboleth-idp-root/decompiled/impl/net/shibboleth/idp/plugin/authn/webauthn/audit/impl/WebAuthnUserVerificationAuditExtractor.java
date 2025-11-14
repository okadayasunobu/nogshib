/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.AuthenticatorAssertionResponse
 *  com.yubico.webauthn.data.AuthenticatorData
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnAuditExtractor
 *  net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.audit.impl;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorData;
import com.yubico.webauthn.data.PublicKeyCredential;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnAuditExtractor;
import net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import org.opensaml.profile.context.ProfileRequestContext;

public class WebAuthnUserVerificationAuditExtractor
extends AbstractWebAuthnAuditExtractor<String> {
    protected WebAuthnUserVerificationAuditExtractor(@Nonnull Function<ProfileRequestContext, BaseWebAuthnContext> strategy) {
        super(strategy);
    }

    protected String doLookup(@Nonnull BaseWebAuthnContext context) {
        AuthenticatorData authnData;
        AuthenticatorAssertionResponse authenticatorResponse;
        WebAuthnAuthenticationContext authnContext;
        PublicKeyCredential response;
        if (context instanceof WebAuthnAuthenticationContext && (response = (authnContext = (WebAuthnAuthenticationContext)context).getPublicKeyCredentialAssertionResponse()) != null && (authenticatorResponse = (AuthenticatorAssertionResponse)response.getResponse()) != null && (authnData = authenticatorResponse.getParsedAuthenticatorData()) != null) {
            if (authnData.getFlags().UV) {
                return "true";
            }
            return "false";
        }
        return null;
    }
}

