/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.ByteArray
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.logic.PredicateSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import com.yubico.webauthn.data.ByteArray;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class LookupRegisteredCredentials
extends AbstractWebAuthnAction<BaseWebAuthnContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(LookupRegisteredCredentials.class);
    private Predicate<ProfileRequestContext> triggerEventOnNoCredentialsPredicate = PredicateSupport.alwaysFalse();
    @Nonnull
    @NotEmpty
    private String noCredentialsEventId = "NoRegisteredWebAuthnCredentials";
    @NonnullAfterInit
    private WebAuthnCredentialRepository repository;
    @Nonnull
    private Predicate<ProfileRequestContext> usernameRequiredPredicate = PredicateSupport.alwaysTrue();

    public LookupRegisteredCredentials() {
        super(new ChildContextLookup(BaseWebAuthnContext.class).compose((Function)new ChildContextLookup(AuthenticationContext.class)));
    }

    public void setUsernameRequired(boolean flag) {
        this.checkSetterPreconditions();
        this.usernameRequiredPredicate = flag ? PredicateSupport.alwaysTrue() : PredicateSupport.alwaysFalse();
    }

    public void setUsernameRequiredPredicate(@Nonnull Predicate<ProfileRequestContext> predicate) {
        this.checkSetterPreconditions();
        this.usernameRequiredPredicate = (Predicate)Constraint.isNotNull(predicate, (String)"Username required predicate can not be null");
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        this.repository = this.getCredentialRepository();
        if (this.repository == null) {
            throw new ComponentInitializationException("Credential repository can not be null");
        }
    }

    public void setTriggerEventOnNoCredentials(boolean trigger) {
        this.checkSetterPreconditions();
        this.triggerEventOnNoCredentialsPredicate = trigger ? PredicateSupport.alwaysTrue() : PredicateSupport.alwaysFalse();
    }

    public void setTriggerEventOnNoCredentialsPredicate(@Nonnull Predicate<ProfileRequestContext> predicate) {
        this.checkSetterPreconditions();
        this.triggerEventOnNoCredentialsPredicate = (Predicate)Constraint.isNotNull(predicate, (String)"TriggerEventOnNoCredentialsPredicate can not be null");
    }

    public void setNoCredentialsEventId(@Nonnull @NotEmpty String eventId) {
        this.checkSetterPreconditions();
        this.noCredentialsEventId = Constraint.isNotEmpty((String)eventId, (String)"NoCredentialsEventId can not be null or empty");
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull BaseWebAuthnContext context) {
        String username = context.getUsername();
        if (username == null && !this.usernameRequiredPredicate.test(profileRequestContext)) {
            this.log.error("{} Unable to find username in WebAuthn context, no credentials found", (Object)this.getLogPrefix());
            return;
        }
        if (username == null && this.usernameRequiredPredicate.test(profileRequestContext)) {
            this.log.error("{} Unable to find username in WebAuthn context", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidProfileContext");
            return;
        }
        assert (username != null);
        Set credentials = this.repository.getRegistrationsByUsername(username);
        this.log.debug("{} Found '{}' registered credentials for '{}'", new Object[]{this.getLogPrefix(), credentials.size(), username});
        context.setExistingCredentials(this.enhancedCredentialRecord(credentials));
        Optional userHandle = this.repository.getUserHandleForUsername(username);
        if (userHandle.isPresent()) {
            byte[] userId = ((ByteArray)userHandle.get()).getBytes();
            assert (userId != null);
            context.setUserId(userId);
        }
        if (this.triggerEventOnNoCredentialsPredicate.test(profileRequestContext) && credentials.isEmpty()) {
            this.log.debug("{} Triggering event '{}' ", (Object)this.getLogPrefix(), (Object)this.noCredentialsEventId);
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)this.noCredentialsEventId);
            return;
        }
    }
}

