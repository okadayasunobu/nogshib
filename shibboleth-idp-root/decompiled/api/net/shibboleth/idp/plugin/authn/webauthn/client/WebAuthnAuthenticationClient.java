/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.AuthenticatorAssertionResponse
 *  com.yubico.webauthn.data.AuthenticatorAttestationResponse
 *  com.yubico.webauthn.data.ClientAssertionExtensionOutputs
 *  com.yubico.webauthn.data.ClientRegistrationExtensionOutputs
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
 *  com.yubico.webauthn.data.PublicKeyCredentialRequestOptions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.ThreadSafe
 */
package net.shibboleth.idp.plugin.authn.webauthn.client;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.idp.plugin.authn.webauthn.admin.CredentialCreationOptionsParameters;
import net.shibboleth.idp.plugin.authn.webauthn.admin.RegistrationResult;
import net.shibboleth.idp.plugin.authn.webauthn.authn.AssertionResult;
import net.shibboleth.idp.plugin.authn.webauthn.authn.CredentialRequestOptionsParameters;
import net.shibboleth.idp.plugin.authn.webauthn.exception.AssertionFailureException;
import net.shibboleth.idp.plugin.authn.webauthn.exception.RegistrationFailureException;
import net.shibboleth.idp.plugin.authn.webauthn.exception.WebAuthnAuthenticationClientException;

@ThreadSafe
public interface WebAuthnAuthenticationClient {
    @Nonnull
    public PublicKeyCredentialRequestOptions createAuthenticationRequest(@Nonnull CredentialRequestOptionsParameters var1) throws WebAuthnAuthenticationClientException;

    @Nonnull
    public PublicKeyCredentialCreationOptions createRegistrationRequest(@Nonnull CredentialCreationOptionsParameters var1) throws WebAuthnAuthenticationClientException;

    @Nonnull
    public AssertionResult validateAuthenticatorAssertionResponse(@Nullable String var1, @Nullable byte[] var2, @Nonnull PublicKeyCredentialRequestOptions var3, @Nonnull PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> var4) throws AssertionFailureException;

    @Nonnull
    public RegistrationResult validateAuthenticatorAttestationResponse(@Nonnull PublicKeyCredentialCreationOptions var1, @Nonnull PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> var2) throws RegistrationFailureException;
}

