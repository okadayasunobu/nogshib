/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  com.yubico.fido.metadata.FidoMetadataService
 *  com.yubico.fido.metadata.MetadataBLOBPayloadEntry
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy$AuthenticatorPolicyOutcome
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.logic.PredicateSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.policy.impl;

import com.yubico.fido.metadata.AAGUID;
import com.yubico.fido.metadata.FidoMetadataService;
import com.yubico.fido.metadata.MetadataBLOBPayloadEntry;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy;
import net.shibboleth.idp.plugin.authn.webauthn.admin.policy.impl.AbstractAuthenticatorPolicyRule;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class AuthenticatorCapabilitiesPolicyRule
extends AbstractAuthenticatorPolicyRule {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AuthenticatorCapabilitiesPolicyRule.class);
    @Nonnull
    private Predicate<Set<MetadataBLOBPayloadEntry>> authenticatorCapabilityAcceptor = PredicateSupport.alwaysTrue();

    public void setAuthenticatorCapabilityAcceptor(@Nonnull Predicate<Set<MetadataBLOBPayloadEntry>> predicate) {
        this.checkSetterPreconditions();
        this.authenticatorCapabilityAcceptor = (Predicate)Constraint.isNotNull(predicate, (String)"AuthenticatorCapabilityAcceptor can not be null");
    }

    @Override
    public AuthenticatorPolicy.AuthenticatorPolicyOutcome doAccept(@Nonnull AAGUID aaguid, @Nullable ProfileRequestContext prc) {
        this.checkComponentActive();
        FidoMetadataService metadata = this.getFidoMetadataService();
        if (metadata == null) {
            this.log.warn("{} AuthenticatorCapabilities Policy Rule can not access attestation trust source, is metadata suported enabled? rejecting", (Object)this.getId());
            return AuthenticatorPolicy.AuthenticatorPolicyOutcome.REJECT;
        }
        Set entries = metadata.findEntries(aaguid);
        if (entries == null) {
            this.log.warn("{} No metadata to assess authenticator capabilities, rejecting", (Object)this.getId());
            return AuthenticatorPolicy.AuthenticatorPolicyOutcome.REJECT;
        }
        return AuthenticatorPolicy.AuthenticatorPolicyOutcome.of((boolean)this.authenticatorCapabilityAcceptor.test(entries));
    }
}

