/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.PublicKeyCredentialDescriptor
 *  com.yubico.webauthn.data.UserVerificationRequirement
 *  javax.annotation.Nonnull
 *  net.shibboleth.shared.annotation.constraint.NonnullElements
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.NonnullSupplier
 */
package net.shibboleth.idp.plugin.authn.webauthn.authn;

import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.UserVerificationRequirement;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.authn.BaseOptionsParameters;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.NonnullSupplier;

public final class CredentialRequestOptionsParameters
extends BaseOptionsParameters {
    @Nonnull
    @NonnullElements
    private final List<PublicKeyCredentialDescriptor> allowCredentials;

    private CredentialRequestOptionsParameters(Builder builder) {
        super(builder.userVerificationRequirement, builder.challenge);
        Constraint.isNotNull(builder.allowCredentials, (String)"AllowCredentials can not be null");
        this.allowCredentials = (List)((NonnullSupplier)builder.allowCredentials.stream().filter(Objects::nonNull).collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableList()))).get();
    }

    @Nonnull
    @NonnullElements
    public final List<PublicKeyCredentialDescriptor> getAllowCredentials() {
        return this.allowCredentials;
    }

    public static IUserVerificationRequirementStage builder() {
        return new Builder();
    }

    public static final class Builder
    implements IUserVerificationRequirementStage,
    IChallengeStage,
    IAllowCredentialsStage,
    IBuildStage {
        private UserVerificationRequirement userVerificationRequirement;
        private byte[] challenge;
        private List<PublicKeyCredentialDescriptor> allowCredentials = CollectionSupport.emptyList();

        private Builder() {
        }

        @Override
        public IChallengeStage withUserVerificationRequirement(@Nonnull UserVerificationRequirement uvRequirement) {
            this.userVerificationRequirement = uvRequirement;
            return this;
        }

        @Override
        public IAllowCredentialsStage withChallenge(@Nonnull byte[] challengeIn) {
            this.challenge = challengeIn;
            return this;
        }

        @Override
        public IBuildStage withAllowCredentials(@Nonnull List<PublicKeyCredentialDescriptor> allowCreds) {
            this.allowCredentials = allowCreds;
            return this;
        }

        @Override
        public CredentialRequestOptionsParameters build() {
            return new CredentialRequestOptionsParameters(this);
        }
    }

    public static interface IBuildStage {
        public CredentialRequestOptionsParameters build();
    }

    public static interface IAllowCredentialsStage {
        public IBuildStage withAllowCredentials(@Nonnull List<PublicKeyCredentialDescriptor> var1);
    }

    public static interface IChallengeStage {
        public IAllowCredentialsStage withChallenge(@Nonnull byte[] var1);
    }

    public static interface IUserVerificationRequirementStage {
        public IChallengeStage withUserVerificationRequirement(@Nonnull UserVerificationRequirement var1);
    }
}

