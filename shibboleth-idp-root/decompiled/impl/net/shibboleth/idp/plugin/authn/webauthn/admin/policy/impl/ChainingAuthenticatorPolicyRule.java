/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy$AuthenticatorPolicyOutcome
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.policy.impl;

import com.yubico.fido.metadata.AAGUID;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy;
import net.shibboleth.idp.plugin.authn.webauthn.admin.policy.impl.AbstractAuthenticatorPolicyRule;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class ChainingAuthenticatorPolicyRule
extends AbstractAuthenticatorPolicyRule {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(ChainingAuthenticatorPolicyRule.class);
    private List<AuthenticatorPolicy> authenticatorPolicyChain;

    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (this.authenticatorPolicyChain == null) {
            throw new ComponentInitializationException("List of authenticator policies can not be null");
        }
    }

    public void setAuthenticatorPolicyChain(@Nullable List<AuthenticatorPolicy> chain) {
        this.checkSetterPreconditions();
        this.authenticatorPolicyChain = chain != null ? chain : CollectionSupport.emptyList();
    }

    @Override
    protected AuthenticatorPolicy.AuthenticatorPolicyOutcome doAccept(@Nonnull AAGUID aaguid, @Nonnull ProfileRequestContext prc) {
        for (AuthenticatorPolicy policy : this.authenticatorPolicyChain) {
            AuthenticatorPolicy.AuthenticatorPolicyOutcome outcome;
            if (this.log.isTraceEnabled()) {
                this.log.trace("Trying AuthenticatoryPolicy rule '{}' for authenticator '{}'", (Object)policy.getId(), (Object)aaguid.asGuidString());
            }
            if ((outcome = policy.evaluate(aaguid, prc)) == AuthenticatorPolicy.AuthenticatorPolicyOutcome.REJECT) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("AuthenticatorPolicy rule '{}' rejected authenticator '{}'", (Object)policy.getId(), (Object)aaguid.asGuidString());
                }
                return AuthenticatorPolicy.AuthenticatorPolicyOutcome.REJECT;
            }
            if (outcome == AuthenticatorPolicy.AuthenticatorPolicyOutcome.IGNORE) {
                if (!this.log.isDebugEnabled()) continue;
                this.log.debug("AuthenticatorPolicy rule '{}' was ignored for authenticator '{}'", (Object)policy.getId(), (Object)aaguid.asGuidString());
                continue;
            }
            if (!this.log.isTraceEnabled()) continue;
            this.log.trace("AuthenticatoryPolicy rule '{}' accepted authenticator '{}'", (Object)policy.getId(), (Object)aaguid.asGuidString());
        }
        return AuthenticatorPolicy.AuthenticatorPolicyOutcome.ACCEPT;
    }
}

