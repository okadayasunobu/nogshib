/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.ByteArray
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import com.yubico.webauthn.data.ByteArray;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.audit.impl.AbstractWebAuthnAuditingAction;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.WebAuthnSupport;
import net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord;
import net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class DeletePublicKeyCredential
extends AbstractWebAuthnAuditingAction<WebAuthnRegistrationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(DeletePublicKeyCredential.class);
    @NonnullAfterInit
    private WebAuthnCredentialRepository repository;

    protected DeletePublicKeyCredential() {
        super(new ChildContextLookup(WebAuthnRegistrationContext.class));
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
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnRegistrationContext context) {
        String username = context.getUsername();
        if (username == null) {
            this.log.error("{} Unable to find username in registration context", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistrationContext");
            return;
        }
        byte[] credentialId = context.getCredentialIdToRemove();
        if (credentialId == null) {
            this.log.error("{} Unable to find credentialId in registration context", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistrationContext");
            return;
        }
        Optional credential = this.repository.getRegistrationByUsernameAndCredentialId(username, new ByteArray(credentialId));
        if (credential.isEmpty()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("{} Unable to find credential '{}' to remove for user '{}', nothing to remove", new Object[]{this.getLogPrefix(), WebAuthnSupport.toBase64OrUnknown(credentialId), username});
            }
        } else {
            boolean removed = this.repository.removeRegistrationByUsername(username, (CredentialRecord)credential.get());
            this.log.info("{} Credential '{}' {} removed for user '{}'", new Object[]{this.getLogPrefix(), ((CredentialRecord)credential.get()).getCredentialIdBase64(), removed ? "was" : "was not", username});
            if (removed) {
                this.auditSuccess(profileRequestContext, "credential-removed");
            } else {
                this.auditFailure(profileRequestContext, "credential-removed");
            }
        }
        context.setCredentialIdToRemove(null);
    }
}

