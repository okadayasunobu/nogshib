/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.authn.AbstractAuthenticationAction
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.shibboleth.idp.authn.AbstractAuthenticationAction;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class LabelCredentialRecords
extends AbstractAuthenticationAction {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(LabelCredentialRecords.class);
    @Nonnull
    @NotLive
    private BiFunction<EnhancedCredentialRecord, ProfileRequestContext, List<String>> labeller = (cred, prc) -> CollectionSupport.emptyList();
    @NonnullAfterInit
    private Function<ProfileRequestContext, Collection<EnhancedCredentialRecord>> credentialsLookupStrategy;

    protected LabelCredentialRecords() {
    }

    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (this.credentialsLookupStrategy == null) {
            throw new ComponentInitializationException("credentialsLookupStrategy can not be null");
        }
    }

    public void setCredentialsLookupStrategy(@Nonnull Function<ProfileRequestContext, Collection<EnhancedCredentialRecord>> strategy) {
        this.checkSetterPreconditions();
        this.credentialsLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"credentialsLookupStrategy can not be null");
    }

    public void setLabeller(@Nonnull BiFunction<EnhancedCredentialRecord, ProfileRequestContext, List<String>> labellerIn) {
        this.checkSetterPreconditions();
        this.labeller = (BiFunction)Constraint.isNotNull(labellerIn, (String)"labeller can not be null");
    }

    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull AuthenticationContext context) {
        Collection<EnhancedCredentialRecord> credentials = this.credentialsLookupStrategy.apply(profileRequestContext);
        this.log.trace("{} Labelling '{}' credentials, with labeller '{}'", new Object[]{this.getLogPrefix(), credentials.size(), this.labeller});
        for (EnhancedCredentialRecord credential : credentials) {
            List<String> labels = this.labeller.apply(credential, profileRequestContext);
            if (labels == null) continue;
            this.log.trace("{} Added labels '{}' for credential '{}'", new Object[]{this.getLogPrefix(), labels, credential.getCredentialRecord().getCredentialIdBase64()});
            credential.setLabels(labels);
        }
    }
}

