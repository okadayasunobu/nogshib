/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.AuthenticatorGetInfo
 *  com.yubico.fido.metadata.MetadataBLOBPayloadEntry
 *  com.yubico.fido.metadata.MetadataStatement
 *  com.yubico.fido.metadata.SupportedCtapOptions
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.policy.impl;

import com.yubico.fido.metadata.AuthenticatorGetInfo;
import com.yubico.fido.metadata.MetadataBLOBPayloadEntry;
import com.yubico.fido.metadata.MetadataStatement;
import com.yubico.fido.metadata.SupportedCtapOptions;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class AuthenticatorGetInfoUVCapable
implements Predicate<Set<MetadataBLOBPayloadEntry>> {
    @Override
    public boolean test(Set<MetadataBLOBPayloadEntry> metadata) {
        if (metadata.size() != 1) {
            return false;
        }
        MetadataBLOBPayloadEntry entry = metadata.iterator().next();
        Optional metadataStmt = entry.getMetadataStatement();
        if (metadataStmt.isEmpty()) {
            return false;
        }
        Optional authenticatorGetInfo = ((MetadataStatement)metadataStmt.get()).getAuthenticatorGetInfo();
        if (authenticatorGetInfo.isEmpty()) {
            return false;
        }
        Optional options = ((AuthenticatorGetInfo)authenticatorGetInfo.get()).getOptions();
        if (options.isEmpty()) {
            return false;
        }
        return ((SupportedCtapOptions)options.get()).isUv() || ((SupportedCtapOptions)options.get()).isClientPin();
    }
}

