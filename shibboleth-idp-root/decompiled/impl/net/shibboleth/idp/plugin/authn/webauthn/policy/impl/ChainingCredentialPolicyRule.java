/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy
 *  net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy$CredentialPolicyOutcome
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.policy.impl;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy;
import net.shibboleth.idp.plugin.authn.webauthn.policy.impl.AbstractCredentialPolicyRule;
import net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class ChainingCredentialPolicyRule
extends AbstractCredentialPolicyRule {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(ChainingCredentialPolicyRule.class);
    private List<CredentialPolicy> credentialPolicyChain;

    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (this.credentialPolicyChain == null) {
            throw new ComponentInitializationException("List of authentication credential policies can not be null");
        }
    }

    public void setCredentialPolicyChain(@Nullable List<CredentialPolicy> chain) {
        this.checkSetterPreconditions();
        this.credentialPolicyChain = chain != null ? chain : CollectionSupport.emptyList();
    }

    @Override
    protected CredentialPolicy.CredentialPolicyOutcome doEvaluate(@Nonnull EnhancedCredentialRecord credential, @Nonnull ProfileRequestContext prc, @Nonnull WebAuthnAuthenticationContext webAuthnContext) {
        CredentialRecord credentialRecord = credential.getCredentialRecord();
        for (CredentialPolicy policy : this.credentialPolicyChain) {
            CredentialPolicy.CredentialPolicyOutcome outcome;
            if (this.log.isTraceEnabled()) {
                this.log.trace("Trying CredentialPolicy rule '{}' for credential '{}'", (Object)policy.getId(), (Object)credentialRecord.getCredentialIdBase64());
            }
            if ((outcome = policy.evaluate(credential, prc)) == CredentialPolicy.CredentialPolicyOutcome.REJECT) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("CredentialPolicy rule '{}' rejected credential '{}'", (Object)policy.getId(), (Object)credentialRecord.getCredentialIdBase64());
                }
                return CredentialPolicy.CredentialPolicyOutcome.REJECT;
            }
            if (outcome == CredentialPolicy.CredentialPolicyOutcome.IGNORE) {
                if (!this.log.isDebugEnabled()) continue;
                this.log.debug("CredentialPolicy rule '{}' was ignored for credential '{}'", (Object)policy.getId(), (Object)credentialRecord.getCredentialIdBase64());
                continue;
            }
            if (!this.log.isTraceEnabled()) continue;
            this.log.trace("CredentialPolicy rule '{}' accepted credential '{}'", (Object)policy.getId(), (Object)credentialRecord.getCredentialIdBase64());
        }
        return CredentialPolicy.CredentialPolicyOutcome.ACCEPT;
    }
}

