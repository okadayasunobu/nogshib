/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 *  net.shibboleth.shared.collection.CollectionSupport
 *  org.opensaml.messaging.context.BaseContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.context;

import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import org.opensaml.messaging.context.BaseContext;

public class WebAuthnManagementContext
extends BaseContext {
    @Nullable
    private String principalName;
    @Nullable
    private String searchUsername;
    @Nullable
    @Unmodifiable
    @NotLive
    private Collection<EnhancedCredentialRecord> foundCredentials;
    @Nullable
    private byte[] credentialIdToRemove;

    @Nullable
    public String getPrincipalName() {
        return this.principalName;
    }

    @Nonnull
    public WebAuthnManagementContext setPrincipalName(@Nullable String name) {
        this.principalName = name;
        return this;
    }

    @Nullable
    public String getSearchUsername() {
        return this.searchUsername;
    }

    @Nonnull
    public WebAuthnManagementContext setSearchUsername(@Nullable String name) {
        this.searchUsername = name;
        return this;
    }

    @Nonnull
    public WebAuthnManagementContext setFoundCredentials(@Nullable Collection<EnhancedCredentialRecord> credentials) {
        this.foundCredentials = credentials == null ? CollectionSupport.emptyList() : CollectionSupport.copyToList(credentials);
        return this;
    }

    @Nonnull
    @Unmodifiable
    @NotLive
    public Collection<EnhancedCredentialRecord> getFoundCredentials() {
        Collection<EnhancedCredentialRecord> localFoundCredentials = this.foundCredentials;
        if (localFoundCredentials == null) {
            return CollectionSupport.emptyList();
        }
        return localFoundCredentials;
    }

    @Nonnull
    public WebAuthnManagementContext setCredentialIdToRemove(@Nullable byte[] id) {
        this.credentialIdToRemove = id;
        return this;
    }

    @Nullable
    public byte[] getCredentialIdToRemove() {
        return this.credentialIdToRemove;
    }
}

