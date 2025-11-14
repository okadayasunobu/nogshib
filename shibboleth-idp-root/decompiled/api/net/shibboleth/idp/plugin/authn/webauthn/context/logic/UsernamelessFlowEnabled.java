/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.shared.component.AbstractInitializableComponent
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.logic.PredicateSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.context.logic;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class UsernamelessFlowEnabled
extends AbstractInitializableComponent
implements Predicate<ProfileRequestContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(UsernamelessFlowEnabled.class);
    @Nonnull
    private Predicate<ProfileRequestContext> enabled = PredicateSupport.alwaysFalse();

    public void setEnabled(@Nonnull Predicate<ProfileRequestContext> predicate) {
        this.checkSetterPreconditions();
        this.enabled = (Predicate)Constraint.isNotNull(predicate, (String)"Enabled predicate can not be null");
    }

    public void setEnabled(boolean flag) {
        this.checkSetterPreconditions();
        this.enabled = flag ? PredicateSupport.alwaysTrue() : PredicateSupport.alwaysFalse();
    }

    @Override
    public boolean test(@Nullable ProfileRequestContext input) {
        if (input == null) {
            this.log.trace("Profile context was null, assuming username is required");
            return false;
        }
        boolean usernamelessEnabled = this.enabled.test(input);
        this.log.trace("{}", (Object)(usernamelessEnabled ? "Usernameless authentication flow initiated" : "Passwordless authentication flow initiated"));
        return usernamelessEnabled;
    }
}

