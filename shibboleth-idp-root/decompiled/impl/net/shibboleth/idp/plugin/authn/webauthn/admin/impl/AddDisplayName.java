/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
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

import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class AddDisplayName
extends AbstractWebAuthnAction<WebAuthnRegistrationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AddDisplayName.class);
    @NonnullAfterInit
    private Function<ProfileRequestContext, String> displayNameLookupStrategy;
    @NonnullBeforeExec
    private String username;

    protected AddDisplayName() {
        super(new ChildContextLookup(WebAuthnRegistrationContext.class));
    }

    public void setDisplayNameLookupStrategy(@Nonnull Function<ProfileRequestContext, String> strategy) {
        this.checkSetterPreconditions();
        this.displayNameLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"DisplayName lookup strategy cannot be null");
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (this.displayNameLookupStrategy == null) {
            throw new ComponentInitializationException("DisplayName lookup strategy can not be null");
        }
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
        String displayName = this.displayNameLookupStrategy.apply(profileRequestContext);
        if (displayName == null) {
            this.log.trace("{} DisplayName was null for user '{}'", (Object)this.getLogPrefix(), (Object)this.username);
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            return;
        }
        if (displayName.getBytes(StandardCharsets.UTF_8).length > 64) {
            this.log.trace("{} DisplayName exceeds 64 bytes and might get truncated by the authenticator", (Object)this.getLogPrefix());
        }
        this.log.debug("{} Populating DisplayName '{}'", (Object)this.getLogPrefix(), (Object)displayName);
        context.setDisplayName(displayName);
    }
}

