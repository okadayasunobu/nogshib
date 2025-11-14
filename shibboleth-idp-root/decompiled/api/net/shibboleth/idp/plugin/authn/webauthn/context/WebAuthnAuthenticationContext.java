/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.AuthenticatorAssertionResponse
 *  com.yubico.webauthn.data.ClientAssertionExtensionOutputs
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  com.yubico.webauthn.data.PublicKeyCredentialRequestOptions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.NotThreadSafe
 *  net.shibboleth.shared.logic.Constraint
 */
package net.shibboleth.idp.plugin.authn.webauthn.context;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext;
import net.shibboleth.shared.logic.Constraint;

@NotThreadSafe
public final class WebAuthnAuthenticationContext
extends BaseWebAuthnContext {
    @Nullable
    private byte[] credentialId;
    private boolean secondFactor;
    private boolean passwordless;
    private boolean usernameless;
    @Nullable
    private PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> publicKeyCredentialAssertionResponse;
    @Nullable
    private PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions;

    public byte[] getCredentialId() {
        return this.credentialId;
    }

    public WebAuthnAuthenticationContext setCredentialId(@Nonnull byte[] id) {
        this.credentialId = (byte[])Constraint.isNotNull((Object)id, (String)"Credential ID can not be null");
        return this;
    }

    @Nonnull
    public WebAuthnAuthenticationContext setPublicKeyCredentialRequestOptions(@Nullable PublicKeyCredentialRequestOptions options) {
        this.publicKeyCredentialRequestOptions = options;
        return this;
    }

    @Nullable
    public PublicKeyCredentialRequestOptions getPublicKeyCredentialRequestOptions() {
        return this.publicKeyCredentialRequestOptions;
    }

    @Nonnull
    public WebAuthnAuthenticationContext setPublicKeyCredentialAssertionResponse(@Nullable PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkCredAssertion) {
        this.publicKeyCredentialAssertionResponse = pkCredAssertion;
        return this;
    }

    @Nullable
    public PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> getPublicKeyCredentialAssertionResponse() {
        return this.publicKeyCredentialAssertionResponse;
    }

    @Nonnull
    public WebAuthnAuthenticationContext setUsernameless(boolean flag) {
        this.usernameless = flag;
        return this;
    }

    public boolean isUsernameless() {
        return this.usernameless;
    }

    @Nonnull
    public WebAuthnAuthenticationContext setPasswordless(boolean flag) {
        this.passwordless = flag;
        return this;
    }

    public boolean isPasswordless() {
        return this.passwordless;
    }

    @Nonnull
    public WebAuthnAuthenticationContext setSecondFactor(boolean flag) {
        this.secondFactor = flag;
        return this;
    }

    public boolean isSecondFactor() {
        return this.secondFactor;
    }
}

