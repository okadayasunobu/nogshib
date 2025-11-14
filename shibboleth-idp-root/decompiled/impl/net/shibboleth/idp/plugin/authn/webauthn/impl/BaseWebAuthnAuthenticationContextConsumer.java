/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnAuthenticationContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;

public abstract class BaseWebAuthnAuthenticationContextConsumer
implements Consumer<ProfileRequestContext> {
    @Nonnull
    private final Function<ProfileRequestContext, WebAuthnAuthenticationContext> webauthnAuthContextCreationStrategy = new ChildContextLookup(WebAuthnAuthenticationContext.class).compose((Function)new ChildContextLookup(AuthenticationContext.class));

    BaseWebAuthnAuthenticationContextConsumer() {
    }

    @Override
    public void accept(@Nullable ProfileRequestContext input) {
        if (input == null) {
            return;
        }
        WebAuthnAuthenticationContext webAuthnContext = this.webauthnAuthContextCreationStrategy.apply(input);
        if (webAuthnContext == null) {
            return;
        }
        this.doAccept(webAuthnContext);
    }

    protected abstract void doAccept(@Nonnull WebAuthnAuthenticationContext var1);
}

