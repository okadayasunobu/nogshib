/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy$CredentialPolicyOutcome
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.policy.impl;

import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy;
import net.shibboleth.idp.plugin.authn.webauthn.policy.impl.AbstractCredentialPolicyRule;
import net.shibboleth.idp.plugin.authn.webauthn.policy.impl.FunctionalRuleContext;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class FunctionalCredentialPolicyRule
extends AbstractCredentialPolicyRule {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(FunctionalCredentialPolicyRule.class);
    @Nonnull
    private BiFunction<EnhancedCredentialRecord, FunctionalRuleContext, CredentialPolicy.CredentialPolicyOutcome> rule = (cred, prc) -> CredentialPolicy.CredentialPolicyOutcome.IGNORE;

    public void setRule(@Nonnull BiFunction<EnhancedCredentialRecord, FunctionalRuleContext, CredentialPolicy.CredentialPolicyOutcome> function) {
        this.checkSetterPreconditions();
        this.rule = (BiFunction)Constraint.isNotNull(function, (String)"Rule function can not be null");
    }

    @Override
    protected CredentialPolicy.CredentialPolicyOutcome doEvaluate(EnhancedCredentialRecord credential, ProfileRequestContext prc, WebAuthnAuthenticationContext webAuthnContext) {
        this.log.debug("Running CredentialPolicy rule function '{}'", (Object)this.getId());
        return this.rule.apply(credential, new FunctionalRuleContext(prc, this.getFidoMetadataService()));
    }
}

