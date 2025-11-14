/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.yubico.webauthn.data.UserVerificationRequirement
 *  javax.annotation.Nonnull
 *  net.shibboleth.shared.logic.Constraint
 */
package net.shibboleth.idp.plugin.authn.webauthn.authn;

import com.yubico.webauthn.data.UserVerificationRequirement;
import javax.annotation.Nonnull;
import net.shibboleth.shared.logic.Constraint;

public abstract class BaseOptionsParameters {
    @Nonnull
    private final UserVerificationRequirement userVerificationRequirement;
    @Nonnull
    private final byte[] challenge;

    protected BaseOptionsParameters(@Nonnull UserVerificationRequirement uvRequirement, @Nonnull byte[] challengeIn) {
        this.userVerificationRequirement = (UserVerificationRequirement)Constraint.isNotNull((Object)uvRequirement, (String)"UserVerificationRequirement can not be null");
        this.challenge = (byte[])Constraint.isNotNull((Object)challengeIn, (String)"Challenge can not be null");
    }

    @Nonnull
    public final UserVerificationRequirement getUserVerificationRequirement() {
        return this.userVerificationRequirement;
    }

    @Nonnull
    public final byte[] getChallenge() {
        return this.challenge;
    }
}

