/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.shared.logic.Constraint
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.audit;

import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext;
import net.shibboleth.shared.logic.Constraint;
import org.opensaml.profile.context.ProfileRequestContext;

@ThreadSafe
public abstract class AbstractWebAuthnAuditExtractor<T>
implements Function<ProfileRequestContext, T> {
    @Nonnull
    private final Function<ProfileRequestContext, BaseWebAuthnContext> webAuthnBaseContextLookupStrategy;

    protected AbstractWebAuthnAuditExtractor(@Nonnull Function<ProfileRequestContext, BaseWebAuthnContext> strategy) {
        this.webAuthnBaseContextLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"BaseWebAuthnContext lookup strategy can not be null");
    }

    @Override
    @Nullable
    public T apply(@Nullable ProfileRequestContext profileRequestContext) {
        if (profileRequestContext == null) {
            return null;
        }
        BaseWebAuthnContext webAuthnContext = this.webAuthnBaseContextLookupStrategy.apply(profileRequestContext);
        if (webAuthnContext == null) {
            return null;
        }
        return this.doLookup(webAuthnContext);
    }

    @Nullable
    protected abstract T doLookup(@Nonnull BaseWebAuthnContext var1);
}

