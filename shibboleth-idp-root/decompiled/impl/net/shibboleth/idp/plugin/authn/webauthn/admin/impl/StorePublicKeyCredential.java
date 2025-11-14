/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.RegisteredCredential
 *  com.yubico.webauthn.data.ByteArray
 *  com.yubico.webauthn.data.UserIdentity
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.RegistrationResult
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

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.UserIdentity;
import java.time.Instant;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.admin.RegistrationResult;
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

public class StorePublicKeyCredential
extends AbstractWebAuthnAuditingAction<WebAuthnRegistrationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(StorePublicKeyCredential.class);
    @NonnullAfterInit
    private WebAuthnCredentialRepository repository;

    protected StorePublicKeyCredential() {
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
            this.log.error("Unable to find username in registration context");
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            return;
        }
        String name = context.getName();
        if (name == null) {
            this.log.error("Unable to find user.name in registration context");
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            return;
        }
        String displayName = context.getDisplayName();
        if (displayName == null) {
            this.log.error("Unable to find displayName in registration context");
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            return;
        }
        RegistrationResult registrationResult = context.getRegistrationResult();
        if (registrationResult == null) {
            this.log.error("Unable to find registration result in registration context");
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            return;
        }
        try {
            RegisteredCredential credential = RegisteredCredential.builder().credentialId(registrationResult.getKeyId().getId()).userHandle(new ByteArray(context.getUserId())).publicKeyCose(registrationResult.getPublicKeyCose()).build();
            UserIdentity user = UserIdentity.builder().name(name).displayName(displayName).id(new ByteArray(context.getUserId())).build();
            assert (user != null);
            assert (credential != null);
            Instant now = Instant.now();
            assert (now != null);
            Optional isDiscoverable = registrationResult.isDiscoverable();
            ByteArray aaguid = registrationResult.getAaguid();
            CredentialRecord registration = CredentialRecord.builder().withUserIdentity(user).withUsername(username).withTransports((SortedSet)registrationResult.getKeyId().getTransports().orElse(new TreeSet())).withRegistrationTime(now).withCredential(credential).withAaguid(aaguid != null ? aaguid.getBytes() : null).withCredentialNickname(context.getCredentialNickname()).withDiscoverable(isDiscoverable).withUserVerified(registrationResult.isUserVerified()).build();
            boolean added = this.repository.addRegistrationByUsername(username, registration);
            if (!added) {
                this.log.error("{} Unable to store registration for key '{}'", (Object)this.getLogPrefix(), (Object)registrationResult.getKeyId().getId().getBase64Url());
                ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
                this.auditFailure(profileRequestContext, "credential-added");
                return;
            }
            this.logRegistration(context, username, registrationResult);
            this.auditSuccess(profileRequestContext, "credential-added");
        }
        catch (Exception e) {
            this.log.error("{} Unable to store registration for key '{}'", new Object[]{this.getLogPrefix(), registrationResult.getKeyId().getId().getBase64Url(), e});
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            this.auditFailure(profileRequestContext, "credential-added");
            return;
        }
    }

    private void logRegistration(@Nonnull WebAuthnRegistrationContext context, @Nullable String username, @Nonnull RegistrationResult registrationResult) {
        if (this.log.isInfoEnabled()) {
            byte[] userId = context.getUserId();
            String userIdBase64 = userId != null ? WebAuthnSupport.toBase64OrUnknown(userId) : null;
            this.log.info("{} Added public key credential registration for user '{}' with user.id '{}' and key '{}'. Using a discoverable credential '{}' and user verification '{}'", new Object[]{this.getLogPrefix(), username, userIdBase64, registrationResult.getKeyId().getId().getBase64(), registrationResult.isDiscoverable().isPresent() ? registrationResult.isDiscoverable() : "unknown", registrationResult.isUserVerified()});
        }
    }
}

