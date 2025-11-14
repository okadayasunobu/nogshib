/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.AttestationConveyancePreference
 *  com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
 *  com.yubico.webauthn.data.ResidentKeyRequirement
 *  com.yubico.webauthn.data.UserVerificationRequirement
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.CredentialCreationOptionsParameters
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.exception.WebAuthnAuthenticationClientException
 *  net.shibboleth.shared.logic.ConstraintViolationException
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import com.yubico.webauthn.data.AttestationConveyancePreference;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.ResidentKeyRequirement;
import com.yubico.webauthn.data.UserVerificationRequirement;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.admin.CredentialCreationOptionsParameters;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.idp.plugin.authn.webauthn.exception.WebAuthnAuthenticationClientException;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.shared.logic.ConstraintViolationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class CreatePublicKeyCredentialCreationOptions
extends AbstractWebAuthnAction<WebAuthnRegistrationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(CreatePublicKeyCredentialCreationOptions.class);

    protected CreatePublicKeyCredentialCreationOptions() {
        super(new ChildContextLookup(WebAuthnRegistrationContext.class));
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnRegistrationContext context) {
        byte[] challenge = context.getServerChallenge();
        if (challenge == null) {
            this.log.error("{} WebAuthn challenge is null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistrationContext");
            return;
        }
        ResidentKeyRequirement residentKeyRequirement = context.getResidentKeyRequirement();
        if (residentKeyRequirement == null) {
            this.log.error("{} ResidentKeyRequirement is null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistrationContext");
            return;
        }
        UserVerificationRequirement uvRequirement = context.getUserVerificationRequirement();
        if (uvRequirement == null) {
            this.log.error("{} UserVerificationRequirement is null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistrationContext");
            return;
        }
        String name = context.getName();
        if (name == null) {
            this.log.error("{} user.name is null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistrationContext");
            return;
        }
        String displayName = context.getDisplayName();
        if (displayName == null) {
            this.log.error("{} user.displayName is null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistrationContext");
            return;
        }
        byte[] userId = context.getUserId();
        if (userId == null) {
            this.log.error("{} user.id is null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistrationContext");
            return;
        }
        AttestationConveyancePreference attestationPreference = context.getAttestationConveyancePreference();
        if (attestationPreference == null) {
            this.log.error("{} AttestationConveyancePreference is null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistrationContext");
            return;
        }
        try {
            Set existingCredentialDescriptors = context.getExistingCredentials().stream().map(cred -> cred.getCredentialRecord()).filter(Objects::nonNull).map(cred -> cred.toPublicKeyCredentialDescriptor()).filter(Objects::nonNull).collect(Collectors.toSet());
            assert (null != existingCredentialDescriptors);
            CredentialCreationOptionsParameters creationOptions = CredentialCreationOptionsParameters.builder().withUserVerificationRequirement(uvRequirement).withChallenge(challenge).withExcludeCredentials(existingCredentialDescriptors).withName(name).withDisplayName(displayName).withResidentKeyRequirement(residentKeyRequirement).withUserId(userId).withAttestationConveyancePreference(attestationPreference).withAuthenticatorAttachment(context.getAuthenticatorAttachmentRequirement()).withCredentialPropertiesExt(true).build();
            assert (creationOptions != null);
            PublicKeyCredentialCreationOptions pkCredCreationOptions = this.getWebAuthnClient().createRegistrationRequest(creationOptions);
            context.setPublicKeyCredentialCreationOptions(pkCredCreationOptions);
            this.log.debug("{} Created PublicKeyCredentialCreationOptions '{}'", (Object)this.getLogPrefix(), (Object)pkCredCreationOptions);
        }
        catch (WebAuthnAuthenticationClientException | ConstraintViolationException e) {
            this.log.error("{} Unable to generate PublicKeyCredentialCreationOptions", (Object)this.getLogPrefix(), (Object)e);
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InputOutputError");
            return;
        }
    }
}

