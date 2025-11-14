/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  com.yubico.webauthn.data.ByteArray
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.authn.AuthenticatorSupport
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy$CredentialPolicyOutcome
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.NonnullSupplier
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.policy.impl;

import com.yubico.fido.metadata.AAGUID;
import com.yubico.webauthn.data.ByteArray;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.authn.AuthenticatorSupport;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.policy.CredentialPolicy;
import net.shibboleth.idp.plugin.authn.webauthn.policy.impl.AbstractCredentialPolicyRule;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.NonnullSupplier;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class SecondFactorOnlyCredentialPolicyRule
extends AbstractCredentialPolicyRule {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(SecondFactorOnlyCredentialPolicyRule.class);
    @Nonnull
    @NotLive
    @Unmodifiable
    private Set<AAGUID> secondFactorOnlyAuthenticators = CollectionSupport.emptySet();

    public void setSecondFactorOnlyAuthenticators(Set<String> allowed) {
        this.checkSetterPreconditions();
        if (allowed != null) {
            this.secondFactorOnlyAuthenticators = (Set)((NonnullSupplier)allowed.stream().filter(strAAGUID -> StringSupport.trimOrNull((String)strAAGUID) != null).map(strAAGUID -> {
                ByteArray aaguidBytes = AuthenticatorSupport.parse((String)strAAGUID);
                if (aaguidBytes != null) {
                    return new AAGUID(aaguidBytes);
                }
                return null;
            }).filter(Objects::nonNull).collect(CollectionSupport.nonnullCollector(Collectors.toSet()))).get();
        }
    }

    @Override
    public CredentialPolicy.CredentialPolicyOutcome doEvaluate(@Nonnull EnhancedCredentialRecord credential, @Nonnull ProfileRequestContext prc, @Nonnull WebAuthnAuthenticationContext webAuthnContext) {
        AAGUID aaguidToCheck = new AAGUID(new ByteArray(credential.getCredentialRecord().getAaguid()));
        boolean secondFactorOnly = this.secondFactorOnlyAuthenticators.contains(aaguidToCheck);
        if (!webAuthnContext.isSecondFactor() && secondFactorOnly) {
            if (this.log.isTraceEnabled()) {
                this.log.trace("Rejected credential '{}', authentication is sole-factor and authenticator '{}' that created the credential should only be used as a second factor", (Object)credential.getCredentialRecord().getCredentialIdBase64(), (Object)aaguidToCheck.asGuidString());
            }
            return CredentialPolicy.CredentialPolicyOutcome.REJECT;
        }
        if (this.log.isTraceEnabled()) {
            this.log.trace("Accepting credential '{}' from authenticator '{}'", (Object)credential.getCredentialRecord().getCredentialIdBase64(), (Object)aaguidToCheck.asGuidString());
        }
        return CredentialPolicy.CredentialPolicyOutcome.ACCEPT;
    }
}

