/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.context.InlineEnrolmentContext
 *  net.shibboleth.shared.component.AbstractInitializableComponent
 *  net.shibboleth.shared.logic.Constraint
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.springframework.webflow.execution.RequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import java.net.URL;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.context.InlineEnrolmentContext;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.webflow.execution.RequestContext;

public class InlineEnrolmentRedirectFunction
extends AbstractInitializableComponent
implements BiFunction<RequestContext, ProfileRequestContext, String> {
    @Nonnull
    private Function<ProfileRequestContext, InlineEnrolmentContext> inlineEnrolementContextCreationStrategy = new ChildContextLookup(InlineEnrolmentContext.class, false);

    public void setInlineEnrolementContextCreationStrategy(@Nonnull Function<ProfileRequestContext, InlineEnrolmentContext> strategy) {
        this.checkSetterPreconditions();
        this.inlineEnrolementContextCreationStrategy = (Function)Constraint.isNotNull(strategy, (String)"InlineEnrolementContextCreationStrategy can not be null");
    }

    @Override
    @Nullable
    public String apply(@Nullable RequestContext springRequestContext, @Nullable ProfileRequestContext profileRequestContext) {
        InlineEnrolmentContext context = this.inlineEnrolementContextCreationStrategy.apply(profileRequestContext);
        if (context == null) {
            throw new IllegalArgumentException("InlineEnrolmentContext can not be null");
        }
        URL ssoUrl = context.getSsoUrl();
        if (ssoUrl != null) {
            StringBuilder builder = new StringBuilder(ssoUrl.getPath());
            builder.append("?");
            builder.append(ssoUrl.getQuery());
            return builder.toString();
        }
        throw new IllegalArgumentException("SSO URL cannot be null");
    }
}

