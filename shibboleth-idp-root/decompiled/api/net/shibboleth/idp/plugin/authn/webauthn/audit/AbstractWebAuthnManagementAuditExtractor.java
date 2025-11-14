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
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext;
import net.shibboleth.shared.logic.Constraint;
import org.opensaml.profile.context.ProfileRequestContext;

@ThreadSafe
public abstract class AbstractWebAuthnManagementAuditExtractor<T>
implements Function<ProfileRequestContext, T> {
    @Nonnull
    private final Function<ProfileRequestContext, WebAuthnManagementContext> webAuthnManagementContextLookupStrategy;

    protected AbstractWebAuthnManagementAuditExtractor(@Nonnull Function<ProfileRequestContext, WebAuthnManagementContext> strategy) {
        this.webAuthnManagementContextLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"WebAuthnManagementContext lookup strategy can not be null");
    }

    @Override
    @Nullable
    public T apply(@Nullable ProfileRequestContext profileRequestContext) {
        if (profileRequestContext == null) {
            return null;
        }
        WebAuthnManagementContext webAuthnContext = this.webAuthnManagementContextLookupStrategy.apply(profileRequestContext);
        if (webAuthnContext == null) {
            return null;
        }
        return this.doLookup(webAuthnContext);
    }

    @Nullable
    protected abstract T doLookup(@Nonnull WebAuthnManagementContext var1);
}

