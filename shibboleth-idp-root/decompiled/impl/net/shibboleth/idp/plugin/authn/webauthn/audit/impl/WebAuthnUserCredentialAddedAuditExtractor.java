/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.RegistrationResult
 *  net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnRegistrationAuditExtractor
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
 *  net.shibboleth.shared.codec.Base64Support
 *  net.shibboleth.shared.codec.EncodingException
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.audit.impl;

import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.admin.RegistrationResult;
import net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnRegistrationAuditExtractor;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.EncodingException;
import org.opensaml.profile.context.ProfileRequestContext;

public class WebAuthnUserCredentialAddedAuditExtractor
extends AbstractWebAuthnRegistrationAuditExtractor<String> {
    public WebAuthnUserCredentialAddedAuditExtractor(@Nonnull Function<ProfileRequestContext, WebAuthnRegistrationContext> strategy) {
        super(strategy);
    }

    protected String doLookup(@Nonnull WebAuthnRegistrationContext context) {
        RegistrationResult result = context.getRegistrationResult();
        if (result == null) {
            return null;
        }
        byte[] credentialToRemove = result.getKeyId().getId().getBytes();
        if (credentialToRemove == null) {
            return null;
        }
        try {
            return Base64Support.encode((byte[])credentialToRemove, (boolean)false);
        }
        catch (EncodingException e) {
            return null;
        }
    }
}

