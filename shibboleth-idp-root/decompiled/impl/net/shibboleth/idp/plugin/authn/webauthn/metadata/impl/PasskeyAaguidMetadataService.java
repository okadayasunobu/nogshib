/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AAGUID
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.shared.logic.Constraint
 */
package net.shibboleth.idp.plugin.authn.webauthn.metadata.impl;

import com.yubico.fido.metadata.AAGUID;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.metadata.AaguidEntry;
import net.shibboleth.shared.logic.Constraint;

public class PasskeyAaguidMetadataService {
    @Nonnull
    private final Map<String, AaguidEntry> metadata;

    public PasskeyAaguidMetadataService(@Nonnull Map<String, AaguidEntry> mtdata) {
        this.metadata = (Map)Constraint.isNotNull(mtdata, (String)"AAGUID Metadata can not be null");
    }

    @Nullable
    public AaguidEntry getEntry(@Nullable String aaguid) {
        if (aaguid == null) {
            return null;
        }
        return this.metadata.get(aaguid);
    }

    @Nullable
    public AaguidEntry getEntry(@Nullable AAGUID aaguid) {
        if (aaguid == null) {
            return null;
        }
        return this.metadata.get(aaguid.asGuidString());
    }
}

