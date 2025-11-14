/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy$AuthenticatorPolicyOutcome
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.policy.impl;

import com.yubico.fido.metadata.AAGUID;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy;
import net.shibboleth.idp.plugin.authn.webauthn.admin.policy.impl.AbstractAuthenticatorPolicyRule;
import net.shibboleth.idp.plugin.authn.webauthn.policy.impl.FunctionalRuleContext;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class FunctionalAuthenticatorPolicyRule
extends AbstractAuthenticatorPolicyRule {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(FunctionalAuthenticatorPolicyRule.class);
    @Nonnull
    private BiFunction<AAGUID, FunctionalRuleContext, AuthenticatorPolicy.AuthenticatorPolicyOutcome> rule = (cred, rc) -> AuthenticatorPolicy.AuthenticatorPolicyOutcome.IGNORE;

    public void setRule(@Nonnull BiFunction<AAGUID, FunctionalRuleContext, AuthenticatorPolicy.AuthenticatorPolicyOutcome> function) {
        this.checkSetterPreconditions();
        this.rule = (BiFunction)Constraint.isNotNull(function, (String)"Rule function can not be null");
    }

    @Override
    protected AuthenticatorPolicy.AuthenticatorPolicyOutcome doAccept(AAGUID aaguid, ProfileRequestContext prc) {
        this.log.debug("Running AuthenticatorPolicy rule function '{}'", (Object)this.getId());
        return this.rule.apply(aaguid, new FunctionalRuleContext(prc, this.getFidoMetadataService()));
    }
}

