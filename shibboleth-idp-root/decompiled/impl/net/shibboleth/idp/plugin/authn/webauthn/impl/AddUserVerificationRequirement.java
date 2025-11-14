/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.UserVerificationRequirement
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.logic.ConstraintViolationException
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import com.yubico.webauthn.data.UserVerificationRequirement;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.ConstraintViolationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class AddUserVerificationRequirement
extends AbstractWebAuthnAction<BaseWebAuthnContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AddUserVerificationRequirement.class);
    @Nonnull
    private UserVerificationRequirement userVerificationRequirement = UserVerificationRequirement.PREFERRED;

    public AddUserVerificationRequirement() {
        super(new ChildContextLookup(BaseWebAuthnContext.class).compose((Function)new ChildContextLookup(AuthenticationContext.class)));
    }

    public void setUserVerificationRequirement(@Nonnull @NotEmpty String requirement) {
        this.checkSetterPreconditions();
        Constraint.isNotEmpty((String)requirement, (String)"userVerificationRequirement can not be null or empty");
        UserVerificationRequirement uvRequirement = Stream.of(UserVerificationRequirement.values()).filter(uv -> uv.getValue().equals(requirement)).findAny().orElseThrow(() -> new ConstraintViolationException("UserVerification requirement " + requirement + " unknown"));
        assert (uvRequirement != null);
        this.userVerificationRequirement = uvRequirement;
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull BaseWebAuthnContext context) {
        this.log.debug("{} UserVerification is '{}'", (Object)this.getLogPrefix(), (Object)this.userVerificationRequirement);
        context.setUserVerificationRequirement(this.userVerificationRequirement);
    }
}

