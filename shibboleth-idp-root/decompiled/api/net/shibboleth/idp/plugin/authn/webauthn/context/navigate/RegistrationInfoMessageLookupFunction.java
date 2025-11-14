/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.NotThreadSafe
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.messaging.context.navigate.ContextDataLookupFunction
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.springframework.context.support.ApplicationObjectSupport
 *  org.springframework.context.support.MessageSourceAccessor
 */
package net.shibboleth.idp.plugin.authn.webauthn.context.navigate;

import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationContext;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnRegistrationInformationContext;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.context.navigate.ContextDataLookupFunction;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.context.support.MessageSourceAccessor;

@NotThreadSafe
public class RegistrationInfoMessageLookupFunction
extends ApplicationObjectSupport
implements ContextDataLookupFunction<ProfileRequestContext, String> {
    private String genericMessageID;
    @Nonnull
    private Function<ProfileRequestContext, WebAuthnRegistrationInformationContext> webauthnInfoContextLookupStrategy = new ChildContextLookup(WebAuthnRegistrationInformationContext.class).compose((Function)new ChildContextLookup(WebAuthnRegistrationContext.class));

    public void setWebauthnInfoContextLookupStrategy(Function<ProfileRequestContext, WebAuthnRegistrationInformationContext> strategy) {
        this.webauthnInfoContextLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"WebauthnInfoContextLookupStrategy can not be null");
    }

    public void setGenericMessageID(@Nullable String id) {
        this.genericMessageID = StringSupport.trimOrNull((String)id);
    }

    public String apply(@Nullable ProfileRequestContext input) {
        MessageSourceAccessor messageSource = this.getMessageSourceAccessor();
        if (messageSource == null) {
            return null;
        }
        WebAuthnRegistrationInformationContext regInfoCtx = this.webauthnInfoContextLookupStrategy.apply(input);
        if (regInfoCtx == null) {
            return null;
        }
        String classifiedError = regInfoCtx.getLastClassifiedMessage();
        if (classifiedError != null && !classifiedError.isEmpty()) {
            return this.getClassifiedMessage(messageSource, classifiedError);
        }
        return null;
    }

    @Nullable
    private String getClassifiedMessage(@Nonnull MessageSourceAccessor messageSource, @Nonnull String classifiedMessage) {
        String message = messageSource.getMessage(classifiedMessage, "");
        if (message.isEmpty()) {
            message = messageSource.getMessage(this.genericMessageID != null ? this.genericMessageID : "idp.webauthn.register.message", "Registration result: " + classifiedMessage);
        }
        if (message.isEmpty()) {
            return null;
        }
        return message;
    }
}

