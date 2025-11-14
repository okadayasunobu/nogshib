/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.authn.AuthenticationResult
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.authn.principal.UsernamePrincipal
 *  org.opensaml.profile.context.ProfileRequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.impl;

import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import javax.security.auth.Subject;
import net.shibboleth.idp.authn.AuthenticationResult;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.principal.UsernamePrincipal;
import org.opensaml.profile.context.ProfileRequestContext;

public class UsernameFromAuthenticationContextLookupStrategy
implements Function<ProfileRequestContext, String> {
    @Override
    @Nullable
    public String apply(@Nullable ProfileRequestContext input) {
        Subject subject;
        Set<UsernamePrincipal> usernamePrincipals;
        AuthenticationResult result;
        AuthenticationContext authnContext;
        if (input != null && (authnContext = (AuthenticationContext)input.getSubcontext(AuthenticationContext.class)) != null && (result = authnContext.getAuthenticationResult()) != null && (usernamePrincipals = (subject = result.getSubject()).getPrincipals(UsernamePrincipal.class)).size() == 1) {
            return usernamePrincipals.iterator().next().getName();
        }
        return null;
    }
}

