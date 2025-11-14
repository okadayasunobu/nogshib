/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.authn.context.SubjectCanonicalizationContext
 *  net.shibboleth.idp.authn.principal.UsernamePrincipal
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import java.security.Principal;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.security.auth.Subject;
import net.shibboleth.idp.authn.context.SubjectCanonicalizationContext;
import net.shibboleth.idp.authn.principal.UsernamePrincipal;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class InitializeAdminSubjectCanonicalizationContext
extends AbstractWebAuthnAction<WebAuthnManagementContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(InitializeAdminSubjectCanonicalizationContext.class);
    @Nonnull
    private Function<ProfileRequestContext, SubjectCanonicalizationContext> scCtxLookupStrategy = new ChildContextLookup(SubjectCanonicalizationContext.class, true);

    protected InitializeAdminSubjectCanonicalizationContext() {
        super(new ChildContextLookup(WebAuthnManagementContext.class));
    }

    public void setLookupStrategy(@Nonnull Function<ProfileRequestContext, SubjectCanonicalizationContext> strategy) {
        this.checkSetterPreconditions();
        this.scCtxLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"Strategy cannot be null");
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnManagementContext context) {
        String username = context.getSearchUsername();
        if (username == null) {
            this.log.warn("{} Unable to find search username in WebAuthn management context", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"UnknownUsername");
            return;
        }
        SubjectCanonicalizationContext c14n = this.scCtxLookupStrategy.apply(profileRequestContext);
        Subject subject = new Subject();
        subject.getPrincipals().add((Principal)new UsernamePrincipal(username));
        c14n.setSubject(subject);
    }
}

