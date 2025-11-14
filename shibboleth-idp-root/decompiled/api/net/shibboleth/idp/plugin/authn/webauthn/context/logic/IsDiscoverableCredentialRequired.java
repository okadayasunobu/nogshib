/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.context.logic;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class IsDiscoverableCredentialRequired
implements Predicate<ProfileRequestContext> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(IsDiscoverableCredentialRequired.class);

    @Override
    public boolean test(@Nullable ProfileRequestContext input) {
        if (input == null) {
            this.log.trace("Profile context was null, can not determine if discoverable credentials are required");
            return false;
        }
        AuthenticationContext authnContext = (AuthenticationContext)input.getSubcontext(AuthenticationContext.class);
        if (authnContext == null) {
            this.log.trace("Authentication context was null, can not determine if discoverable credentials are required");
            return false;
        }
        WebAuthnAuthenticationContext webauthnContext = (WebAuthnAuthenticationContext)authnContext.getSubcontext(WebAuthnAuthenticationContext.class);
        if (webauthnContext == null) {
            this.log.trace("WebAuthn authentication context was null, can not determine if discoverable credentials are required");
            return false;
        }
        boolean discoverableCredentialRequired = webauthnContext.getUsername() == null;
        this.log.debug("{}", discoverableCredentialRequired ? "Usernameless (discoverable/passkey) authentication required" : "Passwordless authentication required (username supplied) for '" + webauthnContext.getUsername() + "'");
        return discoverableCredentialRequired;
    }
}

