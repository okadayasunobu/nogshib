/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnManagementAuditExtractor
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.audit.impl;

import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.audit.AbstractWebAuthnManagementAuditExtractor;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext;
import org.opensaml.profile.context.ProfileRequestContext;

public class WebAuthnSearchUserAuditExtractor
extends AbstractWebAuthnManagementAuditExtractor<String> {
    public WebAuthnSearchUserAuditExtractor(@Nonnull Function<ProfileRequestContext, WebAuthnManagementContext> strategy) {
        super(strategy);
    }

    protected String doLookup(@Nonnull WebAuthnManagementContext context) {
        return context.getSearchUsername();
    }
}

