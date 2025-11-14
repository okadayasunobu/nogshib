/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.authn.context.AuthenticationErrorContext
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.opensaml.messaging.context.navigate.ContextDataLookupFunction
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.springframework.context.support.ApplicationObjectSupport
 *  org.springframework.context.support.MessageSourceAccessor
 */
package net.shibboleth.idp.plugin.authn.webauthn.context.navigate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.AuthenticationErrorContext;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.messaging.context.navigate.ContextDataLookupFunction;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.context.support.MessageSourceAccessor;

public class WebAuthnErrorMessageLookupFunction
extends ApplicationObjectSupport
implements ContextDataLookupFunction<ProfileRequestContext, String> {
    private String genericMessageID;

    public void setGenericMessageID(@Nullable String id) {
        this.genericMessageID = StringSupport.trimOrNull((String)id);
    }

    public String apply(@Nullable ProfileRequestContext input) {
        AuthenticationErrorContext errorCtx;
        MessageSourceAccessor messageSource = this.getMessageSourceAccessor();
        if (messageSource == null) {
            return null;
        }
        AuthenticationContext authCtx = input != null ? (AuthenticationContext)input.getSubcontext(AuthenticationContext.class) : null;
        AuthenticationErrorContext authenticationErrorContext = errorCtx = authCtx != null ? (AuthenticationErrorContext)authCtx.getSubcontext(AuthenticationErrorContext.class) : null;
        if (errorCtx == null) {
            return null;
        }
        String classifiedError = errorCtx.getLastClassifiedError();
        if (classifiedError != null && !classifiedError.isEmpty()) {
            return this.getClassifiedMessage(messageSource, classifiedError);
        }
        return null;
    }

    @Nullable
    private String getClassifiedMessage(@Nonnull MessageSourceAccessor messageSource, @Nonnull String classifiedError) {
        if (!"ReselectFlow".equals(classifiedError)) {
            String eventKey = messageSource.getMessage(classifiedError, this.genericMessageID != null ? this.genericMessageID : "authn");
            return messageSource.getMessage(eventKey + ".message", "Login Failure: " + classifiedError);
        }
        return null;
    }
}

