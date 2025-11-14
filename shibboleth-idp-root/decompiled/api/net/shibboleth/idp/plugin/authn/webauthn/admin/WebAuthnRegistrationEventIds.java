/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin;

import javax.annotation.Nonnull;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

public final class WebAuthnRegistrationEventIds {
    @Nonnull
    @NotEmpty
    public static final String INVALID_REGISTRATION = "InvalidRegistration";
    @Nonnull
    @NotEmpty
    public static final String INVALID_REGISTRATION_CTX = "InvalidRegistrationContext";
    @Nonnull
    @NotEmpty
    public static final String INVALID_MANAGEMENT_CTX = "InvalidManagmentContext";
    @Nonnull
    @NotEmpty
    public static final String INVALID_ADMIN_ACTION = "InvalidAdminAction";

    private WebAuthnRegistrationEventIds() {
    }
}

