/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.UserVerificationRequirement
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.logic.Constraint
 *  org.opensaml.messaging.context.BaseContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.context;

import com.yubico.webauthn.data.UserVerificationRequirement;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import org.opensaml.messaging.context.BaseContext;

public class BaseWebAuthnContext
extends BaseContext {
    @Nullable
    private String username;
    @Nullable
    private String rawUsername;
    @Nullable
    private Collection<EnhancedCredentialRecord> existingCredentials;
    @Nullable
    private byte[] serverChallenge;
    @Nullable
    private byte[] userId;
    @Nullable
    private UserVerificationRequirement userVerificationRequirement;

    public boolean isWebAuthnAvailable() {
        return this.existingCredentials != null && !this.existingCredentials.isEmpty();
    }

    @Nullable
    public String getUsername() {
        return this.username;
    }

    @Nonnull
    public BaseWebAuthnContext setUsername(@Nullable String name) {
        this.username = name;
        return this;
    }

    @Nullable
    public String getRawUsername() {
        return this.rawUsername;
    }

    @Nonnull
    public BaseWebAuthnContext setRawUsername(@Nullable String name) {
        this.rawUsername = name;
        return this;
    }

    @Nonnull
    public BaseWebAuthnContext setExistingCredentials(@Nullable Collection<EnhancedCredentialRecord> credentials) {
        this.existingCredentials = credentials;
        return this;
    }

    @Nonnull
    @Unmodifiable
    @NotLive
    public Collection<EnhancedCredentialRecord> getExistingCredentials() {
        Collection<EnhancedCredentialRecord> localExistingCredentials = this.existingCredentials;
        if (localExistingCredentials == null) {
            return CollectionSupport.emptyList();
        }
        return CollectionSupport.copyToList(localExistingCredentials);
    }

    @Nullable
    public byte[] getServerChallenge() {
        return this.serverChallenge;
    }

    @Nonnull
    public BaseWebAuthnContext setServerChallenge(@Nonnull byte[] challenge) {
        Constraint.isNotEmpty((byte[])challenge, (String)"Challenge can not be null or empty");
        Constraint.isGreaterThan((int)16, (int)challenge.length, (String)"Challenge must be at least 16 bytes");
        this.serverChallenge = challenge;
        return this;
    }

    @Nonnull
    public BaseWebAuthnContext setUserId(@Nonnull byte[] id) {
        Constraint.isNotEmpty((byte[])id, (String)"UserID can not be null or empty");
        Constraint.isLessThan((int)65, (int)id.length, (String)"UserID must be maximum 64 bytes");
        this.userId = id;
        return this;
    }

    @Nullable
    public byte[] getUserId() {
        return this.userId;
    }

    @Nonnull
    public BaseWebAuthnContext setUserVerificationRequirement(@Nullable UserVerificationRequirement requirement) {
        this.userVerificationRequirement = requirement;
        return this;
    }

    @Nullable
    public UserVerificationRequirement getUserVerificationRequirement() {
        return this.userVerificationRequirement;
    }
}

