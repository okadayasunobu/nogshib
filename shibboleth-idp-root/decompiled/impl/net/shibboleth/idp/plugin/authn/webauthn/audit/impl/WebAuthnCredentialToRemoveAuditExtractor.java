/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnManagementAuditExtractor
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext
 *  net.shibboleth.shared.codec.Base64Support
 *  net.shibboleth.shared.codec.EncodingException
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.audit.impl;

import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnManagementAuditExtractor;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.EncodingException;
import org.opensaml.profile.context.ProfileRequestContext;

public class WebAuthnCredentialToRemoveAuditExtractor
extends AbstractWebAuthnManagementAuditExtractor<String> {
    public WebAuthnCredentialToRemoveAuditExtractor(@Nonnull Function<ProfileRequestContext, WebAuthnManagementContext> strategy) {
        super(strategy);
    }

    protected String doLookup(@Nonnull WebAuthnManagementContext context) {
        byte[] credentialToRemove = context.getCredentialIdToRemove();
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

