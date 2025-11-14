/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonGetter
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder
 *  com.yubico.webauthn.RegisteredCredential
 *  com.yubico.webauthn.data.AuthenticatorTransport
 *  com.yubico.webauthn.data.PublicKeyCredentialDescriptor
 *  com.yubico.webauthn.data.UserIdentity
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.shared.annotation.constraint.NonnullElements
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 *  net.shibboleth.shared.logic.Constraint
 */
package net.shibboleth.idp.plugin.authn.webauthn.storage;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.UserIdentity;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.logic.Constraint;

@JsonDeserialize(builder=Builder.class)
@ThreadSafe
@Immutable
public final class CredentialRecord {
    @Nonnull
    private final UserIdentity userIdentity;
    @Nonnull
    private final String username;
    @Nullable
    private final String credentialNickname;
    @Nonnull
    @Unmodifiable
    @NonnullElements
    private final SortedSet<AuthenticatorTransport> transports;
    @Nonnull
    private final Instant registrationTime;
    @Nonnull
    private final Optional<Boolean> discoverable;
    @Nonnull
    @Unmodifiable
    @NonnullElements
    private final RegisteredCredential credential;
    @Nullable
    private final byte[] aaguid;
    private final boolean userVerified;

    private CredentialRecord(Builder builder) {
        this.userIdentity = builder.userIdentity;
        this.username = builder.username;
        this.transports = builder.transports;
        this.registrationTime = builder.registrationTime;
        this.credential = builder.credential;
        this.credentialNickname = builder.credentialNickname;
        this.discoverable = builder.discoverable;
        this.userVerified = builder.userVerified;
        this.aaguid = builder.aaguid;
    }

    @JsonGetter(value="userVerified")
    public boolean isUserVerified() {
        return this.userVerified;
    }

    @JsonGetter(value="discoverable")
    @Nonnull
    public Optional<Boolean> isDiscoverable() {
        return this.discoverable;
    }

    @JsonGetter(value="nickname")
    @Nullable
    public String getNickname() {
        return this.credentialNickname;
    }

    @JsonGetter(value="registrationTime")
    @Nonnull
    public Instant getRegistrationTime() {
        return this.registrationTime;
    }

    @JsonGetter(value="username")
    @Nonnull
    public String getUsername() {
        String name = this.username;
        assert (name != null);
        return name;
    }

    @JsonIgnore
    @Nonnull
    public String getWebAuthnUserName() {
        String name = this.userIdentity.getName();
        assert (name != null);
        return name;
    }

    @JsonGetter(value="userIdentity")
    @Nonnull
    public UserIdentity getUserIdentity() {
        return this.userIdentity;
    }

    @JsonGetter(value="credential")
    @Nonnull
    public RegisteredCredential getCredential() {
        return this.credential;
    }

    @JsonGetter(value="transports")
    @Nonnull
    @Unmodifiable
    @NonnullElements
    public SortedSet<AuthenticatorTransport> getTransports() {
        return this.transports;
    }

    @JsonGetter(value="aaguid")
    @Nullable
    public byte[] getAaguid() {
        return this.aaguid;
    }

    @JsonIgnore
    public String getCredentialIdBase64Url() {
        return this.credential.getCredentialId().getBase64Url();
    }

    @JsonIgnore
    public String getCredentialIdBase64() {
        return this.credential.getCredentialId().getBase64();
    }

    @JsonIgnore
    public String getCredentialIdHex() {
        return this.credential.getCredentialId().getHex();
    }

    @JsonIgnore
    @Nullable
    public PublicKeyCredentialDescriptor toPublicKeyCredentialDescriptor() {
        return PublicKeyCredentialDescriptor.builder().id(this.credential.getCredentialId()).transports(this.transports).build();
    }

    public int hashCode() {
        return Objects.hash(this.credential);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CredentialRecord other = (CredentialRecord)obj;
        return Objects.equals(this.credential, other.credential);
    }

    @JsonIgnore
    public CredentialRecord withCredential(@Nonnull RegisteredCredential newRegisteredCred) {
        return CredentialRecord.builder().withUserIdentity(this.userIdentity).withUsername(this.username).withTransports(this.transports).withRegistrationTime(this.registrationTime).withCredential(newRegisteredCred).withAaguid(this.aaguid).withCredentialNickname(this.credentialNickname).withDiscoverable(this.discoverable).withUserVerified(this.userVerified).build();
    }

    public static IUserIdentityStage builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(buildMethodName="build", withPrefix="with")
    public static final class Builder
    implements IUserIdentityStage,
    IUsernameStage,
    ITransportsStage,
    IRegistrationTimeStage,
    ICredentialStage,
    IBuildStage {
        @Nonnull
        private UserIdentity userIdentity;
        @Nonnull
        private String username;
        @Nonnull
        private SortedSet<AuthenticatorTransport> transports;
        @Nonnull
        private Instant registrationTime;
        @Nonnull
        private RegisteredCredential credential;
        @Nullable
        private String credentialNickname;
        @Nonnull
        private Optional<Boolean> discoverable = Optional.empty();
        private boolean userVerified = false;
        @Nullable
        private byte[] aaguid;

        private Builder() {
            this.transports = Collections.emptySortedSet();
        }

        @Override
        @JsonProperty(value="userIdentity")
        @Nonnull
        public IUsernameStage withUserIdentity(@Nonnull UserIdentity user) {
            this.userIdentity = (UserIdentity)Constraint.isNotNull((Object)user, (String)"UserIdentity can not be null");
            return this;
        }

        @Override
        @JsonProperty(value="username")
        @Nonnull
        public ITransportsStage withUsername(@Nonnull String name) {
            this.username = (String)Constraint.isNotNull((Object)name, (String)"Username can not be null");
            return this;
        }

        @Override
        @JsonProperty(value="transports")
        @Nonnull
        public IRegistrationTimeStage withTransports(@Nonnull SortedSet<AuthenticatorTransport> authenticatorTransports) {
            this.transports = authenticatorTransports == null ? Collections.emptySortedSet() : authenticatorTransports;
            return this;
        }

        @Override
        @JsonProperty(value="registrationTime")
        @Nonnull
        public ICredentialStage withRegistrationTime(@Nonnull Instant time) {
            this.registrationTime = (Instant)Constraint.isNotNull((Object)time, (String)"Registration time can not be null");
            return this;
        }

        @Override
        @JsonProperty(value="credential")
        @Nonnull
        public IBuildStage withCredential(@Nonnull RegisteredCredential cred) {
            this.credential = (RegisteredCredential)Constraint.isNotNull((Object)cred, (String)"Credential can not be null");
            return this;
        }

        @Override
        @JsonProperty(value="nickname")
        @Nonnull
        public IBuildStage withCredentialNickname(@Nullable String credNickname) {
            this.credentialNickname = credNickname;
            return this;
        }

        @Override
        @JsonProperty(value="discoverable")
        @Nonnull
        public IBuildStage withDiscoverable(@Nonnull Optional<Boolean> isDiscoverable) {
            this.discoverable = isDiscoverable;
            return this;
        }

        @Override
        @JsonProperty(value="userVerified")
        @Nonnull
        public IBuildStage withUserVerified(boolean isUserVerified) {
            this.userVerified = isUserVerified;
            return this;
        }

        @Override
        @Nonnull
        public CredentialRecord build() {
            return new CredentialRecord(this);
        }

        @Override
        @JsonProperty(value="aaguid")
        public IBuildStage withAaguid(byte[] authenticatorGuid) {
            this.aaguid = authenticatorGuid;
            return this;
        }
    }

    public static interface IUserIdentityStage {
        @Nonnull
        public IUsernameStage withUserIdentity(@Nonnull UserIdentity var1);
    }

    public static interface IUsernameStage {
        @Nonnull
        public ITransportsStage withUsername(@Nonnull String var1);
    }

    public static interface ITransportsStage {
        @Nonnull
        public IRegistrationTimeStage withTransports(@Nonnull SortedSet<AuthenticatorTransport> var1);
    }

    public static interface IRegistrationTimeStage {
        @Nonnull
        public ICredentialStage withRegistrationTime(@Nonnull Instant var1);
    }

    public static interface ICredentialStage {
        @Nonnull
        public IBuildStage withCredential(@Nonnull RegisteredCredential var1);
    }

    public static interface IBuildStage {
        @Nonnull
        public IBuildStage withCredentialNickname(@Nullable String var1);

        @Nonnull
        public IBuildStage withDiscoverable(@Nonnull Optional<Boolean> var1);

        @Nonnull
        public IBuildStage withUserVerified(boolean var1);

        @Nonnull
        public IBuildStage withAaguid(byte[] var1);

        @Nonnull
        public CredentialRecord build();
    }
}

