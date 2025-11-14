/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  com.yubico.webauthn.data.AuthenticatorAttestationResponse
 *  com.yubico.webauthn.data.ByteArray
 *  com.yubico.webauthn.data.ClientRegistrationExtensionOutputs
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.RegistrationResult
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationErrorContext
 *  net.shibboleth.idp.plugin.authn.webauthn.exception.RegistrationFailureException
 *  net.shibboleth.shared.annotation.constraint.NonnullBeforeExec
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import com.yubico.fido.metadata.AAGUID;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.admin.RegistrationResult;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationErrorContext;
import net.shibboleth.idp.plugin.authn.webauthn.exception.RegistrationFailureException;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class ValidateAuthenticatorAttestationResponse
extends AbstractWebAuthnAction<WebAuthnRegistrationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(ValidateAuthenticatorAttestationResponse.class);
    @NonnullBeforeExec
    private PublicKeyCredentialCreationOptions pkCredCreationOptions;
    @NonnullBeforeExec
    private PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> attestation;

    public ValidateAuthenticatorAttestationResponse() {
        super(new ChildContextLookup(WebAuthnRegistrationContext.class));
    }

    @Override
    protected boolean doPreExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnRegistrationContext context) {
        if (!super.doPreExecute(profileRequestContext, context)) {
            return false;
        }
        this.attestation = context.getPublicKeyCredentialAttestationResponse();
        if (this.attestation == null) {
            this.log.error("{} PublicKeyCredential containing the authenticator attestation response was null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            ((WebAuthnRegistrationErrorContext)context.ensureSubcontext(WebAuthnRegistrationErrorContext.class)).addClassifiedError("InvalidRegistration");
            return false;
        }
        this.pkCredCreationOptions = context.getPublicKeyCredentialCreationOptions();
        if (this.pkCredCreationOptions == null) {
            this.log.error("{} PublicKeyCredential creation options was null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            ((WebAuthnRegistrationErrorContext)context.ensureSubcontext(WebAuthnRegistrationErrorContext.class)).addClassifiedError("InvalidRegistration");
            return false;
        }
        return true;
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnRegistrationContext context) {
        try {
            RegistrationResult credentialPublicKey = this.getWebAuthnClient().validateAuthenticatorAttestationResponse(this.pkCredCreationOptions, this.attestation);
            ByteArray aaguid = credentialPublicKey.getAaguid();
            String athenticator = aaguid != null ? new AAGUID(aaguid).asGuidString() : "unknown";
            this.log.trace("{} Was attestation statement for authenticator '{}' trusted? {}", new Object[]{this.getLogPrefix(), athenticator, credentialPublicKey.isAttestationTrusted() ? "Yes" : "No"});
            context.setRegistrationResult(credentialPublicKey);
            this.log.info("{} Public key registration was valid for '{}'", (Object)this.getLogPrefix(), (Object)context.getUsername());
        }
        catch (RegistrationFailureException e) {
            this.log.warn("{} Public key registration failed for '{}'", new Object[]{this.getLogPrefix(), context.getUsername(), e});
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            ((WebAuthnRegistrationErrorContext)context.ensureSubcontext(WebAuthnRegistrationErrorContext.class)).addClassifiedError("InvalidRegistration");
            return;
        }
    }
}

