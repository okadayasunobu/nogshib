/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnAuditExtractor
 *  net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext
 *  net.shibboleth.shared.codec.Base64Support
 *  net.shibboleth.shared.codec.EncodingException
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.audit.impl;

import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnAuditExtractor;
import net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.EncodingException;
import org.opensaml.profile.context.ProfileRequestContext;

public class WebAuthnUserIdAuditExtractor
extends AbstractWebAuthnAuditExtractor<String> {
    protected WebAuthnUserIdAuditExtractor(@Nonnull Function<ProfileRequestContext, BaseWebAuthnContext> strategy) {
        super(strategy);
    }

    protected String doLookup(@Nonnull BaseWebAuthnContext context) {
        byte[] userId = context.getUserId();
        if (userId == null) {
            return null;
        }
        try {
            return Base64Support.encode((byte[])userId, (boolean)false);
        }
        catch (EncodingException e) {
            return null;
        }
    }
}

