/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.FidoMetadataService
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy
 *  net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy$CredentialPolicyOutcome
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.shared.annotation.constraint.ThreadSafeAfterInit
 *  net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.policy.impl;

import com.yubico.fido.metadata.FidoMetadataService;
import java.util.function.BiPredicate;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.annotation.constraint.ThreadSafeAfterInit;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

@ThreadSafeAfterInit
public abstract class AbstractCredentialPolicyRule
extends AbstractIdentifiableInitializableComponent
implements CredentialPolicy {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AbstractCredentialPolicyRule.class);
    @Nullable
    private FidoMetadataService fidoMetadataService;
    @Nonnull
    private BiPredicate<EnhancedCredentialRecord, ProfileRequestContext> activationCondition = (cred, prc) -> true;
    @Nonnull
    private Function<ProfileRequestContext, WebAuthnAuthenticationContext> webauthnContextLookupStrategy = new ChildContextLookup(WebAuthnAuthenticationContext.class).compose((Function)new ChildContextLookup(AuthenticationContext.class));

    protected AbstractCredentialPolicyRule() {
    }

    public void setWebAuthnContextLookupStrategy(@Nonnull Function<ProfileRequestContext, WebAuthnAuthenticationContext> strategy) {
        this.checkSetterPreconditions();
        this.webauthnContextLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"WebAuthnContextLookuplookup strategy cannot be null");
    }

    public void setActivationConditionStrategy(@Nonnull BiPredicate<EnhancedCredentialRecord, ProfileRequestContext> condition) {
        this.checkSetterPreconditions();
        this.activationCondition = (BiPredicate)Constraint.isNotNull(condition, (String)"Activation condition cannot be null");
    }

    public void setActivationCondition(boolean flag) {
        this.checkSetterPreconditions();
        this.activationCondition = flag ? (cred, prc) -> true : (cred, prc) -> false;
    }

    public void setFidoMetadataService(@Nullable FidoMetadataService trustSource) {
        this.checkSetterPreconditions();
        this.fidoMetadataService = trustSource;
    }

    @Nullable
    protected FidoMetadataService getFidoMetadataService() {
        return this.fidoMetadataService;
    }

    public CredentialPolicy.CredentialPolicyOutcome evaluate(@Nonnull EnhancedCredentialRecord credential, @Nonnull ProfileRequestContext prc) {
        if (!this.activationCondition.test(credential, prc)) {
            this.log.trace("CredentialPolicy rule '{}' not active for this request", (Object)this.getId());
            return CredentialPolicy.CredentialPolicyOutcome.IGNORE;
        }
        WebAuthnAuthenticationContext webAuthnContext = this.webauthnContextLookupStrategy.apply(prc);
        if (credential == null || prc == null || webAuthnContext == null) {
            return CredentialPolicy.CredentialPolicyOutcome.REJECT;
        }
        return this.doEvaluate(credential, prc, webAuthnContext);
    }

    protected abstract CredentialPolicy.CredentialPolicyOutcome doEvaluate(@Nonnull EnhancedCredentialRecord var1, @Nonnull ProfileRequestContext var2, @Nonnull WebAuthnAuthenticationContext var3);
}

