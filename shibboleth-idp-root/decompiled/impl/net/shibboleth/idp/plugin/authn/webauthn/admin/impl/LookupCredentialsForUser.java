/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.idp.plugin.authn.webauthn.storage.WebAuthnCredentialRepository;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class LookupCredentialsForUser
extends AbstractWebAuthnAction<WebAuthnManagementContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(LookupCredentialsForUser.class);
    @NonnullAfterInit
    private WebAuthnCredentialRepository repository;

    public LookupCredentialsForUser() {
        super(new ChildContextLookup(WebAuthnManagementContext.class));
    }

    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        this.repository = this.getCredentialRepository();
        if (this.repository == null) {
            throw new ComponentInitializationException("Credential repository can not be null");
        }
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnManagementContext context) {
        String userToSearchFor = context.getSearchUsername();
        if (StringSupport.trimOrNull((String)userToSearchFor) == null) {
            this.log.trace("{} No username to search for", (Object)this.getLogPrefix());
            context.setFoundCredentials((Collection)CollectionSupport.emptyList());
        } else {
            this.log.trace("{} Finding registered credentials for '{}'", (Object)this.getLogPrefix(), (Object)userToSearchFor);
            assert (userToSearchFor != null);
            Set credentials = this.repository.getRegistrationsByUsername(userToSearchFor);
            this.log.debug("{} Found '{}' credentials", (Object)this.getLogPrefix(), (Object)credentials.size());
            context.setFoundCredentials(this.enhancedCredentialRecord(credentials));
        }
    }
}

