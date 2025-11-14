/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.authn.context.SubjectContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.collection.Pair
 *  net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.SubjectContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class AllowCurrentUserAccessPredicate
extends AbstractIdentifiableInitializableComponent
implements Predicate<ProfileRequestContext> {
    @Nonnull
    @NotEmpty
    private final Logger log = LoggerFactory.getLogger(AllowCurrentUserAccessPredicate.class);
    @Nonnull
    private Function<ProfileRequestContext, WebAuthnRegistrationContext> webauthnRegistrationContextLookupStrategy = new ChildContextLookup(WebAuthnRegistrationContext.class);
    @Nonnull
    private Function<ProfileRequestContext, WebAuthnAuthenticationContext> webauthnContextLookupStrategy = new ChildContextLookup(WebAuthnAuthenticationContext.class).compose((Function)new ChildContextLookup(AuthenticationContext.class));
    @Nonnull
    private Function<ProfileRequestContext, SubjectContext> subjectContextLookupStrategy = new ChildContextLookup(SubjectContext.class);
    @Nonnull
    private BiPredicate<ProfileRequestContext, Pair<String, String>> comparisonPredicate = new DefaultCurrentUserComparisonPredicate();

    public void setWebauthnContextLookupStrategy(@Nonnull Function<ProfileRequestContext, WebAuthnAuthenticationContext> strategy) {
        this.checkSetterPreconditions();
        this.webauthnContextLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"WebAuthnContextLookuplookup strategy cannot be null");
    }

    public void setSubjectContextLookupStrategy(@Nonnull Function<ProfileRequestContext, SubjectContext> strategy) {
        this.checkSetterPreconditions();
        this.subjectContextLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"SubjectContext lookup strategy cannot be null");
    }

    public void setComparisonPredicate(@Nonnull BiPredicate<ProfileRequestContext, Pair<String, String>> predicate) {
        this.checkSetterPreconditions();
        this.comparisonPredicate = (BiPredicate)Constraint.isNotNull(predicate, (String)"ComparisonPredicate can not be null");
    }

    public void setWebauthnRegistrationContextLookupStrategy(@Nonnull Function<ProfileRequestContext, WebAuthnRegistrationContext> strategy) {
        this.checkSetterPreconditions();
        this.webauthnRegistrationContextLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"WebauthnContextLookuplookup strategy cannot be null");
    }

    @Override
    public boolean test(ProfileRequestContext profileRequestContext) {
        WebAuthnRegistrationContext regContext = this.webauthnRegistrationContextLookupStrategy.apply(profileRequestContext);
        WebAuthnAuthenticationContext webAuthnContext = this.webauthnContextLookupStrategy.apply(profileRequestContext);
        if (regContext == null && webAuthnContext == null) {
            this.log.debug("{}: Registration or authentication context not found, access requires either a registration or authentication context", (Object)this.getId());
            return false;
        }
        String usernameFromContext = regContext != null ? regContext.getUsername() : webAuthnContext.getUsername();
        SubjectContext subjectContext = this.subjectContextLookupStrategy.apply(profileRequestContext);
        if (subjectContext == null) {
            this.log.debug("{}: No subject context found, access requires authentication.", (Object)this.getId());
            return false;
        }
        String usernameFromSubjectContext = subjectContext.getPrincipalName();
        return this.comparisonPredicate.test(profileRequestContext, (Pair<String, String>)new Pair((Object)usernameFromSubjectContext, (Object)usernameFromContext));
    }

    public static class DefaultCurrentUserComparisonPredicate
    implements BiPredicate<ProfileRequestContext, Pair<String, String>> {
        @Nonnull
        @NotEmpty
        private final Logger log = LoggerFactory.getLogger(DefaultCurrentUserComparisonPredicate.class);

        @Override
        public boolean test(@Nullable ProfileRequestContext profileRequestContext, @Nullable Pair<String, String> usernamePair) {
            if (profileRequestContext == null || usernamePair == null) {
                this.log.debug("Required context and username information not found, denying access");
                return false;
            }
            String usernameFromSubjectContext = (String)usernamePair.getFirst();
            String usernameFromWebAuthnContext = (String)usernamePair.getSecond();
            if (usernameFromWebAuthnContext == null) {
                this.log.debug("No username in WebAuthn context, granting access");
                return true;
            }
            if (usernameFromSubjectContext == null) {
                this.log.debug("No username in subject context, access requires authentication");
                return false;
            }
            boolean match = usernameFromSubjectContext.equals(usernameFromWebAuthnContext);
            this.log.debug("Username in WebAuthn context '{}' {} with the authenticated principal '{}'", new Object[]{usernameFromWebAuthnContext, match ? "matched" : "did not match", usernameFromSubjectContext});
            return match;
        }
    }
}

