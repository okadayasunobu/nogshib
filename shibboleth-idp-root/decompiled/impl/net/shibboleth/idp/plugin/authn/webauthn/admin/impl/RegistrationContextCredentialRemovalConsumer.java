/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
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
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class RegistrationContextCredentialRemovalConsumer
extends AbstractIdentifiableInitializableComponent
implements BiConsumer<ProfileRequestContext, byte[]> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(RegistrationContextCredentialRemovalConsumer.class);
    @Nonnull
    private Function<ProfileRequestContext, WebAuthnRegistrationContext> webauthnRegistrationContextLookupStrategy = new ChildContextLookup(WebAuthnRegistrationContext.class);

    protected RegistrationContextCredentialRemovalConsumer() {
    }

    public void setWebauthnRegistrationContextLookupStrategy(@Nonnull Function<ProfileRequestContext, WebAuthnRegistrationContext> strategy) {
        this.checkSetterPreconditions();
        this.webauthnRegistrationContextLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"WebauthnContextLookuplookup strategy cannot be null");
    }

    @Override
    public void accept(ProfileRequestContext prc, byte[] credentialId) {
        WebAuthnRegistrationContext webauthnRegistrationContext = this.webauthnRegistrationContextLookupStrategy.apply(prc);
        if (webauthnRegistrationContext == null) {
            this.log.warn("{} No WebAuthn registration context returned by lookup strategy, can not set credential identifier to remove", (Object)this.getId());
            return;
        }
        webauthnRegistrationContext.setCredentialIdToRemove(credentialId);
    }
}

