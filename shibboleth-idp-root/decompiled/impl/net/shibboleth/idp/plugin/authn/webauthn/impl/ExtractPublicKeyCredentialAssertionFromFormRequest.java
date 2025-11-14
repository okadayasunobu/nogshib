/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  jakarta.servlet.http.HttpServletRequest
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubico.webauthn.data.PublicKeyCredential;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class ExtractPublicKeyCredentialAssertionFromFormRequest
extends AbstractWebAuthnAction<WebAuthnAuthenticationContext> {
    @Nonnull
    @NotEmpty
    public static final String DEFAULT_PARAMETER_NAME = "publicKeyCredential";
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(ExtractPublicKeyCredentialAssertionFromFormRequest.class);
    @NonnullAfterInit
    @NotEmpty
    private String publicKeyCredentialAssertionParameterName = "publicKeyCredential";
    @NonnullAfterInit
    private ObjectMapper objectMapper;

    public ExtractPublicKeyCredentialAssertionFromFormRequest() {
        super(new ChildContextLookup(WebAuthnAuthenticationContext.class).compose((Function)new ChildContextLookup(AuthenticationContext.class)));
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (this.objectMapper == null) {
            throw new ComponentInitializationException("The object mapper cannot be null");
        }
    }

    public void setObjectMapper(@Nonnull ObjectMapper mapper) {
        this.checkSetterPreconditions();
        this.objectMapper = (ObjectMapper)Constraint.isNotNull((Object)mapper, (String)"Object mapper cannot be null");
    }

    public void setPublicKeyCredentialAssertionParameterName(@Nonnull @NotEmpty String field) {
        this.checkSetterPreconditions();
        this.publicKeyCredentialAssertionParameterName = (String)Constraint.isNotNull((Object)StringSupport.trimOrNull((String)field), (String)"Field name cannot be null or empty");
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnAuthenticationContext context) {
        HttpServletRequest request = this.getHttpServletRequest();
        if (request == null) {
            this.log.debug("{} Profile action does not contain an HttpServletRequest", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"NoCredentials");
            return;
        }
        String pkCredAssertionJson = request.getParameter(this.publicKeyCredentialAssertionParameterName);
        if (pkCredAssertionJson == null) {
            this.log.debug("{} No PublicKeyCredential with authenticator assertion response in request", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"NoCredentials");
            return;
        }
        this.log.trace("{} PublicKeyCredential authenticator assertion response: '{}'", (Object)this.getLogPrefix(), (Object)pkCredAssertionJson);
        try {
            PublicKeyCredential pkCredAssertion = PublicKeyCredential.parseAssertionResponseJson((String)pkCredAssertionJson);
            context.setPublicKeyCredentialAssertionResponse(pkCredAssertion);
        }
        catch (IOException e) {
            this.log.debug("{} Could not parse PublicKeyCredential response from request parameter '{}'", new Object[]{this.getLogPrefix(), this.publicKeyCredentialAssertionParameterName, e});
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"NoCredentials");
            return;
        }
    }
}

