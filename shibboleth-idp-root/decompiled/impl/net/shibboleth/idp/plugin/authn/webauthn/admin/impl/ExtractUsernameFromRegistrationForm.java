/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext
 *  net.shibboleth.shared.annotation.constraint.NonnullBeforeExec
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.plugin.authn.webauthn.context.BaseWebAuthnContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnExtractionAction;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class ExtractUsernameFromRegistrationForm
extends AbstractWebAuthnExtractionAction {
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(ExtractUsernameFromRegistrationForm.class);
    @Nonnull
    private Function<ProfileRequestContext, BaseWebAuthnContext> webAuthnContextLookupStrategy = new ChildContextLookup(BaseWebAuthnContext.class).compose((Function)new ChildContextLookup(ProfileRequestContext.class));
    @Nonnull
    @NotEmpty
    private String usernameFieldName = "j_username";
    @NonnullBeforeExec
    private BaseWebAuthnContext webAuthnContext;

    public void setWebAuthnContextLookupStrategy(@Nonnull Function<ProfileRequestContext, BaseWebAuthnContext> strategy) {
        this.checkSetterPreconditions();
        this.webAuthnContextLookupStrategy = (Function)Constraint.isNotNull(strategy, (String)"WebAuthnContext lookup strategy cannot be null");
    }

    public void setUsernameFieldName(@Nonnull String name) {
        this.checkSetterPreconditions();
        this.usernameFieldName = (String)Constraint.isNotNull((Object)StringSupport.trimOrNull((String)name), (String)"Username form field name cannot be null or empty");
    }

    protected boolean doPreExecute(@Nonnull ProfileRequestContext profileRequestContext) {
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        this.webAuthnContext = this.webAuthnContextLookupStrategy.apply(profileRequestContext);
        if (this.webAuthnContext == null) {
            this.log.debug("{} No WebAuthnContext found, nothing to do", (Object)this.getLogPrefix());
            return false;
        }
        return true;
    }

    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext) {
        String username = this.processUsername(profileRequestContext);
        if (username == null) {
            this.log.warn("{} Unable to find username in HTTP request", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"UnknownUsername");
            return;
        }
        this.log.trace("{} Populating username '{}' from form", (Object)this.getLogPrefix(), (Object)username);
        this.webAuthnContext.setUsername(username);
        this.webAuthnContext.setRawUsername(this.getUsernameFromForm());
    }

    @Nullable
    private String getUsernameFromForm() {
        HttpServletRequest request = this.getHttpServletRequest();
        if (request != null) {
            return request.getParameter(this.usernameFieldName);
        }
        return null;
    }

    @Nullable
    private String processUsername(@Nonnull ProfileRequestContext profileRequestContext) {
        String username = this.getUsernameFromForm();
        if (username != null) {
            return this.applyTransforms(username);
        }
        return null;
    }
}

