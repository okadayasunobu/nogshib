/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.ByteArray
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext
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
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.audit.impl.AbstractWebAuthnAuditingAction;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.WebAuthnSupport;
import net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class AdminDeletePublicKeyCredential
extends AbstractWebAuthnAuditingAction<WebAuthnManagementContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AdminDeletePublicKeyCredential.class);
    @NonnullAfterInit
    private WebAuthnCredentialRepository repository;

    public AdminDeletePublicKeyCredential() {
        super(new ChildContextLookup(WebAuthnManagementContext.class));
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
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnManagementContext context) {
        byte[] credentialId = context.getCredentialIdToRemove();
        if (credentialId == null) {
            this.log.error("{} Unable to find credentialId in management context", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidManagmentContext");
            return;
        }
        String credentialUsername = context.getSearchUsername();
        if (credentialUsername == null) {
            this.log.error("{} Unable to find username to remove credential from in management context", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidManagmentContext");
            return;
        }
        boolean removed = this.repository.removeRegistrationByUsernameAndCredentialId(credentialUsername, new ByteArray(credentialId));
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} Credential '{}' {} removed", new Object[]{this.getLogPrefix(), WebAuthnSupport.toBase64OrUnknown(credentialId), removed ? "was" : "was not"});
        }
        if (removed) {
            this.auditSuccess(profileRequestContext, "credential-removed");
        } else {
            this.auditFailure(profileRequestContext, "credential-removed");
        }
        context.setCredentialIdToRemove(null);
    }
}

