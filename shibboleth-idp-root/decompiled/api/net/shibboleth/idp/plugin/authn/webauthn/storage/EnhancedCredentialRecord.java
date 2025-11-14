/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.MetadataBLOBPayloadEntry
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.shared.annotation.constraint.NonnullElements
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 *  net.shibboleth.shared.collection.CollectionSupport
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.NonnullSupplier
 */
package net.shibboleth.idp.plugin.authn.webauthn.storage;

import com.yubico.fido.metadata.MetadataBLOBPayloadEntry;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.metadata.AaguidEntry;
import net.shibboleth.idp.plugin.authn.webauthn.storage.CredentialRecord;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.NonnullSupplier;

public class EnhancedCredentialRecord {
    @Nonnull
    private final CredentialRecord credentialRecord;
    @Nonnull
    @Unmodifiable
    @NonnullElements
    @NotLive
    private Set<MetadataBLOBPayloadEntry> authenticatorMetadata;
    @Nullable
    private AaguidEntry aaguidMetadata;
    @Nonnull
    @Unmodifiable
    @NonnullElements
    @NotLive
    private List<String> labels;

    public EnhancedCredentialRecord(@Nonnull CredentialRecord credentialRecord) {
        this.credentialRecord = (CredentialRecord)Constraint.isNotNull((Object)credentialRecord, (String)"Credential can not be null");
        this.authenticatorMetadata = CollectionSupport.emptySet();
        this.labels = CollectionSupport.emptyList();
    }

    @Nonnull
    public CredentialRecord getCredentialRecord() {
        return this.credentialRecord;
    }

    public void setAuthenticatorMetadata(@Nullable Set<MetadataBLOBPayloadEntry> set) {
        if (set != null) {
            this.authenticatorMetadata = CollectionSupport.copyToSet((Collection)((Collection)((NonnullSupplier)set.stream().filter(Objects::nonNull).collect(CollectionSupport.nonnullCollector(Collectors.toSet()))).get()));
        }
    }

    @Nonnull
    @Unmodifiable
    @NonnullElements
    @NotLive
    public Set<MetadataBLOBPayloadEntry> getAuthenticatorMetadata() {
        return this.authenticatorMetadata;
    }

    public void setAaguidMetadata(@Nullable AaguidEntry aaguidEntry) {
        if (aaguidEntry != null) {
            this.aaguidMetadata = aaguidEntry;
        }
    }

    public void setLabels(@Nonnull List<String> list) {
        this.labels = CollectionSupport.copyToList((Collection)((Collection)Constraint.isNotNull(list, (String)"labels can not be null")));
    }

    @Nonnull
    @Unmodifiable
    @NonnullElements
    @NotLive
    public List<String> getLabels() {
        return this.labels;
    }

    @Nullable
    public String getAuthenticatorDescription() {
        if (this.aaguidMetadata != null) {
            return this.aaguidMetadata.getName();
        }
        return null;
    }

    @Nullable
    public String getIcon() {
        if (this.aaguidMetadata != null) {
            return this.aaguidMetadata.getIconLight();
        }
        return null;
    }

    @Nullable
    public String getType() {
        if (this.aaguidMetadata != null) {
            return this.aaguidMetadata.getType();
        }
        return null;
    }
}

