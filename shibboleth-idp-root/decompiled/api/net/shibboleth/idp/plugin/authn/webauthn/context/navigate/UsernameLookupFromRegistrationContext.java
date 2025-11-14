/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.context.navigate;

import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class UsernameLookupFromRegistrationContext
implements Function<ProfileRequestContext, String> {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(UsernameLookupFromRegistrationContext.class);

    @Override
    public String apply(@Nullable ProfileRequestContext input) {
        if (input == null) {
            this.log.trace("Profile context was null, can not find existing username");
            return null;
        }
        WebAuthnRegistrationContext registrationContext = (WebAuthnRegistrationContext)input.getSubcontext(WebAuthnRegistrationContext.class);
        if (registrationContext == null) {
            this.log.trace("WebAuthn registration context was null, can not find existing username");
            return null;
        }
        String username = registrationContext.getUsername();
        this.log.debug("{}", (Object)(username != null ? "Found existing username from registration context" : "Did not find existing username from registration context"));
        return username;
    }
}

