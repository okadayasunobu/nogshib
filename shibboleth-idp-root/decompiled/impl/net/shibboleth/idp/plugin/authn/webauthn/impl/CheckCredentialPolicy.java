/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.AuthenticatorAssertionResponse
 *  com.yubico.webauthn.data.ByteArray
 *  com.yubico.webauthn.data.ClientAssertionExtensionOutputs
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.authn.context.AuthenticationErrorContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy
 *  net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy$CredentialPolicyOutcome
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.annotation.constraint.NonnullBeforeExec
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.AuthenticationErrorContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy;
import net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class CheckCredentialPolicy
extends AbstractWebAuthnAction<WebAuthnAuthenticationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(CheckCredentialPolicy.class);
    @NonnullBeforeExec
    private PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> assertion;
    @Nullable
    private CredentialPolicy credentialPolicy;
    @NonnullAfterInit
    private WebAuthnCredentialRepository repository;
    @NonnullBeforeExec
    private AuthenticationContext authnContext;

    protected CheckCredentialPolicy() {
        super(new ChildContextLookup(WebAuthnAuthenticationContext.class).compose((Function)new ChildContextLookup(AuthenticationContext.class)));
    }

    public void setCredentialPolicy(@Nullable CredentialPolicy policy) {
        this.checkSetterPreconditions();
        this.credentialPolicy = policy;
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        this.repository = this.getCredentialRepository();
        if (this.repository == null) {
            throw new ComponentInitializationException("Credential repository can not be null");
        }
    }

    @Override
    protected boolean doPreExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnAuthenticationContext context) {
        if (!super.doPreExecute(profileRequestContext, context)) {
            return false;
        }
        this.assertion = context.getPublicKeyCredentialAssertionResponse();
        if (this.assertion == null) {
            this.log.error("{} Assertion not available in registration context", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidProfileContext");
            return false;
        }
        this.authnContext = (AuthenticationContext)profileRequestContext.getSubcontext(AuthenticationContext.class);
        if (this.authnContext == null) {
            this.log.error("{} Authentication context not available", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidProfileContext");
            return false;
        }
        return true;
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnAuthenticationContext context) {
        CredentialPolicy localPolicy = this.credentialPolicy;
        if (localPolicy == null) {
            this.log.trace("{} No authenticator policy to apply", (Object)this.getLogPrefix());
            return;
        }
        Optional userHandle = ((AuthenticatorAssertionResponse)this.assertion.getResponse()).getUserHandle();
        if (userHandle.isEmpty()) {
            this.log.trace("{} UserHandle could not be found in the response", (Object)this.getLogPrefix());
            return;
        }
        Collection registeredCredentials = context.getExistingCredentials();
        Optional<EnhancedCredentialRecord> credential = registeredCredentials.stream().filter(cred -> {
            CredentialRecord credRecord = cred.getCredentialRecord();
            return this.assertion.getId().equals((Object)credRecord.getCredential().getCredentialId()) && ((ByteArray)userHandle.get()).equals((Object)credRecord.getUserIdentity().getId());
        }).findFirst();
        if (credential.isEmpty()) {
            this.log.trace("{} UserHandle '{}' has no registered credential", (Object)this.getLogPrefix(), (Object)((ByteArray)userHandle.get()).getHex());
            return;
        }
        EnhancedCredentialRecord credentialToEvaluate = credential.get();
        assert (credentialToEvaluate != null);
        CredentialPolicy.CredentialPolicyOutcome outcome = localPolicy.evaluate(credentialToEvaluate, profileRequestContext);
        if (outcome == CredentialPolicy.CredentialPolicyOutcome.REJECT) {
            this.log.warn("{} CredentialPolicy '{}' has rejected credential '{}'", new Object[]{this.getLogPrefix(), localPolicy.getId(), credential.get().getCredentialRecord().getCredentialIdBase64()});
            ((AuthenticationErrorContext)this.authnContext.ensureSubcontext(AuthenticationErrorContext.class)).getClassifiedErrors().add("CredentialPolicyRejection");
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"CredentialPolicyRejection");
            return;
        }
        if (outcome == CredentialPolicy.CredentialPolicyOutcome.IGNORE) {
            this.log.trace("{} CredentialPolicy '{}' was not active for credential '{}', accepting", new Object[]{this.getLogPrefix(), localPolicy.getId(), credential.get().getCredentialRecord().getCredentialIdBase64()});
            return;
        }
        this.log.debug("{} CredentialPolicy '{}' accepted credential '{}'", new Object[]{this.getLogPrefix(), localPolicy.getId(), credential.get().getCredentialRecord().getCredentialIdBase64()});
    }
}

