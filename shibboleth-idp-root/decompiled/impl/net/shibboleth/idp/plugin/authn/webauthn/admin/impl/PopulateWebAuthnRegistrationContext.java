/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationErrorContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationInformationContext
 *  net.shibboleth.idp.profile.AbstractProfileAction
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.logic.FunctionSupport
 *  net.shibboleth.shared.logic.PredicateSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.BaseContext
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationErrorContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationInformationContext;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.FunctionSupport;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class PopulateWebAuthnRegistrationContext
extends AbstractProfileAction {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(PopulateWebAuthnRegistrationContext.class);
    @Nonnull
    private Function<ProfileRequestContext, WebAuthnRegistrationContext> webAuthnRegistrationContextCreationStrategy = new ChildContextLookup(WebAuthnRegistrationContext.class, true);
    @Nonnull
    private Function<ProfileRequestContext, String> usernameLookupStrategy;
    @Nonnull
    private Predicate<ProfileRequestContext> usernameRequiredPredicate = PredicateSupport.alwaysFalse();
    @Nonnull
    private Predicate<ProfileRequestContext> removeExistingRegistrationContext;

    public PopulateWebAuthnRegistrationContext() {
        this.usernameLookupStrategy = FunctionSupport.constant(null);
        this.removeExistingRegistrationContext = PredicateSupport.alwaysFalse();
    }

    public void setUsernameRequired(boolean flag) {
        this.checkSetterPreconditions();
        this.usernameRequiredPredicate = flag ? PredicateSupport.alwaysTrue() : PredicateSupport.alwaysFalse();
    }

    public void setUsernameRequiredPredicate(@Nonnull Predicate<ProfileRequestContext> predicate) {
        this.checkSetterPreconditions();
        this.usernameRequiredPredicate = (Predicate)Constraint.isNotNull(predicate, (String)"Username required predicate can not be null");
    }

    public void setRemoveExistingRegistrationContext(boolean flag) {
        this.checkSetterPreconditions();
        this.removeExistingRegistrationContext = flag ? PredicateSupport.alwaysTrue() : PredicateSupport.alwaysFalse();
    }

    public void setRemoveExistingRegistrationContextPredicate(@Nonnull Predicate<ProfileRequestContext> predicate) {
        this.checkSetterPreconditions();
        this.removeExistingRegistrationContext = (Predicate)Constraint.isNotNull(predicate, (String)"RemoveExistingRegistrationContext predicate can not be null");
    }

    public void setWebAuthnRegistrationContextCreationStrategy(@Nonnull Function<ProfileRequestContext, WebAuthnRegistrationContext> strategy) {
        this.checkSetterPreconditions();
        this.webAuthnRegistrationContextCreationStrategy = (Function)Constraint.isNotNull(strategy, (String)"WebAuthnRegistrationContextCreationStrategy can not be null");
    }

    public void setUsernameLookupStrategy(@Nonnull Function<ProfileRequestContext, String> strategy) {
        this.checkSetterPreconditions();
        this.usernameLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"Username lookup strategy cannot be null");
    }

    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext) {
        WebAuthnRegistrationContext context = null;
        if (this.removeExistingRegistrationContext.test(profileRequestContext)) {
            WebAuthnRegistrationContext existingContext = this.webAuthnRegistrationContextCreationStrategy.apply(profileRequestContext);
            if (existingContext != null) {
                existingContext.removeFromParent();
                context = this.webAuthnRegistrationContextCreationStrategy.apply(profileRequestContext);
                WebAuthnRegistrationErrorContext oldErrCtx = (WebAuthnRegistrationErrorContext)existingContext.getSubcontext(WebAuthnRegistrationErrorContext.class);
                WebAuthnRegistrationInformationContext oldInfoCtx = (WebAuthnRegistrationInformationContext)existingContext.getSubcontext(WebAuthnRegistrationInformationContext.class);
                if (oldErrCtx != null) {
                    oldErrCtx.removeFromParent();
                    context.addSubcontext((BaseContext)oldErrCtx);
                }
                if (oldInfoCtx != null) {
                    oldInfoCtx.removeFromParent();
                    context.addSubcontext((BaseContext)oldInfoCtx);
                }
            }
        } else {
            context = this.webAuthnRegistrationContextCreationStrategy.apply(profileRequestContext);
        }
        if (context == null) {
            this.log.error("{} Error creating WebAuthnRegistrationContext", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidProfileContext");
            return;
        }
        String username = this.usernameLookupStrategy.apply(profileRequestContext);
        if (this.usernameRequiredPredicate.test(profileRequestContext) && username == null) {
            this.log.error("{} Error creating WebAuthnRegistrationContext, no username found", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidProfileContext");
            return;
        }
        if (username != null) {
            context.setUsername(username);
            this.log.debug("{} Created WebAuthn registration context for user '{}'", (Object)this.getLogPrefix(), (Object)context.getUsername());
        } else {
            this.log.debug("{} Created WebAuthn registration context", (Object)this.getLogPrefix());
        }
    }
}

