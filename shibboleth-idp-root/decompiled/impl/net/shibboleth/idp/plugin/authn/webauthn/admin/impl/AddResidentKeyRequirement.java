/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.ResidentKeyRequirement
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

import com.yubico.webauthn.data.ResidentKeyRequirement;
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

public class AddResidentKeyRequirement
extends AbstractWebAuthnAction<WebAuthnRegistrationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AddResidentKeyRequirement.class);
    @Nonnull
    private Function<ProfileRequestContext, ResidentKeyRequirement> residentKeyRequirementLookupStrategy = FunctionSupport.constant((Object)ResidentKeyRequirement.PREFERRED);

    public AddResidentKeyRequirement() {
        super(new ChildContextLookup(WebAuthnRegistrationContext.class));
    }

    public void setResidentKeyRequirement(@Nullable String requirement) {
        this.checkSetterPreconditions();
        if (requirement == null) {
            return;
        }
        ResidentKeyRequirement uvRequirement = Stream.of(ResidentKeyRequirement.values()).filter(rk -> rk.getValue().equals(requirement)).findAny().orElseThrow(() -> new ConstraintViolationException("ResidentKey requirement " + requirement + " unknown"));
        assert (uvRequirement != null);
        this.residentKeyRequirementLookupStrategy = FunctionSupport.constant((Object)uvRequirement);
    }

    public void setResidentKeyRequirementLookupStrategy(@Nullable Function<ProfileRequestContext, ResidentKeyRequirement> strategy) {
        this.checkSetterPreconditions();
        if (strategy != null) {
            this.residentKeyRequirementLookupStrategy = strategy;
        }
    }

    @Override
    protected void doExecute(ProfileRequestContext profileRequestContext, WebAuthnRegistrationContext context) {
        ResidentKeyRequirement requirement = this.residentKeyRequirementLookupStrategy.apply(profileRequestContext);
        this.log.debug("{} ResidentKey requirement is '{}'", (Object)this.getLogPrefix(), (Object)requirement);
        context.setResidentKeyRequirement(requirement);
    }
}

