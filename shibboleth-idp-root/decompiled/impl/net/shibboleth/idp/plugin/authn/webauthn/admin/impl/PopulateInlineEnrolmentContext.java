/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.servlet.http.HttpServletRequest
 *  javax.annotation.Nonnull
 *  net.shibboleth.idp.plugin.authn.webauthn.context.InlineEnrolmentContext
 *  net.shibboleth.idp.profile.AbstractProfileAction
 *  net.shibboleth.shared.annotation.constraint.NotEmpty
 *  net.shibboleth.shared.logic.Constraint
 *  net.shibboleth.shared.logic.PredicateSupport
 *  net.shibboleth.shared.primitive.LoggerFactory
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.slf4j.Logger
 */
package net.shibboleth.idp.plugin.authn.webauthn.admin.impl;

import jakarta.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.shibboleth.idp.plugin.authn.webauthn.context.InlineEnrolmentContext;
import net.shibboleth.idp.profile.AbstractProfileAction;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

public class PopulateInlineEnrolmentContext
extends AbstractProfileAction {
    @Nonnull
    @NotEmpty
    private static final String REG_QUERY_PARAM = "reg";
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(PopulateInlineEnrolmentContext.class);
    private Predicate<ProfileRequestContext> enabled;
    @Nonnull
    private Function<ProfileRequestContext, InlineEnrolmentContext> inlineEnrolementContextCreationStrategy = new ChildContextLookup(InlineEnrolmentContext.class, true);

    public PopulateInlineEnrolmentContext() {
        this.enabled = PredicateSupport.alwaysTrue();
    }

    public void setEnabled(@Nonnull Predicate<ProfileRequestContext> predicate) {
        this.checkSetterPreconditions();
        this.enabled = (Predicate)Constraint.isNotNull(this.enabled, (String)"Inline enrolment predicate can not be null");
    }

    public void setEnabled(boolean flag) {
        this.checkSetterPreconditions();
        this.enabled = flag ? PredicateSupport.alwaysTrue() : PredicateSupport.alwaysFalse();
    }

    public void setInlineEnrolementContextCreationStrategy(@Nonnull Function<ProfileRequestContext, InlineEnrolmentContext> strategy) {
        this.checkSetterPreconditions();
        this.inlineEnrolementContextCreationStrategy = (Function)Constraint.isNotNull(strategy, (String)"InlineEnrolementContextCreationStrategy can not be null");
    }

    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext) {
        if (!this.enabled.test(profileRequestContext)) {
            return;
        }
        HttpServletRequest request = this.getHttpServletRequest();
        if (request == null) {
            this.log.trace("{} Unable to set inline enrolment URL, no HTTP request set", (Object)this.getLogPrefix());
            return;
        }
        String registrationQueryParam = request.getParameter(REG_QUERY_PARAM);
        if (registrationQueryParam != null && "inline".equals(registrationQueryParam)) {
            String referer = request.getHeader("Referer");
            InlineEnrolmentContext inlineCtx = this.inlineEnrolementContextCreationStrategy.apply(profileRequestContext);
            try {
                inlineCtx.setSsoUrl(new URL(referer));
            }
            catch (MalformedURLException e) {
                this.log.trace("{} Unable to set inline enrolment URL '{}'", (Object)this.getLogPrefix(), (Object)referer);
                return;
            }
            this.log.debug("{} Registration is inline '{}'", (Object)this.getLogPrefix(), (Object)referer);
        }
    }
}

