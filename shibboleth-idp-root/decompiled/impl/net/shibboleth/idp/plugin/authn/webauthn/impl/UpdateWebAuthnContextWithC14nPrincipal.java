/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.authn.context.SubjectCanonicalizationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.authn.context.SubjectCanonicalizationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class UpdateWebAuthnContextWithC14nPrincipal
extends AbstractWebAuthnAction<BaseWebAuthnContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(UpdateWebAuthnContextWithC14nPrincipal.class);
    @Nonnull
    private Function<ProfileRequestContext, SubjectCanonicalizationContext> scCtxLookupStrategy = new ChildContextLookup(SubjectCanonicalizationContext.class, false);

    protected UpdateWebAuthnContextWithC14nPrincipal() {
        super(new ChildContextLookup(BaseWebAuthnContext.class));
    }

    public void setLookupStrategy(@Nonnull Function<ProfileRequestContext, SubjectCanonicalizationContext> strategy) {
        this.checkSetterPreconditions();
        this.scCtxLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"Strategy cannot be null");
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull BaseWebAuthnContext context) {
        SubjectCanonicalizationContext c14n = this.scCtxLookupStrategy.apply(profileRequestContext);
        if (c14n == null) {
            this.log.warn("{} Unable to find subject canonicalization context", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"UnknownUsername");
            return;
        }
        context.setUsername(c14n.getPrincipalName());
        c14n.removeFromParent();
        this.log.debug("{} Updated WebAuthn context with username '{}' from the subject canonicalization context", (Object)this.getLogPrefix(), (Object)c14n.getPrincipalName());
    }
}

