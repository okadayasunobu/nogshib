/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 */
package net.shibboleth.idp.plugin.authn.webauthn.authn;

import javax.annotation.Nonnull;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

public final class WebAuthnAuthenticationEventIds {
    @Nonnull
    @NotEmpty
    public static final String NO_REGISTERED_WEBAUTHN_CREDENTIALS = "NoRegisteredWebAuthnCredentials";
    @Nonnull
    @NotEmpty
    public static final String USER_HANDLE_NOT_REGISTERED = "UserHandleNotRegistered";
    @Nonnull
    @NotEmpty
    public static final String CREDENTIAL_POLICY_REJECTION = "CredentialPolicyRejection";

    private WebAuthnAuthenticationEventIds() {
    }
}

