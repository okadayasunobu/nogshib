/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.authn.AbstractAuthenticationAction
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  net.shibboleth.idp.session.context.navigate.CanonicalUsernameLookupStrategy
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.logic.PredicateSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.shibboleth.idp.authn.AbstractAuthenticationAction;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.session.context.navigate.CanonicalUsernameLookupStrategy;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class PopulateWebAuthnAuthenticationContext
extends AbstractAuthenticationAction {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(PopulateWebAuthnAuthenticationContext.class);
    @Nonnull
    private final Function<ProfileRequestContext, WebAuthnAuthenticationContext> webauthnAuthContextCreationStrategy = new ChildContextLookup(WebAuthnAuthenticationContext.class, true).compose((Function)new ChildContextLookup(AuthenticationContext.class));
    @Nonnull
    private Function<ProfileRequestContext, String> usernameLookupStrategy = new CanonicalUsernameLookupStrategy();
    @Nonnull
    private Predicate<ProfileRequestContext> usernameRequiredPredicate = PredicateSupport.alwaysFalse();
    @Nonnull
    private Consumer<ProfileRequestContext> contextUpdateConsumer = prc -> {};

    public void setContextUpdateConsumer(@Nonnull Consumer<ProfileRequestContext> consumer) {
        this.checkSetterPreconditions();
        this.contextUpdateConsumer = (Consumer)Constraint.isNotNull(consumer, (String)"ContextUpdateConsumer can not be null");
    }

    public void setUsernameRequired(boolean flag) {
        this.checkSetterPreconditions();
        this.usernameRequiredPredicate = flag ? PredicateSupport.alwaysTrue() : PredicateSupport.alwaysFalse();
    }

    public void setUsernameRequiredPredicate(@Nonnull Predicate<ProfileRequestContext> predicate) {
        this.checkSetterPreconditions();
        this.usernameRequiredPredicate = (Predicate)Constraint.isNotNull(predicate, (String)"Username required predicate can not be null");
    }

    public void setUsernameLookupStrategy(@Nonnull Function<ProfileRequestContext, String> strategy) {
        this.checkSetterPreconditions();
        this.usernameLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"Username lookup strategy cannot be null");
    }

    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull AuthenticationContext authenticationContext) {
        WebAuthnAuthenticationContext context = this.webauthnAuthContextCreationStrategy.apply(profileRequestContext);
        if (context == null) {
            this.log.error("{} Error creating WebAuthnAuthenticationContext", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidProfileContext");
            return;
        }
        String username = this.usernameLookupStrategy.apply(profileRequestContext);
        if (this.usernameRequiredPredicate.test(profileRequestContext) && username == null) {
            this.log.error("{} Error creating WebauthnAuthenticationContext, no username found", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidProfileContext");
            return;
        }
        if (username != null) {
            context.setUsername(username);
        }
        this.contextUpdateConsumer.accept(profileRequestContext);
        this.log.trace("Created WebAuthn authentication context {}", username != null ? "for user '" + username + "'" : "without existing username");
    }
}

