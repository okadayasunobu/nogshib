/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.profile.AbstractProfileAction
 *  net.shibboleth.shared.annotation.constraint.NonnullAfterInit
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.codec.Base64Support
 *  net.shibboleth.shared.codec.DecodingException
 *  net.shibboleth.shared.component.ComponentInitializationException
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  net.shibboleth.shared.primitive.StringSupport
 *  org.opensaml.profile.action.ActionSupport
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import jakarta.servlet.http.HttpServletRequest;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.DecodingException;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class ExtractKeyInformationFromFormRequest
extends AbstractProfileAction {
    @Nonnull
    @NotEmpty
    public static final String DEFAULT_PARAMETER_NAME = "credentialId";
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(ExtractKeyInformationFromFormRequest.class);
    @Nonnull
    @NotEmpty
    private String credentialIdParameterName = "credentialId";
    @NonnullAfterInit
    private BiConsumer<ProfileRequestContext, byte[]> contextSettingConsumer;

    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (this.contextSettingConsumer == null) {
            throw new ComponentInitializationException("ContextSettingConsumer can not be null");
        }
    }

    public void setContextSettingConsumer(BiConsumer<ProfileRequestContext, byte[]> consumer) {
        this.checkSetterPreconditions();
        this.contextSettingConsumer = (BiConsumer)Constraint.isNotNull(consumer, (String)"ContextSettingConsumer can not be null");
    }

    public void setCedentialIdParameterName(@Nonnull @NotEmpty String field) {
        this.checkSetterPreconditions();
        this.credentialIdParameterName = (String)Constraint.isNotNull((Object)StringSupport.trimOrNull((String)field), (String)"Credential ID parameter cannot be null or empty");
    }

    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext) {
        HttpServletRequest request = this.getHttpServletRequest();
        if (request == null) {
            this.log.debug("{} Profile action does not contain an HttpServletRequest", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidProfileContext");
            return;
        }
        String credentialId = request.getParameter(this.credentialIdParameterName);
        if (credentialId == null) {
            this.log.debug("{} CredentialID not found in HTTP request", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidAdminAction");
            return;
        }
        try {
            byte[] credentialIdAsBytes = Base64Support.decode((String)credentialId);
            this.contextSettingConsumer.accept(profileRequestContext, credentialIdAsBytes);
            this.log.trace("{} Credential to remove '{}'", (Object)this.getLogPrefix(), (Object)credentialId);
        }
        catch (DecodingException e) {
            this.log.debug("{} Unable to base64 decode credentialID, can not set credential", (Object)this.getLogPrefix());
            ActionSupport.buildEvent((ProfileRequestContext)profileRequestContext, (String)"InvalidAdminAction");
            return;
        }
    }
}

