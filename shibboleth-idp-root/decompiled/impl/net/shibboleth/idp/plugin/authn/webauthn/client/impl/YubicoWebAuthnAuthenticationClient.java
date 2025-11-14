/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.AssertionRequest
 *  com.yubico.webauthn.AssertionResult
 *  com.yubico.webauthn.FinishAssertionOptions
 *  com.yubico.webauthn.FinishRegistrationOptions
 *  com.yubico.webauthn.RegistrationResult
 *  com.yubico.webauthn.RelyingParty
 *  com.yubico.webauthn.data.AttestationType
 *  com.yubico.webauthn.data.AuthenticatorAssertionResponse
 *  com.yubico.webauthn.data.AuthenticatorAttestationResponse
 *  com.yubico.webauthn.data.AuthenticatorSelectionCriteria
 *  com.yubico.webauthn.data.ByteArray
 *  com.yubico.webauthn.data.ClientAssertionExtensionOutputs
 *  com.yubico.webauthn.data.ClientRegistrationExtensionOutputs
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
 *  com.yubico.webauthn.data.PublicKeyCredentialParameters
 *  com.yubico.webauthn.data.PublicKeyCredentialRequestOptions
 *  com.yubico.webauthn.data.RegistrationExtensionInputs
 *  com.yubico.webauthn.data.UserIdentity
 *  com.yubico.webauthn.exception.RegistrationFailedException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.CredentialCreationOptionsParameters
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.RegistrationResult
 *  net.shibboleth.idp.plugin.authn.webauthn.authn.AssertionResult
 *  net.shibboleth.idp.plugin.authn.webauthn.authn.CredentialRequestOptionsParameters
 *  net.shibboleth.idp.plugin.authn.webauthn.client.WebAuthnAuthenticationClient
 *  net.shibboleth.idp.plugin.authn.webauthn.exception.AssertionFailureException
 *  net.shibboleth.idp.plugin.authn.webauthn.exception.RegistrationFailureException
 *  net.shibboleth.idp.plugin.authn.webauthn.exception.WebAuthnAuthenticationClientException
 *  net.shibboleth.shared.annotation.constraint.NonnullElements
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.client.impl;

import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.AttestationType;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialParameters;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import com.yubico.webauthn.data.RegistrationExtensionInputs;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.exception.RegistrationFailedException;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.idp.plugin.authn.webauthn.admin.CredentialCreationOptionsParameters;
import net.shibboleth.idp.plugin.authn.webauthn.admin.RegistrationResult;
import net.shibboleth.idp.plugin.authn.webauthn.authn.CredentialRequestOptionsParameters;
import net.shibboleth.idp.plugin.authn.webauthn.client.WebAuthnAuthenticationClient;
import net.shibboleth.idp.plugin.authn.webauthn.exception.AssertionFailureException;
import net.shibboleth.idp.plugin.authn.webauthn.exception.RegistrationFailureException;
import net.shibboleth.idp.plugin.authn.webauthn.exception.WebAuthnAuthenticationClientException;
import net.shibboleth.idp.plugin.authn.webauthn.impl.WebAuthnSupport;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.slf4j.Logger;

@ThreadSafe
public class YubicoWebAuthnAuthenticationClient
implements WebAuthnAuthenticationClient {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(YubicoWebAuthnAuthenticationClient.class);
    @Nonnull
    private final RelyingParty rp;
    @Nonnull
    @NonnullElements
    @NotLive
    private final List<PublicKeyCredentialParameters> preferredPublickeyParams;

    YubicoWebAuthnAuthenticationClient(@Nonnull RelyingParty relyingParty, @Nonnull @NonnullElements @NotLive List<PublicKeyCredentialParameters> list) {
        this.rp = (RelyingParty)Constraint.isNotNull((Object)relyingParty, (String)"The reyling party configuration can not be null");
        this.preferredPublickeyParams = (List)Constraint.isNotNull(list, (String)"PreferredPublickeyParams can not be null");
    }

    public PublicKeyCredentialRequestOptions createAuthenticationRequest(@Nonnull CredentialRequestOptionsParameters credentialRequestOptionsParameters) throws WebAuthnAuthenticationClientException {
        PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions = PublicKeyCredentialRequestOptions.builder().challenge(new ByteArray(credentialRequestOptionsParameters.getChallenge())).rpId(this.rp.getIdentity().getId()).allowCredentials(Optional.ofNullable(credentialRequestOptionsParameters.getAllowCredentials())).userVerification(credentialRequestOptionsParameters.getUserVerificationRequirement()).timeout(Optional.of(60000L)).build();
        if (publicKeyCredentialRequestOptions == null) {
            throw new WebAuthnAuthenticationClientException("Unable to build public key credential request options");
        }
        return publicKeyCredentialRequestOptions;
    }

    public PublicKeyCredentialCreationOptions createRegistrationRequest(@Nonnull CredentialCreationOptionsParameters credentialCreationOptionsParameters) throws WebAuthnAuthenticationClientException {
        UserIdentity userIdentity = UserIdentity.builder().name(credentialCreationOptionsParameters.getName()).displayName(credentialCreationOptionsParameters.getDisplayName()).id(new ByteArray(credentialCreationOptionsParameters.getUserId())).build();
        RegistrationExtensionInputs registrationExtensionInputs = credentialCreationOptionsParameters.isEnableCredProperties() ? RegistrationExtensionInputs.builder().credProps().build() : RegistrationExtensionInputs.builder().build();
        PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions = PublicKeyCredentialCreationOptions.builder().rp(this.rp.getIdentity()).user(userIdentity).challenge(new ByteArray(credentialCreationOptionsParameters.getChallenge())).pubKeyCredParams(this.preferredPublickeyParams).excludeCredentials(credentialCreationOptionsParameters.getExcludeCredentials()).attestation(credentialCreationOptionsParameters.getAttestationConveyancePreference()).authenticatorSelection(AuthenticatorSelectionCriteria.builder().userVerification(credentialCreationOptionsParameters.getUserVerificationRequirement()).residentKey(credentialCreationOptionsParameters.getResidentKeyRequirement()).authenticatorAttachment(credentialCreationOptionsParameters.getAuthenticatorAttachment()).build()).extensions(registrationExtensionInputs).timeout(Optional.empty()).build();
        if (publicKeyCredentialCreationOptions == null) {
            throw new WebAuthnAuthenticationClientException("Unable to build public key credential creation options");
        }
        return publicKeyCredentialCreationOptions;
    }

    public net.shibboleth.idp.plugin.authn.webauthn.authn.AssertionResult validateAuthenticatorAssertionResponse(@Nullable String string, @Nullable byte[] byArray, @Nonnull PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions, @Nonnull PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> publicKeyCredential) throws AssertionFailureException {
        try {
            AssertionResult assertionResult;
            Optional optional;
            AssertionRequest assertionRequest = AssertionRequest.builder().publicKeyCredentialRequestOptions(publicKeyCredentialRequestOptions).userHandle(Optional.ofNullable(byArray != null ? new ByteArray(byArray) : null)).username(Optional.ofNullable(string)).build();
            if (this.log.isDebugEnabled()) {
                if (string == null && byArray == null) {
                    optional = ((AuthenticatorAssertionResponse)publicKeyCredential.getResponse()).getUserHandle();
                    ByteArray byteArray = optional.isPresent() ? (ByteArray)optional.get() : null;
                    String string2 = byteArray != null ? WebAuthnSupport.toBase64OrUnknown(byteArray.getBytes()) : "unknown";
                    this.log.debug("Attempting validation of assumed discoverable credential with userHandle from response '{}'", (Object)string2);
                } else {
                    this.log.debug("Attempting validation of credential with username '{}' and userHandle '{}'", (Object)string, (Object)WebAuthnSupport.toBase64OrUnknown(byArray));
                }
            }
            if ((assertionResult = this.rp.finishAssertion(FinishAssertionOptions.builder().request(assertionRequest).response(publicKeyCredential).build())) == null) {
                throw new AssertionFailureException("Unable to validate authenticator assertion, null result");
            }
            if (!assertionResult.isSuccess()) {
                throw new AssertionFailureException("Authenticator assertion was not valid");
            }
            if (assertionResult.getCredentialId() != null) {
                this.log.debug("Credential ID: {}", (Object)WebAuthnSupport.toBase64OrUnknown(assertionResult.getCredentialId().getBytes()));
            } else {
                this.log.debug("Credential ID is null");
            }
            optional = net.shibboleth.idp.plugin.authn.webauthn.authn.AssertionResult.builder().withSuccess(assertionResult.isSuccess()).withUsername(assertionResult.getUsername()).withSignatureCounterValid(assertionResult.isSignatureCounterValid()).withUserId(assertionResult.getCredential().getUserHandle().getBytes()).withUserVerified(assertionResult.isUserVerified()).build();
            assert (optional != null);
            return optional;
        }
        catch (Exception exception) {
            throw new AssertionFailureException((Throwable)exception);
        }
    }

    public RegistrationResult validateAuthenticatorAttestationResponse(@Nonnull PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions, @Nonnull PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> publicKeyCredential) throws RegistrationFailureException {
        try {
            this.log.debug("Attempting validation of public key credential attestation '{}'", publicKeyCredential);
            com.yubico.webauthn.RegistrationResult registrationResult = this.rp.finishRegistration(FinishRegistrationOptions.builder().request(publicKeyCredentialCreationOptions).response(publicKeyCredential).build());
            AttestationType attestationType = registrationResult.getAttestationType();
            if (attestationType == null) {
                throw new RegistrationFailureException("Attestation type was null");
            }
            RegistrationResult registrationResult2 = RegistrationResult.builder().withAttestationTrusted(registrationResult.isAttestationTrusted()).withAttestationType(attestationType).withCredential(publicKeyCredential).build();
            assert (registrationResult2 != null);
            return registrationResult2;
        }
        catch (RegistrationFailedException registrationFailedException) {
            throw new RegistrationFailureException((Throwable)registrationFailedException);
        }
    }
}

