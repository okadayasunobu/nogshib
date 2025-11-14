/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord
 *  net.shibboleth.shared.annotation.ParameterName
 *  net.shibboleth.shared.collection.CollectionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.storage.EnhancedCredentialRecord;
import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.collection.CollectionSupport;
import org.opensaml.profile.context.ProfileRequestContext;

public class ChainingCredentialLabeller
implements BiFunction<EnhancedCredentialRecord, ProfileRequestContext, List<String>> {
    private final List<BiFunction<EnhancedCredentialRecord, ProfileRequestContext, List<String>>> labellers;

    public ChainingCredentialLabeller(@Nullable @ParameterName(name="labellers") List<BiFunction<EnhancedCredentialRecord, ProfileRequestContext, List<String>>> labellersIn) {
        this.labellers = labellersIn != null ? CollectionSupport.copyToList(labellersIn) : CollectionSupport.emptyList();
    }

    @Override
    public List<String> apply(EnhancedCredentialRecord credential, ProfileRequestContext prc) {
        ArrayList<String> labels = new ArrayList<String>();
        for (BiFunction<EnhancedCredentialRecord, ProfileRequestContext, List<String>> labeller : this.labellers) {
            List<String> newLabels = labeller.apply(credential, prc);
            if (newLabels == null) continue;
            labels.addAll(newLabels);
        }
        return labels;
    }
}

