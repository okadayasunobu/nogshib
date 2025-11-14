/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  jakarta.servlet.http.HttpServletRequest
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import com.yubico.webauthn.data.PublicKeyCredential;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class ExtractPublicKeyCredentialAttestationFromFormRequest
extends AbstractWebAuthnAction<WebAuthnRegistrationContext> {
    @Nonnull
    @NotEmpty
    public static final String DEFAULT_PARAMETER_NAME = "publicKeyCredential";
    @Nonnull
    @NotEmpty
    public static final String DEFAULT_NICKNAME_FIELD_NAME = "credentialNickname";
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(ExtractPublicKeyCredentialAttestationFromFormRequest.class);
    @Nonnull
    @NotEmpty
    private String publicKeyCredentialAttestationParameterName = "publicKeyCredential";
    @Nonnull
    @NotEmpty
    private String credentialNicknameParameterName = "credentialNickname";

    public ExtractPublicKeyCredentialAttestationFromFormRequest() {
        super(new ChildContextLookup(WebAuthnRegistrationContext.class));
    }

    public void setPublickKeyCredentialAttestationParameterName(@Nonnull @NotEmpty String field) {
        this.checkSetterPreconditions();
        this.publicKeyCredentialAttestationParameterName = (String)Constraint.isNotNull((Object)StringSupport.trimOrNull((String)field), (String)"Attestation parameter name cannot be null or empty");
    }

    public void setCredentialNicknameParameterName(@Nonnull @NotEmpty String field) {
        this.checkSetterPreconditions();
        this.credentialNicknameParameterName = (String)Constraint.isNotNull((Object)StringSupport.trimOrNull((String)field), (String)"Nickname parameter can not be null or empty");
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnRegistrationContext context) {
        HttpServletRequest request = this.getHttpServletRequest();
        if (request == null) {
            this.log.debug("{} Profile action does not contain an HttpServletRequest", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"NoCredentials");
            return;
        }
        String pkCredAttestationJson = request.getParameter(this.publicKeyCredentialAttestationParameterName);
        if (StringSupport.trimOrNull((String)pkCredAttestationJson) == null) {
            this.log.debug("{} No PublicKeyCredential with authenticator attestation response in request", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"NoCredentials");
            return;
        }
        this.log.trace("{} PublicKeyCredential authenticator attestation response:'{}'", (Object)this.getLogPrefix(), (Object)pkCredAttestationJson);
        String credNickname = request.getParameter(this.credentialNicknameParameterName);
        this.log.trace("{} Credential nickname is '{}'", (Object)this.getLogPrefix(), (Object)credNickname);
        if (StringSupport.trimOrNull((String)credNickname) == null) {
            this.log.debug("{} No credential nickname in request", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            return;
        }
        try {
            PublicKeyCredential pkCredAttestation = PublicKeyCredential.parseRegistrationResponseJson((String)pkCredAttestationJson);
            context.setPublicKeyCredentialAttestationResponse(pkCredAttestation);
            context.setCredentialNickname(credNickname);
        }
        catch (IOException e) {
            this.log.debug("{} Could not parse PublicKeyCredential response from request parameter '{}'", new Object[]{this.getLogPrefix(), this.publicKeyCredentialAttestationParameterName, e});
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidRegistration");
            return;
        }
    }
}

