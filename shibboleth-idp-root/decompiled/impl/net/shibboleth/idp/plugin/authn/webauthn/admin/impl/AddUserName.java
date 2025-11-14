/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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

import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
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

public class AddUserName
extends AbstractWebAuthnAction<WebAuthnRegistrationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AddUserName.class);
    @NonnullAfterInit
    private Function<ProfileRequestContext, String> userNameLookupStrategy;
    @NonnullBeforeExec
    private String username;
    @NonnullAfterInit
    private WebAuthnCredentialRepository repository;

    public AddUserName() {
        super(new ChildContextLookup(WebAuthnRegistrationContext.class));
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        this.repository = this.getCredentialRepository();
        if (this.repository == null) {
            throw new ComponentInitializationException("Credential repository can not be null");
        }
        if (this.userNameLookupStrategy == null) {
            throw new ComponentInitializationException("User.name lookup strategy can not be null");
        }
    }

    public void setUserNameLookupStrategy(@Nonnull Function<ProfileRequestContext, String> strategy) {
        this.checkSetterPreconditions();
        this.userNameLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"Username lookup strategy cannot be null");
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
        String webAuthnUserName = this.userNameLookupStrategy.apply(profileRequestContext);
        if (webAuthnUserName == null) {
            this.log.trace("{} User.name was null for user '{}'", (Object)this.getLogPrefix(), (Object)this.username);
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            return;
        }
        this.log.debug("{} Populating User.name '{}' for user '{}'", new Object[]{this.getLogPrefix(), webAuthnUserName, this.username});
        context.setName(webAuthnUserName);
    }
}

