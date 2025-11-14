/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.ThreadSafe
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.logic.Constraint
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import org.opensaml.profile.context.ProfileRequestContext;

@ThreadSafe
public class PasskeyCredentialLabeller
implements BiFunction<EnhancedCredentialRecord, ProfileRequestContext, List<String>> {
    @Nonnull
    @NotEmpty
    private static final String DEFAULT_LABEL = "Passkey";
    @Nonnull
    @NotEmpty
    private String label = "Passkey";

    public void setLabel(@Nonnull String labelIn) {
        this.label = (String)Constraint.isNotNull((Object)labelIn, (String)"label can not be null");
    }

    @Override
    public List<String> apply(@Nullable EnhancedCredentialRecord cred, @Nullable ProfileRequestContext prc) {
        if (cred == null) {
            return CollectionSupport.emptyList();
        }
        ArrayList<String> labels = new ArrayList<String>();
        CredentialRecord credential = cred.getCredentialRecord();
        if (credential.isDiscoverable().isPresent() && credential.isDiscoverable().get() == Boolean.TRUE) {
            labels.add(this.label);
        }
        return labels;
    }
}

