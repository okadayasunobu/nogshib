/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.AuthenticatorAssertionResponse
 *  com.yubico.webauthn.data.ByteArray
 *  com.yubico.webauthn.data.PublicKeyCredential
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.logic.PredicateSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredential;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class LookupRegisteredCredentialsFromUserHandle
extends AbstractWebAuthnAction<WebAuthnAuthenticationContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(LookupRegisteredCredentialsFromUserHandle.class);
    private Predicate<ProfileRequestContext> triggerEventOnNoCredentialsPredicate = PredicateSupport.alwaysFalse();
    @Nonnull
    @NotEmpty
    private String noCredentialsEventId = "NoRegisteredWebAuthnCredentials";
    @NonnullAfterInit
    private WebAuthnCredentialRepository repository;

    public LookupRegisteredCredentialsFromUserHandle() {
        super(new ChildContextLookup(WebAuthnAuthenticationContext.class).compose((Function)new ChildContextLookup(AuthenticationContext.class)));
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        this.repository = this.getCredentialRepository();
        if (this.repository == null) {
            throw new ComponentInitializationException("Credential repository can not be null");
        }
    }

    public void setTriggerEventOnNoCredentials(boolean trigger) {
        this.checkSetterPreconditions();
        this.triggerEventOnNoCredentialsPredicate = trigger ? PredicateSupport.alwaysTrue() : PredicateSupport.alwaysFalse();
    }

    public void setTriggerEventOnNoCredentialsPredicate(@Nonnull Predicate<ProfileRequestContext> predicate) {
        this.checkSetterPreconditions();
        this.triggerEventOnNoCredentialsPredicate = (Predicate)Constraint.isNotNull(predicate, (String)"TriggerEventOnNoCredentialsPredicate can not be null");
    }

    public void setNoCredentialsEventId(@Nonnull @NotEmpty String eventId) {
        this.checkSetterPreconditions();
        this.noCredentialsEventId = Constraint.isNotEmpty((String)eventId, (String)"NoCredentialsEventId can not be null or empty");
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnAuthenticationContext context) {
        PublicKeyCredential assertion = context.getPublicKeyCredentialAssertionResponse();
        if (assertion == null) {
            this.log.error("{} Unable to find Assertion in WebAuthn authentication context", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidProfileContext");
            return;
        }
        String username = context.getUsername();
        Optional userHandle = ((AuthenticatorAssertionResponse)assertion.getResponse()).getUserHandle();
        boolean credentialsFound = false;
        if (userHandle.isEmpty()) {
            this.log.debug("{} User could not be found, the authenticator did not supply a userHandle", (Object)this.getLogPrefix());
        } else {
            Optional potentialUsername = this.repository.getUsernameForUserHandle((ByteArray)userHandle.get());
            if (potentialUsername.isEmpty()) {
                this.log.debug("{} User could not be found from the supplied userHandle, no registered credentials", (Object)this.getLogPrefix());
            } else {
                if (username != null && !((String)potentialUsername.get()).equals(username)) {
                    this.log.debug("{} Username '{}' found from the userHandle was not the same as in the authentication context '{}'", new Object[]{this.getLogPrefix(), potentialUsername.get(), username});
                    ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"NoCredentials");
                    return;
                }
                Set credentials = this.repository.getRegistrationsByUsername((String)potentialUsername.get());
                if (credentials.isEmpty()) {
                    this.log.debug("{} Could not find any registered credentials for userHandle '{}'", (Object)this.getLogPrefix(), (Object)((ByteArray)userHandle.get()).getBase64());
                } else {
                    context.setExistingCredentials(this.enhancedCredentialRecord(credentials));
                    credentialsFound = true;
                    this.log.debug("{} Found registered credentials for userHandle '{}' (determined username as '{}')", new Object[]{this.getLogPrefix(), ((ByteArray)userHandle.get()).getBase64(), potentialUsername.get()});
                }
            }
        }
        if (this.triggerEventOnNoCredentialsPredicate.test(profileRequestContext) && !credentialsFound) {
            this.log.debug("{} Triggering event '{}' ", (Object)this.getLogPrefix(), (Object)this.noCredentialsEventId);
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)this.noCredentialsEventId);
            return;
        }
    }
}

