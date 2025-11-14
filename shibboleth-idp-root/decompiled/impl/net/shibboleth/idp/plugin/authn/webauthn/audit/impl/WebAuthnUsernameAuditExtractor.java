/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnAuditExtractor
 *  net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.audit.impl;

import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnAuditExtractor;
import net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext;
import org.opensaml.profile.context.ProfileRequestContext;

public class WebAuthnUsernameAuditExtractor
extends AbstractWebAuthnAuditExtractor<String> {
    protected WebAuthnUsernameAuditExtractor(@Nonnull Function<ProfileRequestContext, BaseWebAuthnContext> strategy) {
        super(strategy);
    }

    protected String doLookup(@Nonnull BaseWebAuthnContext context) {
        return context.getUsername();
    }
}

