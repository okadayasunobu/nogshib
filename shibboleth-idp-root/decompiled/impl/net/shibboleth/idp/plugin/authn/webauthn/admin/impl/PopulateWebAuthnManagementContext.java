/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext
 *  net.shibboleth.idp.profile.AbstractProfileAction
 *  net.shibboleth.idp.session.context.navigate.CanonicalUsernameLookupStrategy
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
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.idp.session.context.navigate.CanonicalUsernameLookupStrategy;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class PopulateWebAuthnManagementContext
extends AbstractProfileAction {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(PopulateWebAuthnManagementContext.class);
    @Nonnull
    private Function<ProfileRequestContext, WebAuthnManagementContext> webAuthnManagementContextCreationStrategy = new ChildContextLookup(WebAuthnManagementContext.class, true);
    @Nonnull
    private Function<ProfileRequestContext, String> principalNameLookupStrategy = new CanonicalUsernameLookupStrategy();

    public void setWebAuthnManagementContextCreationStrategy(@Nonnull Function<ProfileRequestContext, WebAuthnManagementContext> strategy) {
        this.checkSetterPreconditions();
        this.webAuthnManagementContextCreationStrategy = (Function)Constraint.isNotNull(strategy, (String)"WebAuthnManagementContextCreationStrategy can not be null");
    }

    public void setPrincipalNameLookupStrategy(@Nonnull Function<ProfileRequestContext, String> strategy) {
        this.checkSetterPreconditions();
        this.principalNameLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"Principal name lookup strategy cannot be null");
    }

    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext) {
        WebAuthnManagementContext context = this.webAuthnManagementContextCreationStrategy.apply(profileRequestContext);
        if (context == null) {
            this.log.error("{} Error creating WebAuthnManagementContext", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidProfileContext");
            return;
        }
        String principalName = this.principalNameLookupStrategy.apply(profileRequestContext);
        if (principalName == null) {
            this.log.error("{} Error creating WebAuthnManagementContext, no principal name found", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidProfileContext");
            return;
        }
        context.setPrincipalName(principalName);
        this.log.debug("Created WebAuthn management context for '{}'", (Object)context.getPrincipalName());
    }
}

