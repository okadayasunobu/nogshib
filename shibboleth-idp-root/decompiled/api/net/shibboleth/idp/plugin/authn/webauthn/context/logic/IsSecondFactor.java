/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.authn.context.MultiFactorAuthenticationContext
 *  net.shibboleth.idp.session.context.navigate.CanonicalUsernameLookupStrategy
 *  net.shibboleth.shared.annotation.constraint.NonnullElements
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.component.AbstractInitializableComponent
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.logic.PredicateSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.context.logic;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.MultiFactorAuthenticationContext;
import net.shibboleth.idp.session.context.navigate.CanonicalUsernameLookupStrategy;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class IsSecondFactor
extends AbstractInitializableComponent
implements Predicate<ProfileRequestContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(IsSecondFactor.class);
    @Nonnull
    private Predicate<ProfileRequestContext> secondFactorOverride = PredicateSupport.alwaysFalse();
    @Nonnull
    private Predicate<ProfileRequestContext> enabled = PredicateSupport.alwaysFalse();
    @Nonnull
    private Function<ProfileRequestContext, String> usernameLookupStrategy = new CanonicalUsernameLookupStrategy();
    @Nonnull
    @NonnullElements
    private Set<String> allowedPreviousFactors = CollectionSupport.emptySet();

    public void setUsernameLookupStrategy(@Nonnull Function<ProfileRequestContext, String> strategy) {
        this.checkSetterPreconditions();
        this.usernameLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"Username lookup strategy cannot be null");
    }

    public void setEnabled(@Nonnull Predicate<ProfileRequestContext> predicate) {
        this.checkSetterPreconditions();
        this.enabled = (Predicate)Constraint.isNotNull(predicate, (String)"Enabled predicate can not be null");
    }

    public void setEnabled(boolean flag) {
        this.checkSetterPreconditions();
        this.enabled = flag ? PredicateSupport.alwaysTrue() : PredicateSupport.alwaysFalse();
    }

    public void setSecondFactorOverride(@Nonnull Predicate<ProfileRequestContext> override) {
        this.checkSetterPreconditions();
        this.secondFactorOverride = (Predicate)Constraint.isNotNull(override, (String)"SecondFactorOverride predicate can not be null");
    }

    public void setSecondFactorOverride(boolean flag) {
        this.checkSetterPreconditions();
        this.secondFactorOverride = flag ? PredicateSupport.alwaysTrue() : PredicateSupport.alwaysFalse();
    }

    public synchronized void setAllowedPreviousFactors(@Nullable @NonnullElements Collection<String> factors) {
        this.checkSetterPreconditions();
        if (factors != null) {
            this.allowedPreviousFactors = CollectionSupport.copyToSet((Collection)StringSupport.normalizeStringCollection(factors));
        }
    }

    @Override
    public boolean test(@Nullable ProfileRequestContext input) {
        this.checkComponentActive();
        if (input == null) {
            this.log.trace("Profile context was null, assuming first factor usage");
            return false;
        }
        if (!this.enabled.test(input)) {
            this.log.trace("Use as a second factor authentication flow disabled, assuming first factor usage");
            return false;
        }
        if (this.secondFactorOverride.test(input)) {
            this.log.trace("Second factor authentication flow forced by configuration");
            return true;
        }
        AuthenticationContext authnContext = (AuthenticationContext)input.getSubcontext(AuthenticationContext.class);
        if (authnContext == null) {
            this.log.trace("Authentication context was null, assuming first factor usage");
            return false;
        }
        MultiFactorAuthenticationContext mfaContext = (MultiFactorAuthenticationContext)authnContext.getSubcontext(MultiFactorAuthenticationContext.class);
        if (mfaContext == null) {
            this.log.trace("No MFA context available, assuming first factor usage");
            return false;
        }
        String username = StringSupport.trimOrNull((String)this.usernameLookupStrategy.apply(input));
        this.log.trace("{}", username != null ? "Found principal name '" + username + "'" : "No previous principal name found");
        Optional<String> foundFactor = mfaContext.getActiveResults().keySet().stream().filter(this.allowedPreviousFactors::contains).findFirst();
        foundFactor.ifPresent(factor -> this.log.trace("Found acceptable previous factor '{}'", factor));
        if (username != null && foundFactor.isPresent()) {
            this.log.trace("Principal name '{}' found, and previous factor '{}' accepted, assuming second factor usage", (Object)username, (Object)foundFactor.get());
            return true;
        }
        this.log.trace("Request did not contain an acceptable previous factor, assuming first factor usage");
        return false;
    }
}

