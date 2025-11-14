/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  com.yubico.webauthn.data.AttestedCredentialData
 *  com.yubico.webauthn.data.AuthenticatorAttestationResponse
 *  com.yubico.webauthn.data.ByteArray
 *  com.yubico.webauthn.data.ClientRegistrationExtensionOutputs
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy$AuthenticatorPolicyOutcome
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationErrorContext
 *  net.shibboleth.shared.annotation.constraint.NonnullBeforeExec
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import com.yubico.fido.metadata.AAGUID;
import com.yubico.webauthn.data.AttestedCredentialData;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationErrorContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class CheckRegistrationPolicy
extends AbstractWebAuthnAction<WebAuthnRegistrationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(CheckRegistrationPolicy.class);
    @NonnullBeforeExec
    private PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> attestation;
    @Nullable
    private AuthenticatorPolicy authenticatorPolicy;

    protected CheckRegistrationPolicy() {
        super(new ChildContextLookup(WebAuthnRegistrationContext.class));
    }

    public void setAuthenticatorPolicy(@Nullable AuthenticatorPolicy policy) {
        this.checkSetterPreconditions();
        this.authenticatorPolicy = policy;
    }

    @Override
    protected boolean doPreExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnRegistrationContext context) {
        if (!super.doPreExecute(profileRequestContext, context)) {
            return false;
        }
        this.attestation = context.getPublicKeyCredentialAttestationResponse();
        if (this.attestation == null) {
            this.log.error("{} Attestaion not available in registration context", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistrationContext");
            return false;
        }
        return true;
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnRegistrationContext context) {
        AuthenticatorPolicy localPolicy = this.authenticatorPolicy;
        if (localPolicy == null) {
            this.log.trace("{} No authenticator policy to apply", (Object)this.getLogPrefix());
            return;
        }
        Optional attestedCredData = ((AuthenticatorAttestationResponse)this.attestation.getResponse()).getParsedAuthenticatorData().getAttestedCredentialData();
        if (attestedCredData.isEmpty()) {
            this.log.warn("{} Public key registration failed for '{}', AAGUID not found", (Object)this.getLogPrefix(), (Object)context.getUsername());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            ((WebAuthnRegistrationErrorContext)context.ensureSubcontext(WebAuthnRegistrationErrorContext.class)).addClassifiedError("InvalidRegistration");
            return;
        }
        ByteArray aaguid = ((AttestedCredentialData)attestedCredData.get()).getAaguid();
        AAGUID authenticatorAttestationGUID = new AAGUID(aaguid);
        if (localPolicy.evaluate(authenticatorAttestationGUID, profileRequestContext) == AuthenticatorPolicy.AuthenticatorPolicyOutcome.REJECT) {
            if (this.log.isWarnEnabled()) {
                this.log.warn("{} Public key registration failed for '{}', authenticator '{}' not allowed", new Object[]{this.getLogPrefix(), context.getUsername(), authenticatorAttestationGUID.asGuidString()});
            }
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            ((WebAuthnRegistrationErrorContext)context.ensureSubcontext(WebAuthnRegistrationErrorContext.class)).addClassifiedError("InvalidRegistration");
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} Authenticator '{}' accepted", (Object)this.getLogPrefix(), (Object)authenticatorAttestationGUID.asGuidString());
        }
    }
}

