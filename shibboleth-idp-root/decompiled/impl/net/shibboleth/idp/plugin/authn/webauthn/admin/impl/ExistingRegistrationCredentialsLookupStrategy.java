/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.shared.collection.CollectionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import java.util.Collection;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.collection.CollectionSupport;
import org.opensaml.profile.context.ProfileRequestContext;

public class ExistingRegistrationCredentialsLookupStrategy
implements Function<ProfileRequestContext, Collection<EnhancedCredentialRecord>> {
    @Override
    public Collection<EnhancedCredentialRecord> apply(@Nullable ProfileRequestContext input) {
        if (input == null) {
            return CollectionSupport.emptyList();
        }
        WebAuthnRegistrationContext webAuthnCtx = (WebAuthnRegistrationContext)input.getSubcontext(WebAuthnRegistrationContext.class);
        if (webAuthnCtx != null) {
            return webAuthnCtx.getExistingCredentials();
        }
        return CollectionSupport.emptyList();
    }
}

