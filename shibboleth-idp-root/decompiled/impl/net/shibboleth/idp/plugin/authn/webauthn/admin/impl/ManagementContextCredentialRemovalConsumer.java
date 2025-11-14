/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class ManagementContextCredentialRemovalConsumer
extends AbstractIdentifiableInitializableComponent
implements BiConsumer<ProfileRequestContext, byte[]> {
    @Nonnull
    @NotEmpty
    private final Logger log = LoggerFactory.getLogger(ManagementContextCredentialRemovalConsumer.class);
    @Nonnull
    private Function<ProfileRequestContext, WebAuthnManagementContext> webauthnManagementContextLookupStrategy = new ChildContextLookup(WebAuthnManagementContext.class);

    protected ManagementContextCredentialRemovalConsumer() {
    }

    public void setWebauthnManagementContextLookupStrategy(@Nonnull Function<ProfileRequestContext, WebAuthnManagementContext> strategy) {
        this.checkSetterPreconditions();
        this.webauthnManagementContextLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"WebauthnManagementContextLookupStrategy strategy cannot be null");
    }

    @Override
    public void accept(ProfileRequestContext prc, byte[] credentialId) {
        WebAuthnManagementContext webauthnManagementContext = this.webauthnManagementContextLookupStrategy.apply(prc);
        if (webauthnManagementContext == null) {
            this.log.warn("{} No WebAuthn management context returned by lookup strategy, can not set credential identifier to remove", (Object)this.getId());
            return;
        }
        webauthnManagementContext.setCredentialIdToRemove(credentialId);
    }
}

