/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 */
package net.shibboleth.idp.plugin.authn.webauthn.audit;

import javax.annotation.Nonnull;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

public final class WebAuthnAuditFields {
    @Nonnull
    @NotEmpty
    public static final String USERID = "WebAuthnUID";
    @Nonnull
    @NotEmpty
    public static final String UV = "WebAuthnUV";
    @Nonnull
    @NotEmpty
    public static final String FLOW_MODE = "WebAuthnFM";
    @Nonnull
    @NotEmpty
    public static final String ADMIN_AFFECTED_USER = "WebAuthnAdminAU";
    @Nonnull
    @NotEmpty
    public static final String ACTION_OUTCOME = "WebAuthnAdminAO";
    @Nonnull
    @NotEmpty
    public static final String ACTION = "WebAuthnAdminAction";
    @Nonnull
    @NotEmpty
    public static final String CRED_REMOVED = "WebAuthnAdminCR";
    @Nonnull
    @NotEmpty
    public static final String CRED_ADDED = "WebAuthnAdminCA";

    private WebAuthnAuditFields() {
    }
}

