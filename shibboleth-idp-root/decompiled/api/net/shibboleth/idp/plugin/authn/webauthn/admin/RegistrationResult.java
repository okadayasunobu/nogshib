/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.AttestationType
 *  com.yubico.webauthn.data.AttestedCredentialData
 *  com.yubico.webauthn.data.AuthenticatorAttestationResponse
 *  com.yubico.webauthn.data.ByteArray
 *  com.yubico.webauthn.data.ClientRegistrationExtensionOutputs
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  com.yubico.webauthn.data.PublicKeyCredentialDescriptor
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.shared.logic.Constraint
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin;

import com.yubico.webauthn.data.AttestationType;
import com.yubico.webauthn.data.AttestedCredentialData;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.shared.logic.Constraint;

@ThreadSafe
@Immutable
public final class RegistrationResult {
    private final boolean attestationTrusted;
    @Nonnull
    private final AttestationType attestationType;
    @Nonnull
    private final PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential;

    private RegistrationResult(Builder builder) {
        this.attestationTrusted = builder.attestationTrusted;
        this.attestationType = builder.attestationType;
        this.credential = builder.credential;
    }

    public final boolean isAttestationTrusted() {
        return this.attestationTrusted;
    }

    @Nonnull
    public final AttestationType getAttestationType() {
        return this.attestationType;
    }

    @Nonnull
    public PublicKeyCredentialDescriptor getKeyId() {
        PublicKeyCredentialDescriptor descriptor = PublicKeyCredentialDescriptor.builder().id(this.credential.getId()).type(this.credential.getType()).transports((Set)((AuthenticatorAttestationResponse)this.credential.getResponse()).getTransports()).build();
        assert (descriptor != null);
        return descriptor;
    }

    @Nullable
    public ByteArray getPublicKeyCose() {
        Optional attestedCredentialData = ((AuthenticatorAttestationResponse)this.credential.getResponse()).getAttestation().getAuthenticatorData().getAttestedCredentialData();
        if (attestedCredentialData.isPresent()) {
            return ((AttestedCredentialData)attestedCredentialData.get()).getCredentialPublicKey();
        }
        return null;
    }

    @Nullable
    public ByteArray getAaguid() {
        Optional attestedCredentialData = ((AuthenticatorAttestationResponse)this.credential.getResponse()).getAttestation().getAuthenticatorData().getAttestedCredentialData();
        if (attestedCredentialData.isPresent()) {
            return ((AttestedCredentialData)attestedCredentialData.get()).getAaguid();
        }
        return null;
    }

    @Nonnull
    public Optional<Boolean> isDiscoverable() {
        ClientRegistrationExtensionOutputs clientExtensions = (ClientRegistrationExtensionOutputs)this.credential.getClientExtensionResults();
        if (!clientExtensions.getExtensionIds().isEmpty() && clientExtensions.getCredProps().isPresent()) {
            return clientExtensions.getCredProps().flatMap(credProps -> credProps.getRk());
        }
        return Optional.empty();
    }

    public boolean isUserVerified() {
        return ((AuthenticatorAttestationResponse)this.credential.getResponse()).getParsedAuthenticatorData().getFlags().UV;
    }

    @Nonnull
    public final PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> getCredential() {
        return this.credential;
    }

    @Nonnull
    public static IAttestationTrustedStage builder() {
        return new Builder();
    }

    public static final class Builder
    implements IAttestationTrustedStage,
    IAttestationTypeStage,
    ICredentialStage,
    IBuildStage {
        private boolean attestationTrusted;
        @Nonnull
        private AttestationType attestationType;
        @Nonnull
        private PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential;

        private Builder() {
        }

        @Override
        public IAttestationTypeStage withAttestationTrusted(boolean attTrusted) {
            this.attestationTrusted = attTrusted;
            return this;
        }

        @Override
        public ICredentialStage withAttestationType(@Nonnull AttestationType attType) {
            this.attestationType = (AttestationType)Constraint.isNotNull((Object)attType, (String)"AttestationType can not be null");
            return this;
        }

        @Override
        public IBuildStage withCredential(@Nonnull PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> cred) {
            this.credential = (PublicKeyCredential)Constraint.isNotNull(cred, (String)"Credential cannot be null");
            return this;
        }

        @Override
        public RegistrationResult build() {
            return new RegistrationResult(this);
        }
    }

    public static interface IBuildStage {
        public RegistrationResult build();
    }

    public static interface ICredentialStage {
        public IBuildStage withCredential(@Nonnull PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> var1);
    }

    public static interface IAttestationTypeStage {
        public ICredentialStage withAttestationType(@Nonnull AttestationType var1);
    }

    public static interface IAttestationTrustedStage {
        public IAttestationTypeStage withAttestationTrusted(boolean var1);
    }
}

