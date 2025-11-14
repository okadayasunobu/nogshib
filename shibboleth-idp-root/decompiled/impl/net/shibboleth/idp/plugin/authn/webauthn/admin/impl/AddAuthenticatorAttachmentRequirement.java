/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.AuthenticatorAttachment
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
 *  net.shibboleth.shared.logic.ConstraintViolationException
 *  net.shibboleth.shared.logic.FunctionSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import com.yubico.webauthn.data.AuthenticatorAttachment;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.shared.logic.ConstraintViolationException;
import net.shibboleth.shared.logic.FunctionSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class AddAuthenticatorAttachmentRequirement
extends AbstractWebAuthnAction<WebAuthnRegistrationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AddAuthenticatorAttachmentRequirement.class);
    @Nonnull
    private Function<ProfileRequestContext, AuthenticatorAttachment> authenticatorAttachmentRequirementLookupStrategy = FunctionSupport.constant(null);

    public AddAuthenticatorAttachmentRequirement() {
        super(new ChildContextLookup(WebAuthnRegistrationContext.class));
    }

    public void setAuthenticatorAttachmentRequirement(@Nullable String requirement) {
        this.checkSetterPreconditions();
        if (requirement == null) {
            return;
        }
        if ("any".equals(requirement)) {
            this.authenticatorAttachmentRequirementLookupStrategy = FunctionSupport.constant(null);
        } else {
            AuthenticatorAttachment aaRequirement = Stream.of(AuthenticatorAttachment.values()).filter(aa -> aa.getValue().equals(requirement)).findAny().orElseThrow(() -> new ConstraintViolationException("AuthenticatorAttachment requirement " + requirement + " unknown"));
            assert (aaRequirement != null);
            this.authenticatorAttachmentRequirementLookupStrategy = FunctionSupport.constant((Object)aaRequirement);
        }
    }

    public void setAuthenticatorAttachmentRequirementLookupStrategy(@Nullable Function<ProfileRequestContext, AuthenticatorAttachment> strategy) {
        this.checkSetterPreconditions();
        if (strategy != null) {
            this.authenticatorAttachmentRequirementLookupStrategy = strategy;
        }
    }

    @Override
    protected void doExecute(ProfileRequestContext profileRequestContext, WebAuthnRegistrationContext context) {
        AuthenticatorAttachment attachment = this.authenticatorAttachmentRequirementLookupStrategy.apply(profileRequestContext);
        this.log.debug("{} AuthenticatorAttachment is '{}'", (Object)this.getLogPrefix(), attachment != null ? attachment : "ANY");
        context.setAuthenticatorAttachmentRequirement(attachment);
    }
}

