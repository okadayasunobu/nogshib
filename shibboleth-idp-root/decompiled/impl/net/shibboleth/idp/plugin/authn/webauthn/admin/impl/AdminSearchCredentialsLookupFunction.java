/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.shared.collection.CollectionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import java.util.Collection;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.collection.CollectionSupport;
import org.opensaml.profile.context.ProfileRequestContext;

public class AdminSearchCredentialsLookupFunction
implements Function<ProfileRequestContext, Collection<EnhancedCredentialRecord>> {
    @Override
    public Collection<EnhancedCredentialRecord> apply(@Nullable ProfileRequestContext input) {
        if (input == null) {
            return CollectionSupport.emptyList();
        }
        WebAuthnManagementContext manCtx = (WebAuthnManagementContext)input.getSubcontext(WebAuthnManagementContext.class);
        if (manCtx == null) {
            return CollectionSupport.emptyList();
        }
        return manCtx.getFoundCredentials();
    }
}

