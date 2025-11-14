/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.ByteArray
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.annotation.constraint.NonnullBeforeExec
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import com.yubico.webauthn.data.ByteArray;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.admin.impl.RandomUserIdGenerator;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.idp.plugin.authn.webauthn.impl.WebAuthnSupport;
import net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class AddUserId
extends AbstractWebAuthnAction<WebAuthnRegistrationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AddUserId.class);
    @NonnullAfterInit
    private Function<ProfileRequestContext, byte[]> userIdGeneratorStrategy;
    @NonnullBeforeExec
    private String username;
    @NonnullAfterInit
    private WebAuthnCredentialRepository repository;

    public AddUserId() {
        super(new ChildContextLookup(WebAuthnRegistrationContext.class));
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        this.repository = this.getCredentialRepository();
        if (this.repository == null) {
            throw new ComponentInitializationException("Credential repository can not be null");
        }
        if (this.userIdGeneratorStrategy == null) {
            this.userIdGeneratorStrategy = new RandomUserIdGenerator();
        }
    }

    public void setUserIdGeneratorStrategy(@Nonnull Function<ProfileRequestContext, byte[]> strategy) {
        this.checkSetterPreconditions();
        this.userIdGeneratorStrategy = (Function)Constraint.isNotNull(strategy, (String)"Challenge Generator cannot be null");
    }

    @Override
    protected boolean doPreExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnRegistrationContext context) {
        if (!super.doPreExecute(profileRequestContext, context)) {
            return false;
        }
        this.username = context.getUsername();
        if (this.username == null) {
            this.log.error("{} Username not available in registration context", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistrationContext");
            return false;
        }
        return true;
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnRegistrationContext context) {
        Optional existingUserHandle = this.repository.getUserHandleForUsername(this.username);
        if (existingUserHandle.isPresent()) {
            byte[] handleAsBytes = ((ByteArray)existingUserHandle.get()).getBytes();
            assert (handleAsBytes != null);
            if (this.log.isDebugEnabled()) {
                this.log.debug("{} Found existing user.id '{}'", (Object)this.getLogPrefix(), (Object)WebAuthnSupport.toBase64OrUnknown(handleAsBytes));
            }
            context.setUserId(handleAsBytes);
            return;
        }
        byte[] userId = this.userIdGeneratorStrategy.apply(profileRequestContext);
        if (userId == null || userId.length == 0) {
            this.log.warn("{} Generated user.id was empty or null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            return;
        }
        if (userId.length > 64) {
            this.log.warn("{} User.id is larger than 64 bytes", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("{} Generated user.id '{}' of size '{}'", new Object[]{this.getLogPrefix(), WebAuthnSupport.toBase64OrUnknown(userId), userId.length});
        }
        context.setUserId(userId);
    }
}

