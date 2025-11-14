/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.AttestationConveyancePreference
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

import com.yubico.webauthn.data.AttestationConveyancePreference;
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

public class AddAttestationConveyancePreference
extends AbstractWebAuthnAction<WebAuthnRegistrationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AddAttestationConveyancePreference.class);
    @Nonnull
    private Function<ProfileRequestContext, AttestationConveyancePreference> attestationConveyancePreferenceLookupStrategy = FunctionSupport.constant((Object)AttestationConveyancePreference.NONE);

    public AddAttestationConveyancePreference() {
        super(new ChildContextLookup(WebAuthnRegistrationContext.class));
    }

    public void setAttestationConveyancePreference(@Nullable String preference) {
        this.checkSetterPreconditions();
        if (preference == null) {
            return;
        }
        AttestationConveyancePreference attestationPreference = Stream.of(AttestationConveyancePreference.values()).filter(uv -> uv.getValue().equals(preference)).findAny().orElseThrow(() -> new ConstraintViolationException("Attestation conveyance preference '" + preference + "' unknown"));
        assert (attestationPreference != null);
        this.attestationConveyancePreferenceLookupStrategy = FunctionSupport.constant((Object)attestationPreference);
    }

    public void setAttestationConveyancePreferenceLookupStrategy(@Nullable Function<ProfileRequestContext, AttestationConveyancePreference> strategy) {
        this.checkSetterPreconditions();
        if (strategy != null) {
            this.attestationConveyancePreferenceLookupStrategy = strategy;
        }
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnRegistrationContext context) {
        AttestationConveyancePreference preference = this.attestationConveyancePreferenceLookupStrategy.apply(profileRequestContext);
        this.log.debug("{} Attestation conveyance preference is '{}'", (Object)this.getLogPrefix(), (Object)preference);
        context.setAttestationConveyancePreference(preference);
    }
}

