/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.AttestationConveyancePreference
 *  com.yubico.webauthn.data.AuthenticatorAttachment
 *  com.yubico.webauthn.data.AuthenticatorAttestationResponse
 *  com.yubico.webauthn.data.ClientRegistrationExtensionOutputs
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
 *  com.yubico.webauthn.data.ResidentKeyRequirement
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.NotThreadSafe
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 *  net.shibboleth.shared.collection.CollectionSupport
 */
package net.shibboleth.idp.plugin.authn.webauthn.context;

import com.yubico.webauthn.data.AttestationConveyancePreference;
import com.yubico.webauthn.data.AuthenticatorAttachment;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.ResidentKeyRequirement;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import net.shibboleth.idp.plugin.authn.webauthn.admin.RegistrationResult;
import net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

@NotThreadSafe
public final class WebAuthnRegistrationContext
extends BaseWebAuthnContext {
    @Nullable
    private String name;
    @Nullable
    private PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> publicKeyCredentialAttestationResponse;
    @Nullable
    private PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions;
    @Nullable
    private RegistrationResult registrationResult;
    @Nullable
    private String credentialNickname;
    @Nullable
    private byte[] credentialIdToRemove;
    @Nullable
    private ResidentKeyRequirement residentKeyRequirement;
    @Nullable
    private AuthenticatorAttachment authenticatorAttachmentRequirement;
    @Nullable
    private AttestationConveyancePreference attestationConveyancePreference;
    @Nullable
    private String displayName;
    @Nullable
    private Map<String, String> authenticatorCapabilities;

    @Nonnull
    public BaseWebAuthnContext setName(@Nullable String webAuthnName) {
        this.name = webAuthnName;
        return this;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    public WebAuthnRegistrationContext setAuthenticatorAttachmentRequirement(@Nullable AuthenticatorAttachment requirement) {
        this.authenticatorAttachmentRequirement = requirement;
        return this;
    }

    public AuthenticatorAttachment getAuthenticatorAttachmentRequirement() {
        return this.authenticatorAttachmentRequirement;
    }

    @Nullable
    public PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> getPublicKeyCredentialAttestationResponse() {
        return this.publicKeyCredentialAttestationResponse;
    }

    @Nonnull
    public WebAuthnRegistrationContext setPublicKeyCredentialAttestationResponse(@Nullable PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkCredAttestation) {
        this.publicKeyCredentialAttestationResponse = pkCredAttestation;
        return this;
    }

    @Nonnull
    public WebAuthnRegistrationContext setPublicKeyCredentialCreationOptions(@Nullable PublicKeyCredentialCreationOptions options) {
        this.publicKeyCredentialCreationOptions = options;
        return this;
    }

    @Nullable
    public PublicKeyCredentialCreationOptions getPublicKeyCredentialCreationOptions() {
        return this.publicKeyCredentialCreationOptions;
    }

    @Nonnull
    public WebAuthnRegistrationContext setRegistrationResult(@Nullable RegistrationResult result) {
        this.registrationResult = result;
        return this;
    }

    @Nullable
    public RegistrationResult getRegistrationResult() {
        return this.registrationResult;
    }

    @Nonnull
    public WebAuthnRegistrationContext setCredentialNickname(@Nullable String nickname) {
        this.credentialNickname = nickname;
        return this;
    }

    @Nullable
    public String getCredentialNickname() {
        return this.credentialNickname;
    }

    @Nonnull
    public WebAuthnRegistrationContext setCredentialIdToRemove(@Nullable byte[] id) {
        this.credentialIdToRemove = id;
        return this;
    }

    @Nullable
    public byte[] getCredentialIdToRemove() {
        return this.credentialIdToRemove;
    }

    @Nonnull
    public WebAuthnRegistrationContext setResidentKeyRequirement(@Nullable ResidentKeyRequirement requirement) {
        this.residentKeyRequirement = requirement;
        return this;
    }

    @Nullable
    public ResidentKeyRequirement getResidentKeyRequirement() {
        return this.residentKeyRequirement;
    }

    @Nonnull
    public BaseWebAuthnContext setAttestationConveyancePreference(@Nullable AttestationConveyancePreference preference) {
        this.attestationConveyancePreference = preference;
        return this;
    }

    @Nullable
    public AttestationConveyancePreference getAttestationConveyancePreference() {
        return this.attestationConveyancePreference;
    }

    @Nonnull
    public BaseWebAuthnContext setDisplayName(@Nullable String dispName) {
        this.displayName = dispName;
        return this;
    }

    @Nullable
    public String getDisplayName() {
        return this.displayName;
    }

    @Nonnull
    public BaseWebAuthnContext setAuthenticatorCapabilities(@Nullable Map<String, String> capabilities) {
        this.authenticatorCapabilities = capabilities;
        return this;
    }

    @Nonnull
    @NotLive
    @Unmodifiable
    public Map<String, String> getAuthenticatorCapabilities() {
        if (this.authenticatorCapabilities != null) {
            return CollectionSupport.copyToMap(this.authenticatorCapabilities);
        }
        return CollectionSupport.emptyMap();
    }
}

