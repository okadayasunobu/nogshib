/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  com.yubico.webauthn.data.ByteArray
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy$AuthenticatorPolicyOutcome
 *  net.shibboleth.idp.plugin.authn.webauthn.authn.AuthenticatorSupport
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.NonnullSupplier
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.policy.impl;

import com.yubico.fido.metadata.AAGUID;
import com.yubico.webauthn.data.ByteArray;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.admin.policy.AuthenticatorPolicy;
import net.shibboleth.idp.plugin.authn.webauthn.admin.policy.impl.AbstractAuthenticatorPolicyRule;
import net.shibboleth.idp.plugin.authn.webauthn.authn.AuthenticatorSupport;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.NonnullSupplier;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class AllowlistAuthenticatorPolicy
extends AbstractAuthenticatorPolicyRule {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(AllowlistAuthenticatorPolicy.class);
    @Nonnull
    @NotLive
    @Unmodifiable
    private Set<AAGUID> allowedAuthenticators = CollectionSupport.emptySet();

    public void setAllowedAuthenticators(Set<String> allowed) {
        this.checkSetterPreconditions();
        if (allowed != null) {
            this.allowedAuthenticators = (Set)((NonnullSupplier)allowed.stream().map(strAAGUID -> {
                ByteArray aaguidBytes = AuthenticatorSupport.parse((String)strAAGUID);
                if (aaguidBytes != null) {
                    return new AAGUID(aaguidBytes);
                }
                return null;
            }).filter(Objects::nonNull).collect(CollectionSupport.nonnullCollector(Collectors.toSet()))).get();
        }
    }

    @Override
    public AuthenticatorPolicy.AuthenticatorPolicyOutcome doAccept(@Nonnull AAGUID aaguid, @Nullable ProfileRequestContext prc) {
        return AuthenticatorPolicy.AuthenticatorPolicyOutcome.of((boolean)this.allowedAuthenticators.contains(aaguid));
    }
}

