/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  com.yubico.fido.metadata.FidoMetadataService
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy$AuthenticatorPolicyOutcome
 *  net.shibboleth.shared.annotation.constraint.ThreadSafeAfterInit
 *  net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.policy.impl;

import com.yubico.fido.metadata.AAGUID;
import com.yubico.fido.metadata.FidoMetadataService;
import java.util.function.BiPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy;
import net.shibboleth.shared.annotation.constraint.ThreadSafeAfterInit;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

@ThreadSafeAfterInit
public abstract class AbstractAuthenticatorPolicyRule
extends AbstractIdentifiableInitializableComponent
implements AuthenticatorPolicy {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AbstractAuthenticatorPolicyRule.class);
    @Nullable
    private FidoMetadataService fidoMetadataService;
    @Nonnull
    private BiPredicate<AAGUID, ProfileRequestContext> activationCondition = (prc, claims) -> true;

    protected AbstractAuthenticatorPolicyRule() {
    }

    public void setActivationConditionStrategy(@Nonnull BiPredicate<AAGUID, ProfileRequestContext> condition) {
        this.checkSetterPreconditions();
        this.activationCondition = (BiPredicate)Constraint.isNotNull(condition, (String)"Activation condition cannot be null");
    }

    public void setActivationCondition(boolean flag) {
        this.checkSetterPreconditions();
        this.activationCondition = flag ? (prc, claims) -> true : (prc, claims) -> false;
    }

    public void setFidoMetadataService(@Nullable FidoMetadataService trustSource) {
        this.checkSetterPreconditions();
        this.fidoMetadataService = trustSource;
    }

    @Nullable
    protected FidoMetadataService getFidoMetadataService() {
        return this.fidoMetadataService;
    }

    public AuthenticatorPolicy.AuthenticatorPolicyOutcome evaluate(@Nullable AAGUID aaguid, @Nonnull ProfileRequestContext prc) {
        if (!this.activationCondition.test(aaguid, prc)) {
            this.log.trace("AuthenticatorPolicy rule '{}' not active for this request", (Object)this.getId());
            return AuthenticatorPolicy.AuthenticatorPolicyOutcome.IGNORE;
        }
        if (aaguid == null) {
            return AuthenticatorPolicy.AuthenticatorPolicyOutcome.REJECT;
        }
        return this.doAccept(aaguid, prc);
    }

    protected abstract AuthenticatorPolicy.AuthenticatorPolicyOutcome doAccept(@Nonnull AAGUID var1, @Nonnull ProfileRequestContext var2);
}

