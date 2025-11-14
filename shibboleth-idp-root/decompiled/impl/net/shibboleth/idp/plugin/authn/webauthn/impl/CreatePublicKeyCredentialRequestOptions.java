/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.PublicKeyCredentialRequestOptions
 *  com.yubico.webauthn.data.UserVerificationRequirement
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.authn.CredentialRequestOptionsParameters
 *  net.shibboleth.idp.plugin.authn.webauthn.client.WebAuthnAuthenticationClient
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.exception.WebAuthnAuthenticationClientException
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.NonnullSupplier
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import com.yubico.webauthn.data.UserVerificationRequirement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.authn.CredentialRequestOptionsParameters;
import net.shibboleth.idp.plugin.authn.webauthn.client.WebAuthnAuthenticationClient;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.exception.WebAuthnAuthenticationClientException;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.NonnullSupplier;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class CreatePublicKeyCredentialRequestOptions
extends AbstractWebAuthnAction<WebAuthnAuthenticationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(CreatePublicKeyCredentialRequestOptions.class);

    protected CreatePublicKeyCredentialRequestOptions() {
        super(new ChildContextLookup(WebAuthnAuthenticationContext.class).compose((Function)new ChildContextLookup(AuthenticationContext.class)));
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnAuthenticationContext context) {
        WebAuthnAuthenticationClient client = this.getWebAuthnClient();
        byte[] challenge = context.getServerChallenge();
        if (challenge == null) {
            this.log.error("{} WebAuthn challenge is null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"AuthenticationException");
            return;
        }
        UserVerificationRequirement uvRequirement = context.getUserVerificationRequirement();
        if (uvRequirement == null) {
            this.log.error("{} User verification requirement is null", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"AuthenticationException");
            return;
        }
        try {
            Collection existingCredentials = context.getExistingCredentials();
            List existingCredentialDescriptors = (List)((NonnullSupplier)existingCredentials.stream().map(cred -> cred.getCredentialRecord()).map(cred -> cred.toPublicKeyCredentialDescriptor()).filter(Objects::nonNull).collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableList()))).get();
            CredentialRequestOptionsParameters requestParams = CredentialRequestOptionsParameters.builder().withUserVerificationRequirement(uvRequirement).withChallenge(challenge).withAllowCredentials(existingCredentialDescriptors).build();
            assert (requestParams != null);
            PublicKeyCredentialRequestOptions pkCredRequestOptions = client.createAuthenticationRequest(requestParams);
            context.setPublicKeyCredentialRequestOptions(pkCredRequestOptions);
            this.log.debug("{} Created PublicKeyCredentialRequestOptions: '{}'", (Object)this.getLogPrefix(), (Object)pkCredRequestOptions);
        }
        catch (WebAuthnAuthenticationClientException e) {
            this.log.error("{} Unable to generate PublicKeyCredentialRequestOptions", (Object)this.getLogPrefix(), (Object)e);
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"AuthenticationException");
            return;
        }
    }
}

