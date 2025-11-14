/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent
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
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class IsAdminUsernameCollectionEnabled
extends AbstractIdentifiableInitializableComponent
implements Predicate<ProfileRequestContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(IsAdminUsernameCollectionEnabled.class);
    private Predicate<ProfileRequestContext> usernameCollectionRequiredPredicate;

    public void setUsernameCollectionRequiredPredicate(Predicate<ProfileRequestContext> predicate) {
        this.checkSetterPreconditions();
        this.usernameCollectionRequiredPredicate = (Predicate)Constraint.isNotNull(this.usernameCollectionRequiredPredicate, (String)"usernameCollectionRequiredPredicate can not be null");
    }

    public void setUsernameCollectionRequired(boolean flag) {
        this.checkSetterPreconditions();
        this.usernameCollectionRequiredPredicate = flag ? PredicateSupport.alwaysTrue() : PredicateSupport.alwaysFalse();
    }

    @Override
    public boolean test(@Nullable ProfileRequestContext input) {
        boolean usernameCollectionRequired = this.usernameCollectionRequiredPredicate.test(input);
        this.log.trace("{}: Username collection was {}'", (Object)this.getId(), (Object)(usernameCollectionRequired ? "required" : "not required"));
        return usernameCollectionRequired;
    }
}

