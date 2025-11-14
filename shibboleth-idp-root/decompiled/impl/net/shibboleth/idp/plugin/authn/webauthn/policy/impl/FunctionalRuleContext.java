/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.fido.metadata.FidoMetadataService
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.shared.logic.Constraint
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.policy.impl;

import com.yubico.fido.metadata.FidoMetadataService;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.shared.logic.Constraint;
import org.opensaml.profile.context.ProfileRequestContext;

public class FunctionalRuleContext {
    @Nonnull
    private final ProfileRequestContext profileRequestContext;
    @Nullable
    private final FidoMetadataService metadataService;

    public FunctionalRuleContext(@Nonnull ProfileRequestContext prc, @Nullable FidoMetadataService metadata) {
        this.profileRequestContext = (ProfileRequestContext)Constraint.isNotNull((Object)prc, (String)"The ProfileRequestContext can not be null");
        this.metadataService = metadata;
    }

    @Nullable
    public FidoMetadataService getMetadataService() {
        return this.metadataService;
    }

    @Nonnull
    public ProfileRequestContext getProfileRequestContext() {
        return this.profileRequestContext;
    }
}

