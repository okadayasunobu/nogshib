/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.shared.collection.CollectionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import java.util.Collection;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.collection.CollectionSupport;
import org.opensaml.profile.context.ProfileRequestContext;

public class ExistingAuthenticationCredentialsLookupStrategy
implements Function<ProfileRequestContext, Collection<EnhancedCredentialRecord>> {
    @Override
    public Collection<EnhancedCredentialRecord> apply(@Nullable ProfileRequestContext input) {
        WebAuthnAuthenticationContext webAuthnCtx;
        if (input == null) {
            return CollectionSupport.emptyList();
        }
        AuthenticationContext authnCtx = (AuthenticationContext)input.getSubcontext(AuthenticationContext.class);
        if (authnCtx != null && (webAuthnCtx = (WebAuthnAuthenticationContext)authnCtx.getSubcontext(WebAuthnAuthenticationContext.class)) != null) {
            return webAuthnCtx.getExistingCredentials();
        }
        return CollectionSupport.emptyList();
    }
}

