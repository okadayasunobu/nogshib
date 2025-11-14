/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.AttestationConveyancePreference
 *  com.yubico.webauthn.data.AuthenticatorAttachment
 *  com.yubico.webauthn.data.PublicKeyCredentialDescriptor
 *  com.yubico.webauthn.data.ResidentKeyRequirement
 *  com.yubico.webauthn.data.UserVerificationRequirement
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.shared.annotation.constraint.NonnullElements
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.logic.Constraint
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin;

import com.yubico.webauthn.data.AttestationConveyancePreference;
import com.yubico.webauthn.data.AuthenticatorAttachment;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.ResidentKeyRequirement;
import com.yubico.webauthn.data.UserVerificationRequirement;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.idp.plugin.authn.webauthn.authn.BaseOptionsParameters;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;

@ThreadSafe
@Immutable
public final class CredentialCreationOptionsParameters
extends BaseOptionsParameters {
    @Nonnull
    @NonnullElements
    private final Set<PublicKeyCredentialDescriptor> excludeCredentials;
    @Nonnull
    @NotEmpty
    private final String name;
    @Nonnull
    @NotEmpty
    private final String displayName;
    @Nullable
    private final AuthenticatorAttachment authenticatorAttachment;
    @Nonnull
    private final ResidentKeyRequirement residentKeyRequirement;
    @Nonnull
    private final byte[] userId;
    @Nonnull
    private final AttestationConveyancePreference attestationConveyancePreference;
    private final boolean enableCredProperties;

    private CredentialCreationOptionsParameters(Builder builder) {
        super(builder.userVerificationRequirement, builder.challenge);
        this.excludeCredentials = (Set)Constraint.isNotNull(builder.excludeCredentials, (String)"Exclude credentails can not be null");
        this.name = Constraint.isNotEmpty((String)builder.name, (String)"User.name can not be null or empty");
        this.residentKeyRequirement = (ResidentKeyRequirement)Constraint.isNotNull((Object)builder.residentKeyRequirement, (String)"Resident key requirement can not be null");
        this.userId = (byte[])Constraint.isNotNull((Object)builder.userId, (String)"UserID can not be null");
        this.authenticatorAttachment = builder.authenticatorAttachment;
        this.attestationConveyancePreference = (AttestationConveyancePreference)Constraint.isNotNull((Object)builder.attestationConveyancePreference, (String)"AttestationConveyancePreference can not be null");
        this.enableCredProperties = builder.enableCredProperties;
        this.displayName = builder.displayName;
    }

    @Nonnull
    @NonnullElements
    public final Set<PublicKeyCredentialDescriptor> getExcludeCredentials() {
        return this.excludeCredentials;
    }

    @Nonnull
    @NotEmpty
    public final String getName() {
        return this.name;
    }

    @Nullable
    public final AuthenticatorAttachment getAuthenticatorAttachment() {
        return this.authenticatorAttachment;
    }

    @Nonnull
    public final ResidentKeyRequirement getResidentKeyRequirement() {
        return this.residentKeyRequirement;
    }

    @Nonnull
    public final byte[] getUserId() {
        return this.userId;
    }

    @Nonnull
    public final String getDisplayName() {
        return this.displayName;
    }

    public final boolean isEnableCredProperties() {
        return this.enableCredProperties;
    }

    public final AttestationConveyancePreference getAttestationConveyancePreference() {
        return this.attestationConveyancePreference;
    }

    public static IUserVerificationRequirementStage builder() {
        return new Builder();
    }

    public static final class Builder
    implements IUserVerificationRequirementStage,
    IChallengeStage,
    IExcludeCredentialsStage,
    INameStage,
    IDisplayNameStage,
    IResidentKeyRequirementStage,
    IUserHandleStage,
    IAttestationConveyancePreferenceStage,
    IBuildStage {
        private UserVerificationRequirement userVerificationRequirement;
        private byte[] challenge;
        private Set<PublicKeyCredentialDescriptor> excludeCredentials = Collections.emptySet();
        private String name;
        private ResidentKeyRequirement residentKeyRequirement;
        private byte[] userId;
        private AuthenticatorAttachment authenticatorAttachment;
        private AttestationConveyancePreference attestationConveyancePreference;
        private String displayName;
        private boolean enableCredProperties = false;

        private Builder() {
        }

        @Override
        public IChallengeStage withUserVerificationRequirement(@Nonnull UserVerificationRequirement uvRequirement) {
            this.userVerificationRequirement = uvRequirement;
            return this;
        }

        @Override
        public IExcludeCredentialsStage withChallenge(@Nonnull byte[] challengeIn) {
            this.challenge = challengeIn;
            return this;
        }

        @Override
        public INameStage withExcludeCredentials(@Nonnull Set<PublicKeyCredentialDescriptor> exclude) {
            this.excludeCredentials = exclude;
            return this;
        }

        @Override
        public IDisplayNameStage withName(@Nonnull String uname) {
            this.name = uname;
            return this;
        }

        @Override
        public IResidentKeyRequirementStage withDisplayName(@Nonnull String dispName) {
            this.displayName = dispName;
            return this;
        }

        @Override
        public IUserHandleStage withResidentKeyRequirement(@Nonnull ResidentKeyRequirement residentKeyReq) {
            this.residentKeyRequirement = residentKeyReq;
            return this;
        }

        @Override
        public IAttestationConveyancePreferenceStage withUserId(@Nonnull byte[] id) {
            this.userId = id;
            return this;
        }

        @Override
        public IBuildStage withAttestationConveyancePreference(@Nonnull AttestationConveyancePreference preference) {
            this.attestationConveyancePreference = preference;
            return this;
        }

        @Override
        public IBuildStage withCredentialPropertiesExt(boolean credProps) {
            this.enableCredProperties = credProps;
            return this;
        }

        @Override
        public IBuildStage withAuthenticatorAttachment(@Nullable AuthenticatorAttachment attachment) {
            this.authenticatorAttachment = attachment;
            return this;
        }

        @Override
        public CredentialCreationOptionsParameters build() {
            return new CredentialCreationOptionsParameters(this);
        }
    }

    public static interface IBuildStage {
        public IBuildStage withAuthenticatorAttachment(@Nullable AuthenticatorAttachment var1);

        public IBuildStage withCredentialPropertiesExt(boolean var1);

        public CredentialCreationOptionsParameters build();
    }

    public static interface IAttestationConveyancePreferenceStage {
        public IBuildStage withAttestationConveyancePreference(@Nonnull AttestationConveyancePreference var1);
    }

    public static interface IUserHandleStage {
        public IAttestationConveyancePreferenceStage withUserId(@Nonnull byte[] var1);
    }

    public static interface IResidentKeyRequirementStage {
        public IUserHandleStage withResidentKeyRequirement(@Nonnull ResidentKeyRequirement var1);
    }

    public static interface IDisplayNameStage {
        public IResidentKeyRequirementStage withDisplayName(@Nonnull @NotEmpty String var1);
    }

    public static interface INameStage {
        public IDisplayNameStage withName(@Nonnull @NotEmpty String var1);
    }

    public static interface IExcludeCredentialsStage {
        public INameStage withExcludeCredentials(@Nonnull Set<PublicKeyCredentialDescriptor> var1);
    }

    public static interface IChallengeStage {
        public IExcludeCredentialsStage withChallenge(@Nonnull byte[] var1);
    }

    public static interface IUserVerificationRequirementStage {
        public IChallengeStage withUserVerificationRequirement(@Nonnull UserVerificationRequirement var1);
    }
}

