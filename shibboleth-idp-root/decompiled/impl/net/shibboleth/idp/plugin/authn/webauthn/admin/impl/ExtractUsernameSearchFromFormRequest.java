/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
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
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.context.WebAuthnManagementContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class ExtractUsernameSearchFromFormRequest
extends AbstractWebAuthnAction<WebAuthnManagementContext> {
    @Nonnull
    @NotEmpty
    public static final String DEFAULT_PARAMETER_NAME = "username_search";
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(ExtractUsernameSearchFromFormRequest.class);
    @NonnullAfterInit
    @NotEmpty
    private String requestParameterToExtract = "username_search";

    public ExtractUsernameSearchFromFormRequest() {
        super(new ChildContextLookup(WebAuthnManagementContext.class));
    }

    public void setRequestParameterName(@Nonnull @NotEmpty String parameter) {
        this.checkSetterPreconditions();
        this.requestParameterToExtract = (String)Constraint.isNotNull((Object)StringSupport.trimOrNull((String)parameter), (String)"Request parameter parameter cannot be null or empty");
    }

    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext, @Nonnull WebAuthnManagementContext context) {
        HttpServletRequest request = this.getHttpServletRequest();
        if (request == null) {
            this.log.debug("{} Profile action does not contain an HttpServletRequest", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"UnknownUsername");
            return;
        }
        String parameterValue = request.getParameter(this.requestParameterToExtract);
        if (parameterValue == null) {
            this.log.debug("{} '{}' not found in HTTP request", (Object)this.getLogPrefix(), (Object)this.requestParameterToExtract);
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"UnknownUsername");
            return;
        }
        this.log.trace("{} Extracting username '{}' to manage credentials for", (Object)this.getLogPrefix(), (Object)parameterValue);
        context.setSearchUsername(parameterValue);
    }
}

