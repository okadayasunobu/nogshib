/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  com.yubico.webauthn.data.ByteArray
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.idp.plugin.authn.webauthn.authn.AuthenticatorSupport
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.NonnullSupplier
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import com.yubico.fido.metadata.AAGUID;
import com.yubico.webauthn.data.ByteArray;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.idp.plugin.authn.webauthn.authn.AuthenticatorSupport;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.NonnullSupplier;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

@ThreadSafe
public class SecondFactorOnlyCredentialLabeller
implements BiFunction<EnhancedCredentialRecord, ProfileRequestContext, List<String>> {
    @Nonnull
    @NotEmpty
    private static final String DEFAULT_LABEL = "SecondFactorOnly";
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(SecondFactorOnlyCredentialLabeller.class);
    @Nonnull
    @NotLive
    @Unmodifiable
    private Set<AAGUID> secondFactorOnlyAuthenticators = CollectionSupport.emptySet();
    @Nonnull
    @NotEmpty
    private String label = "SecondFactorOnly";

    public void setSecondFactorOnlyAuthenticators(Set<String> allowed) {
        if (allowed != null) {
            this.secondFactorOnlyAuthenticators = (Set)((NonnullSupplier)allowed.stream().filter(strAAGUID -> StringSupport.trimOrNull((String)strAAGUID) != null).map(strAAGUID -> {
                ByteArray aaguidBytes = AuthenticatorSupport.parse((String)strAAGUID);
                if (aaguidBytes != null) {
                    return new AAGUID(aaguidBytes);
                }
                this.log.trace("AAGUID '{}' is not valid", strAAGUID);
                return null;
            }).filter(Objects::nonNull).collect(CollectionSupport.nonnullCollector(Collectors.toSet()))).get();
        }
    }

    public void setLabel(@Nonnull String labelIn) {
        this.label = (String)Constraint.isNotNull((Object)labelIn, (String)"label can not be null");
    }

    @Override
    public List<String> apply(@Nullable EnhancedCredentialRecord cred, @Nullable ProfileRequestContext prc) {
        if (cred == null) {
            return CollectionSupport.emptyList();
        }
        AAGUID aaguidToCheck = new AAGUID(new ByteArray(cred.getCredentialRecord().getAaguid()));
        boolean secondFactorOnly = this.secondFactorOnlyAuthenticators.contains(aaguidToCheck);
        if (secondFactorOnly) {
            return CollectionSupport.listOf((Object)this.label);
        }
        return CollectionSupport.emptyList();
    }
}

