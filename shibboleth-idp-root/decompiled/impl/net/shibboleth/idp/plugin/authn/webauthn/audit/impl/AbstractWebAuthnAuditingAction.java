/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.shibboleth.idp.authn.context.AuthenticationContext
 *  net.shibboleth.idp.profile.audit.impl.PopulateAuditContext
 *  net.shibboleth.idp.profile.audit.impl.WriteAuditLog
 *  net.shibboleth.profile.context.AuditContext
 *  net.shibboleth.shared.annotation.constraint.NotLive
 *  net.shibboleth.shared.annotation.constraint.Unmodifiable
 *  net.shibboleth.shared.logic.Constraint
 *  org.opensaml.messaging.context.BaseContext
 *  org.opensaml.messaging.context.navigate.ChildContextLookup
 *  org.opensaml.profile.context.EventContext
 *  org.opensaml.profile.context.ProfileRequestContext
 *  org.springframework.webflow.execution.Event
 *  org.springframework.webflow.execution.RequestContext
 */
package net.shibboleth.idp.plugin.authn.webauthn.audit.impl;

import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.plugin.authn.webauthn.impl.AbstractWebAuthnAction;
import net.shibboleth.idp.profile.audit.impl.PopulateAuditContext;
import net.shibboleth.idp.profile.audit.impl.WriteAuditLog;
import net.shibboleth.profile.context.AuditContext;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.logic.Constraint;
import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public abstract class AbstractWebAuthnAuditingAction<T>
extends AbstractWebAuthnAction<T> {
    @Nonnull
    private Function<ProfileRequestContext, AuditContext> auditContextCreationStrategy = new ChildContextLookup(AuditContext.class, true).compose((Function)new ChildContextLookup(AuthenticationContext.class));
    @Nullable
    private PopulateAuditContext populateAuditContextAction;
    @Nullable
    private WriteAuditLog writeAuditLogAction;
    @Nullable
    private RequestContext requestContext;

    protected AbstractWebAuthnAuditingAction(@Nonnull Function<ProfileRequestContext, T> defaultStrategy) {
        super(defaultStrategy);
    }

    public void setAuditContextCreationStrategy(@Nonnull Function<ProfileRequestContext, AuditContext> strategy) {
        this.checkSetterPreconditions();
        this.auditContextCreationStrategy = (Function)Constraint.isNotNull(strategy, (String)"AuditContext creation strategy cannot be null");
    }

    public void setPopulateAuditContextAction(@Nullable PopulateAuditContext action) {
        this.checkSetterPreconditions();
        this.populateAuditContextAction = action;
    }

    public void setWriteAuditLogAction(@Nullable WriteAuditLog action) {
        this.checkSetterPreconditions();
        this.writeAuditLogAction = action;
    }

    @Nullable
    protected AuditContext getAuditContext(@Nonnull ProfileRequestContext profileRequestContext) {
        return this.auditContextCreationStrategy.apply(profileRequestContext);
    }

    protected Event doExecute(@Nonnull RequestContext springRequestContext, @Nonnull ProfileRequestContext profileRequestContext) {
        this.requestContext = springRequestContext;
        return super.doExecute(springRequestContext, profileRequestContext);
    }

    protected void auditSuccess(@Nonnull ProfileRequestContext profileRequestContext, @Nullable String action) {
        this.doAudit(profileRequestContext, true, action);
    }

    protected void auditFailure(@Nonnull ProfileRequestContext profileRequestContext, @Nullable String action) {
        this.doAudit(profileRequestContext, false, action);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doAudit(@Nonnull ProfileRequestContext profileRequestContext, boolean success, @Nullable String action) {
        if (this.populateAuditContextAction != null && this.writeAuditLogAction != null) {
            EventContext existingEvent = (EventContext)profileRequestContext.getSubcontext(EventContext.class);
            try {
                assert (this.populateAuditContextAction != null);
                this.populateAuditContextAction.execute(this.requestContext);
                AuditContext ac = this.getAuditContext(profileRequestContext);
                if (ac != null) {
                    Map<String, String> fields = this.getAuditFields(profileRequestContext);
                    if (fields != null) {
                        for (Map.Entry<String, String> field : fields.entrySet()) {
                            String key = field.getKey();
                            assert (key != null);
                            ac.getFieldValues(key).add(field.getValue());
                        }
                    }
                    if (success) {
                        ac.getFields().put((Object)"WebAuthnAdminAO", (Object)"success");
                    } else {
                        ac.getFields().put((Object)"WebAuthnAdminAO", (Object)"failure");
                    }
                    if (action != null && !action.isEmpty()) {
                        ac.getFields().put((Object)"WebAuthnAdminAction", (Object)action);
                    }
                }
            }
            finally {
                if (existingEvent != null) {
                    profileRequestContext.addSubcontext((BaseContext)existingEvent);
                }
            }
            try {
                assert (this.writeAuditLogAction != null);
                this.writeAuditLogAction.execute(this.requestContext);
            }
            finally {
                if (existingEvent != null) {
                    profileRequestContext.addSubcontext((BaseContext)existingEvent);
                }
            }
        }
    }

    @Nullable
    @Unmodifiable
    @NotLive
    protected Map<String, String> getAuditFields(@Nonnull ProfileRequestContext profileRequestContext) {
        return null;
    }
}

